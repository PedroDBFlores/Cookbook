import React, {useEffect} from "react"
import {render, screen} from "@testing-library/react"
import RecipeDetails from "./details"
import createRecipeService from "../../../services/recipe-service"
import Modal from "../../../components/modal/modal"
import {generateRecipeDetails} from "../../../../tests/helpers/generators/dto-generators"
import {WrapperWithRoutes, WrapWithCommonContexts} from "../../../../tests/render-helpers"
import userEvent from "@testing-library/user-event"

jest.mock("../../../../src/services/recipe-service")
const createRecipeServiceMock = createRecipeService as jest.MockedFunction<typeof createRecipeService>

jest.mock("../../../../src/components/modal/modal", () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(() => <div>Delete Recipe Modal</div>)
    }
})
const basicModalDialogMock = Modal as jest.MockedFunction<typeof Modal>

describe("Recipe details component", () => {
    const findRecipeMock = jest.fn()
    const deleteRecipeMock = jest.fn()

    createRecipeServiceMock.mockImplementation(() => {
        return {
            getAll: jest.fn(),
            search: jest.fn(),
            update: jest.fn(),
            find: findRecipeMock,
            delete: deleteRecipeMock,
            create: jest.fn()
        }
    })

    beforeEach(() => jest.clearAllMocks())

    it("renders the recipe details component", async () => {
        const expectedRecipe = {...generateRecipeDetails(), id: 99}
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")
        findRecipeMock.mockResolvedValueOnce(expectedRecipe)

        render(<WrapWithCommonContexts apiHandler={apiHandlerMock}>
            <RecipeDetails id={99}/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(findRecipeMock).toHaveBeenCalledWith(99)
        expect(await screen.findByText(/^recipe details$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Id$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Name$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Description$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Ingredients$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Preparing steps$/i)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.id.toString())).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.name)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.description)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.ingredients)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.preparingSteps)).toBeInTheDocument()
    })

    it("renders an error if the recipe cannot be obtained", async () => {
        findRecipeMock.mockRejectedValueOnce(new Error("failure"))

        render(<WrapWithCommonContexts>
            <RecipeDetails id={99}/>
        </WrapWithCommonContexts>)

        expect(await screen.findByText(/^an error occurred while fetching the recipe$/i)).toBeInTheDocument()
        expect(await screen.findByText(/failure/i)).toBeInTheDocument()
        expect(await screen.findByText(/^failed to fetch the recipe$/i)).toBeInTheDocument()
    })

    describe("Actions", () => {

        it("takes the user to the edit recipe page", async () => {
            const expectedRecipe = generateRecipeDetails()
            findRecipeMock.mockResolvedValueOnce(expectedRecipe)
            render(<WrapWithCommonContexts>
                <WrapperWithRoutes initialPath={`/recipe/${expectedRecipe.id}/details`} routeConfiguration={[
                    {
                        path: `/recipe/${expectedRecipe.id}/details`,
                        exact: true,
                        component: () => <RecipeDetails id={expectedRecipe.id}/>
                    },
                    {
                        path: `/recipe/${expectedRecipe.id}/edit`,
                        exact: true,
                        component: () => <>I'm the recipe edit page</>
                    }
                ]}/>
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Edit recipe '${expectedRecipe.name}'`))

            expect(screen.getByText(/I'm the recipe edit page/i)).toBeInTheDocument()
        })

        it("deletes the recipe", async () => {
            const expectedRecipe = generateRecipeDetails()
            findRecipeMock.mockResolvedValueOnce(expectedRecipe)
            deleteRecipeMock.mockResolvedValueOnce({})
            basicModalDialogMock.mockImplementationOnce(({content, onAction}) => {
                useEffect(() => onAction(), [])
                return <div>{content}</div>
            })

            render(<WrapWithCommonContexts>
                <WrapperWithRoutes initialPath={`/recipe/${expectedRecipe.id}/details`} routeConfiguration={[
                    {
                        path: `/recipe/${expectedRecipe.id}/details`,
                        exact: true,
                        component: () => <RecipeDetails id={expectedRecipe.id}/>
                    },
                    {
                        path: "/recipe",
                        exact: true,
                        component: () => <>I'm the recipe search page</>
                    }
                ]}/>
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Delete recipe '${expectedRecipe.name}'`))
            expect(screen.getByText(/are you sure you want to delete this recipe?/i)).toBeInTheDocument()

            expect(deleteRecipeMock).toHaveBeenCalledWith(expectedRecipe.id)
            expect(await screen.findByText(`Recipe '${expectedRecipe.name}' was deleted`)).toBeInTheDocument()
            expect(await screen.findByText(/I'm the recipe search page/i)).toBeInTheDocument()
        })

        it("shows an error if deleting the recipe fails", async () => {
            const expectedRecipe = generateRecipeDetails()
            findRecipeMock.mockResolvedValueOnce(expectedRecipe)
            deleteRecipeMock.mockRejectedValueOnce({message: "Something went wrong"})
            basicModalDialogMock.mockImplementationOnce(({content, onAction}) => {
                useEffect(() => onAction(), [])
                return <div>{content}</div>
            })

            render(<WrapWithCommonContexts>
                <RecipeDetails id={expectedRecipe.id}/>
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(`Delete recipe '${expectedRecipe.name}'`))
            expect(screen.getByText(/are you sure you want to delete this recipe?/i)).toBeInTheDocument()

            expect(await screen.findByText(/^an error occurred while trying to delete this recipe$/i)).toBeInTheDocument()
            expect(await screen.findByText(/^something went wrong$/i)).toBeInTheDocument()
        })
    })
})