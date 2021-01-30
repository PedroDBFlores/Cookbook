import React, { useEffect } from "react"
import { render, screen } from "@testing-library/react"
import RecipeDetails from "./details"
import createRecipeService, { RecipeDetails as RecipeDetailsModel } from "services/recipe-service"
import Modal from "components/modal/modal"
import { WrapperWithRoutes, WrapWithCommonContexts } from "../../../../tests/render-helpers"
import userEvent from "@testing-library/user-event"

jest.mock("services/recipe-service")
const createRecipeServiceMock = createRecipeService as jest.MockedFunction<typeof createRecipeService>

jest.mock("components/modal/modal", () => ({
    __esModule: true,
    default: jest.fn().mockImplementation(() => <div>Delete Recipe Modal</div>)
}))
const basicModalDialogMock = Modal as jest.MockedFunction<typeof Modal>

describe("Recipe details component", () => {
    const baseRecipe: RecipeDetailsModel = {
        id: 123,
        recipeTypeId: 456,
        name: "Roasted sweet potato",
        description: "This is the beast roasted sweet potato you'll ever eat",
        recipeTypeName: "Dessert",
        ingredients: "1 mid sized sweet Potato",
        preparingSteps: "Take potato to oven for 45 minutes to 1 hour"
    }

    const findRecipeMock = jest.fn()
    const deleteRecipeMock = jest.fn()

    createRecipeServiceMock.mockImplementation(() => ({
        getAll: jest.fn(),
        search: jest.fn(),
        update: jest.fn(),
        find: findRecipeMock,
        delete: deleteRecipeMock,
        create: jest.fn()
    }))

    beforeEach(jest.clearAllMocks)

    it("renders the recipe details component", async() => {
        findRecipeMock.mockResolvedValueOnce(baseRecipe)

        render(<WrapWithCommonContexts>
            <RecipeDetails id={123} />
        </WrapWithCommonContexts>)

        expect(screen.getByText(/^recipe details$/i)).toBeInTheDocument()
        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(findRecipeMock).toHaveBeenCalledWith(123)
        expect(await screen.findByText(/^Id$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Name$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Description$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Ingredients$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Preparing steps$/i)).toBeInTheDocument()
        expect(await screen.findByText(baseRecipe.id.toString())).toBeInTheDocument()
        expect(await screen.findByText(baseRecipe.name)).toBeInTheDocument()
        expect(await screen.findByText(baseRecipe.description)).toBeInTheDocument()
        expect(await screen.findByText(baseRecipe.ingredients)).toBeInTheDocument()
        expect(await screen.findByText(baseRecipe.preparingSteps)).toBeInTheDocument()
    })

    it("renders an error if the recipe cannot be obtained", async() => {
        findRecipeMock.mockRejectedValueOnce(new Error("failure"))

        render(<WrapWithCommonContexts>
            <RecipeDetails id={123} />
        </WrapWithCommonContexts>)

        expect(await screen.findByText(/^an error occurred while fetching the recipe$/i)).toBeInTheDocument()
        expect(await screen.findByText(/failure/i)).toBeInTheDocument()
        expect(await screen.findByText(/^failed to fetch the recipe$/i)).toBeInTheDocument()
    })

    describe("Actions", () => {

        it("takes the user to the edit recipe page", async() => {
            findRecipeMock.mockResolvedValueOnce(baseRecipe)
            render(<WrapWithCommonContexts>
                <WrapperWithRoutes initialPath={`/recipe/${baseRecipe.id}/details`} routeConfiguration={[
                    {
                        path: `/recipe/${baseRecipe.id}/details`,
                        exact: true,
                        component: () => <RecipeDetails id={baseRecipe.id} />
                    },
                    {
                        path: `/recipe/${baseRecipe.id}/edit`,
                        exact: true,
                        component: () => <>I'm the recipe edit page</>
                    }
                ]} />
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Edit recipe '${baseRecipe.name}'`))

            expect(screen.getByText(/I'm the recipe edit page/i)).toBeInTheDocument()
        })

        it("deletes the recipe", async() => {
            findRecipeMock.mockResolvedValueOnce(baseRecipe)
            deleteRecipeMock.mockResolvedValueOnce({})
            basicModalDialogMock.mockImplementationOnce(({ content, onAction }) => {
                useEffect(() => onAction(), [])
                return <div>{content}</div>
            })

            render(<WrapWithCommonContexts>
                <WrapperWithRoutes initialPath={`/recipe/${baseRecipe.id}/details`} routeConfiguration={[
                    {
                        path: `/recipe/${baseRecipe.id}/details`,
                        exact: true,
                        component: () => <RecipeDetails id={baseRecipe.id} />
                    },
                    {
                        path: "/recipe",
                        exact: true,
                        component: () => <>I'm the recipe search page</>
                    }
                ]} />
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Delete recipe '${baseRecipe.name}'`))
            expect(screen.getByText(/are you sure you want to delete this recipe?/i)).toBeInTheDocument()

            expect(deleteRecipeMock).toHaveBeenCalledWith(baseRecipe.id)
            expect(await screen.findByText(`Recipe '${baseRecipe.name}' was deleted`)).toBeInTheDocument()
            expect(await screen.findByText(/I'm the recipe search page/i)).toBeInTheDocument()
        })

        it("shows an error if deleting the recipe fails", async() => {
            findRecipeMock.mockResolvedValueOnce(baseRecipe)
            deleteRecipeMock.mockRejectedValueOnce({ message: "Something went wrong" })
            basicModalDialogMock.mockImplementationOnce(({ content, onAction }) => {
                useEffect(() => onAction(), [])
                return <div>{content}</div>
            })

            render(<WrapWithCommonContexts>
                <RecipeDetails id={baseRecipe.id} />
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Delete recipe '${baseRecipe.name}'`))
            expect(screen.getByText(/are you sure you want to delete this recipe?/i)).toBeInTheDocument()

            expect(await screen.findByText(/^an error occurred while trying to delete this recipe$/i)).toBeInTheDocument()
            expect(await screen.findByText(/^something went wrong$/i)).toBeInTheDocument()
        })
    })
})
