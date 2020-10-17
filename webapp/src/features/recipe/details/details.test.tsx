import React, {useEffect} from "react"
import {screen} from "@testing-library/react"
import RecipeDetails from "./details"
import createRecipeService from "../../../services/recipe-service"
import BasicModalDialog from "../../../components/modal/basic-modal-dialog"
import {generateRecipeDetails} from "../../../../tests/helpers/generators/dto-generators"
import {renderWithRoutes, renderWrappedInCommonContexts} from "../../../../tests/render"
import userEvent from "@testing-library/user-event"

jest.mock("../../../../src/services/recipe-service")
const createRecipeServiceMock = createRecipeService as jest.MockedFunction<typeof createRecipeService>

jest.mock("../../../../src/components/modal/basic-modal-dialog", () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(() => <div>Delete Recipe Modal</div>)
    }
})
const basicModalDialogMock = BasicModalDialog as jest.MockedFunction<typeof BasicModalDialog>

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

        renderWrappedInCommonContexts(<RecipeDetails id={99}/>, apiHandlerMock)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(createRecipeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(findRecipeMock).toHaveBeenCalledWith(99)
        expect(await screen.findByText(/^recipe details$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Id:$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Name:$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Description:$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Ingredients:$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^Preparing steps:$/i)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.id.toString())).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.name)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.description)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.ingredients)).toBeInTheDocument()
        expect(await screen.findByText(expectedRecipe.preparingSteps)).toBeInTheDocument()
    })

    it("renders an error if the recipe cannot be obtained", async () => {
        findRecipeMock.mockRejectedValueOnce({message: "Failure"})

        renderWrappedInCommonContexts(<RecipeDetails id={99}/>)

        expect(await screen.findByText(/failure/i)).toBeInTheDocument()
        expect(findRecipeMock).toHaveBeenCalled()
    })

    describe("Actions", () => {

        it("takes the user to the edit recipe page", async () => {
            const expectedRecipe = generateRecipeDetails()
            findRecipeMock.mockResolvedValueOnce(expectedRecipe)
            renderWithRoutes({
                [`/recipe/${expectedRecipe.id}/details`]: () => <RecipeDetails id={expectedRecipe.id}/>,
                [`/recipe/${expectedRecipe.id}/edit`]: () => <div>I'm the recipe edit page</div>
            }, `/recipe/${expectedRecipe.id}/details`)

            userEvent.click(await screen.findByLabelText(`Edit recipe with id ${expectedRecipe.id}`))

            expect(screen.getByText(/I'm the recipe edit page/i)).toBeInTheDocument()
        })

        it("deletes the recipe", async () => {
            const expectedRecipe = generateRecipeDetails()
            findRecipeMock.mockResolvedValueOnce(expectedRecipe)
            deleteRecipeMock.mockResolvedValueOnce({})
            basicModalDialogMock.mockImplementationOnce(({dismiss}) => {
                useEffect(() => {
                    dismiss.onDismiss()
                }, [])
                return <div>Are you sure you want to delete this recipe?</div>
            })

            renderWithRoutes({
                [`/recipe/${expectedRecipe.id}/details`]: () => <RecipeDetails id={expectedRecipe.id}/>,
                "/recipe": () => <div>I'm the recipe search page</div>
            }, `/recipe/${expectedRecipe.id}/details`)

            userEvent.click(await screen.findByLabelText(`Delete recipe with id ${expectedRecipe.id}`))

            expect(basicModalDialogMock).toHaveBeenCalled()
            expect(screen.getByText(/are you sure you want to delete this recipe?/i)).toBeInTheDocument()
            expect(deleteRecipeMock).toHaveBeenCalledWith(expectedRecipe.id)
            expect(await screen.findByText(`Recipe ${expectedRecipe.id} was deleted`)).toBeInTheDocument()
            expect(await screen.findByText(/I'm the recipe search page/i)).toBeInTheDocument()
        })

        it("shows an error if deleting the recipe fails", async () => {
            const expectedRecipe = generateRecipeDetails()
            findRecipeMock.mockResolvedValueOnce(expectedRecipe)
            deleteRecipeMock.mockRejectedValueOnce({message: "Something went wrong"})
            basicModalDialogMock.mockImplementationOnce(({dismiss}) => {
                useEffect(() => {
                    dismiss.onDismiss()
                }, [])
                return <div>Are you sure you want to delete this recipe?</div>
            })

            renderWrappedInCommonContexts(<RecipeDetails id={expectedRecipe.id}/>)

            userEvent.click(await screen.findByLabelText(`Delete recipe with id ${expectedRecipe.id}`))

            expect(await screen.findByText(`An error occurred while trying to delete this recipe: Something went wrong`)).toBeInTheDocument()
        })
    })
})