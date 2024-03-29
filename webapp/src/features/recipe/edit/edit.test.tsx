import React from "react"
import { render, screen } from "@testing-library/react"
import createRecipeService, { RecipeDetails } from "services/recipe-service"
import createRecipeTypeService, { RecipeType } from "services/recipe-type-service"
import { WrapperWithRouter, WrapperWithRoutes, WrapWithCommonContexts } from "../../../../tests/render-helpers"
import userEvent from "@testing-library/user-event"
import EditRecipe from "./edit"
import "jest-chain"

jest.mock("services/recipe-type-service")
jest.mock("services/recipe-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>
const createRecipeServiceMock = createRecipeService as jest.MockedFunction<typeof createRecipeService>

jest.mock("components/recipe-form/recipe-form", () => ({
    __esModule: true,
    default: jest.fn().mockImplementation(({ onSubmit }) => <>
        <p>Edit recipe form</p>
        <button aria-label="Edit recipe"
            onClick={() => onSubmit({
                id: 1,
                recipeTypeId: 1,
                name: "Name",
                description: "Description",
                ingredients: "Ingredients",
                preparingSteps: "PreparingSteps"
            })}>
            Edit
        </button>
    </>)
}))

describe("Edit recipe component", () => {
    const findRecipeMock = jest.fn().mockImplementation(() => Promise.resolve({
        id: 1,
        recipeTypeId: 1,
        name: "A great",
        description: "recipe for winter times",
        ingredients: "A lot of love",
        preparingSteps: "and care"
    } as RecipeDetails))
    const updateRecipeMock = jest.fn()
    const getRecipeTypesMock = jest.fn().mockImplementation(() =>
        Promise.resolve([
            { id: 1, name: "ABC" }
        ] as Array<RecipeType>))

    createRecipeTypeServiceMock.mockImplementation(() => ({
        getAll: getRecipeTypesMock,
        update: jest.fn(),
        find: jest.fn(),
        delete: jest.fn(),
        create: jest.fn()
    }))
    createRecipeServiceMock.mockImplementation(() => ({
        create: jest.fn(),
        search: jest.fn(),
        find: findRecipeMock,
        delete: jest.fn(),
        update: updateRecipeMock,
        getAll: jest.fn()
    }))

    it("renders the initial layout", async () => {
        render(<WrapWithCommonContexts>
            <WrapperWithRouter initialPath="/recipe/1/edit" elementRoute="/recipe/:id/edit">
                <EditRecipe />
            </WrapperWithRouter>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/^translated recipe-feature.edit.title$/i)).toBeInTheDocument()
        expect(screen.getByText(/translated common.loading/i)).toBeInTheDocument()
        expect(await screen.findByText(/Edit recipe form/i)).toBeInTheDocument()
        expect(findRecipeMock).toHaveBeenCalledWith(1)
        expect(getRecipeTypesMock).toHaveBeenCalled()
    })

    describe("Error rendering", () => {
        it("renders an error if it fails to get the recipe types", async () => {
            getRecipeTypesMock.mockRejectedValueOnce(new Error("Failed to fetch recipe types"))

            render(<WrapWithCommonContexts>
                <WrapperWithRouter initialPath="/recipe/1/edit" elementRoute="/recipe/:id/edit">
                    <EditRecipe />
                </WrapperWithRouter>
            </WrapWithCommonContexts>)

            expect(getRecipeTypesMock).toHaveBeenCalled()
            expect(await screen.findByText(/^translated recipe-type-feature.errors.occurred-fetching$/i)).toBeInTheDocument()
            expect(await screen.findByText(/^failed to fetch recipe types$/i)).toBeInTheDocument()
            expect(await screen.findByText(/^translated recipe-type-feature.errors.cannot-load$/i)).toBeInTheDocument()
            expect(findRecipeMock).not.toHaveBeenCalled()
        })

        it("renders an error if it fails to get the recipe", async () => {
            findRecipeMock.mockRejectedValueOnce(new Error("Failed to fetch recipe"))

            render(<WrapWithCommonContexts>
                <WrapperWithRouter initialPath="/recipe/1/edit" elementRoute="/recipe/:id/edit">
                    <EditRecipe />
                </WrapperWithRouter>
            </WrapWithCommonContexts>)

            expect(await screen.findByText(/^translated recipe-feature.errors.occurred-fetching$/i)).toBeInTheDocument()
            expect(await screen.findByText(/^failed to fetch recipe$/i)).toBeInTheDocument()
            expect(await screen.findByText(/^translated recipe-type-feature.errors.cannot-load$/i)).toBeInTheDocument()
        })
    })

    it("calls the 'updateRecipe' function on submit", async () => {
        updateRecipeMock.mockResolvedValueOnce({})
        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipe/1/edit" routeConfiguration={[
                { path: "/recipe/:id/edit", element: <EditRecipe /> },
                { path: "/recipe/:id", element: <>I'm the recipe details page for id 1</> }
            ]} />
        </WrapWithCommonContexts>)
        expect(await screen.findByText(/^translated recipe-feature.edit.title$/i)).toBeInTheDocument()
        await screen.findByText(/Edit recipe form/i)

        await userEvent.click(screen.getByLabelText(/edit recipe/i))

        expect(await screen.findByText(`translated recipe-feature.update.success #Name#`)).toBeInTheDocument()
        expect(await screen.findByText(/i'm the recipe details page for id 1/i)).toBeInTheDocument()
        expect(updateRecipeMock).toHaveBeenCalledWith({
            id: 1,
            recipeTypeId: 1,
            name: "Name",
            description: "Description",
            ingredients: "Ingredients",
            preparingSteps: "PreparingSteps"
        })
    })

    it("shows an error message if the update API call fails", async () => {
        updateRecipeMock.mockRejectedValueOnce({ message: "A wild error has appeared" })
        render(<WrapWithCommonContexts>
            <WrapperWithRouter initialPath="/recipe/1/edit" elementRoute="/recipe/:id/edit">
                <EditRecipe />
            </WrapperWithRouter>
        </WrapWithCommonContexts>)
        expect(await screen.findByText(/^translated recipe-feature.edit.title$/i)).toBeInTheDocument()
        await screen.findByText(/Edit recipe form/i)

        await userEvent.click(screen.getByLabelText(/edit recipe/i))

        expect(await screen.findByText(`translated recipe-feature.update.failure`)).toBeInTheDocument()
        expect(await screen.findByText(/^A wild error has appeared$/i)).toBeInTheDocument()
        expect(updateRecipeMock).toHaveBeenCalledWith({
            id: 1,
            recipeTypeId: 1,
            name: "Name",
            description: "Description",
            ingredients: "Ingredients",
            preparingSteps: "PreparingSteps"
        })
    })
})
