import React from "react"
import { render, screen } from "@testing-library/react"
import CreateRecipe from "./create"
import createRecipeService from "services/recipe-service"
import createRecipeTypeService, { RecipeType } from "services/recipe-type-service"
import { WrapperWithRouter, WrapperWithRoutes, WrapWithCommonContexts } from "../../../../tests/render-helpers"
import userEvent from "@testing-library/user-event"

jest.mock("services/recipe-type-service")
jest.mock("services/recipe-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>
const createRecipeServiceMock = createRecipeService as jest.MockedFunction<typeof createRecipeService>

jest.mock("components/recipe-form/recipe-form", () => ({
    __esModule: true,
    default: jest.fn().mockImplementation(({ onSubmit }) =>
        <>
            <p>Create recipe form</p>
            <button aria-label="Create recipe"
                onClick={() => onSubmit({
                    recipeTypeId: 1,
                    name: "Name",
                    description: "Description",
                    ingredients: "Ingredients",
                    preparingSteps: "PreparingSteps"
                })}>
                Create
            </button>
        </>
    )
}))

describe("Create recipe component", () => {
    const createRecipeMock = jest.fn()
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
        create: createRecipeMock,
        search: jest.fn(),
        find: jest.fn(),
        delete: jest.fn(),
        update: jest.fn(),
        getAll: jest.fn()
    }))

    beforeEach(() => getRecipeTypesMock.mockClear())

    it("renders the initial form", async () => {
        render(<WrapperWithRouter>
            <CreateRecipe />
        </WrapperWithRouter>)

        expect(getRecipeTypesMock).toHaveBeenCalled()
        expect(screen.getByText(/translated recipe-feature.create-label/i)).toBeInTheDocument()
        expect(screen.getByText(/translated common.loading/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Create recipe form$/i)).toBeInTheDocument()
    })

    it("render an error if the recipe types cannot be obtained", async () => {
        getRecipeTypesMock.mockRejectedValueOnce(new Error("Failed to fetch"))
        render(
            <WrapWithCommonContexts>
                <WrapperWithRouter>
                    <CreateRecipe />
                </WrapperWithRouter>
            </WrapWithCommonContexts>
        )

        expect(getRecipeTypesMock).toHaveBeenCalled()
        expect(await screen.findByText(/^translated recipe-feature.errors.occurred-fetching$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^failed to fetch$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^translated recipe-feature.errors.cannot-load$/i)).toBeInTheDocument()
    })


    it("calls the 'createRecipe' function on submit", async () => {
        createRecipeMock.mockResolvedValueOnce({ id: 1 })
        render(
            <WrapWithCommonContexts>
                <WrapperWithRoutes initialPath="/recipe/new" routeConfiguration={[
                    { path: "/recipe/new", element: <CreateRecipe /> },
                    { path: "/recipe/1/details", element: <>I'm the recipe details page for id 1</> }
                ]} />
            </WrapWithCommonContexts>
        )
        await screen.findByText(/^Create recipe form$/i)

        await userEvent.click(screen.getByLabelText(/create recipe/i))

        expect(await screen.findByText(/^translated recipe-feature.create.success #Name#$/i)).toBeInTheDocument()
        expect(await screen.findByText(/i'm the recipe details page for id 1/i)).toBeInTheDocument()
        expect(createRecipeMock).toHaveBeenCalledWith({
            recipeTypeId: 1,
            name: "Name",
            description: "Description",
            ingredients: "Ingredients",
            preparingSteps: "PreparingSteps"
        })
    })

    it("shows an error message if the create API call fails", async () => {
        createRecipeMock.mockRejectedValueOnce({ message: "A wild error has appeared" })
        render(<WrapWithCommonContexts>
            <WrapperWithRouter>
                <CreateRecipe />
            </WrapperWithRouter>
        </WrapWithCommonContexts>)

        await screen.findByText(/^Create recipe form$/i)

        await userEvent.click(screen.getByLabelText(/create recipe/i))

        expect(await screen.findByText(/^translated recipe-type-feature.create.failure$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^a wild error has appeared$/i)).toBeInTheDocument()
        expect(createRecipeMock).toHaveBeenCalledWith({
            recipeTypeId: 1,
            name: "Name",
            description: "Description",
            ingredients: "Ingredients",
            preparingSteps: "PreparingSteps"
        })
    })
})
