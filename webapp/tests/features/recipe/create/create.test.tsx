import React from "react"
import {fireEvent, screen, waitFor, within} from "@testing-library/react"
import CreateRecipe from "../../../../src/features/recipe/create/create"
import createRecipeService from "../../../../src/services/recipe-service"
import createRecipeTypeService, {RecipeType} from "../../../../src/services/recipe-type-service"
import {renderWithRoutes, renderWrappedInCommonContexts} from "../../../render"

jest.mock("../../../../src/services/recipe-type-service")
jest.mock("../../../../src/services/recipe-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>
const createRecipeServiceMock = createRecipeService as jest.MockedFunction<typeof createRecipeService>


describe("Create recipe component", () => {
    const createRecipeMock = jest.fn()
    const getRecipeTypesMock = jest.fn().mockImplementation(() =>
        Promise.resolve([
            {id: 1, name: "ABC"}
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
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")
        renderWrappedInCommonContexts(<CreateRecipe/>, apiHandlerMock)

        expect(screen.getByText(/create a new recipe/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/description/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe type parameter/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/ingredients/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/preparing steps/i)).toBeInTheDocument()
        const submitButton = screen.getByLabelText(/create recipe/i)
        expect(submitButton).toHaveAttribute("type", "submit")

        await waitFor(() => expect(getRecipeTypesMock).toHaveBeenCalled())
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(createRecipeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
    })

    describe("Form validation", () => {
        test.each([
            ["Name is required", ""],
            ["Name exceeds the character limit", "a".repeat(129)],
        ])("it displays an error when '%s'", async (message, name) => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            const nameInput = screen.getByLabelText("Name")
            fireEvent.change(nameInput, {target: {value: name}})

            const submitButton = screen.getByLabelText(/create recipe/i)
            fireEvent.submit(submitButton)

            await waitFor(() => {
                expect(screen.getByText(message)).toBeInTheDocument()
            })
        })

        test.each([
            ["Description is required", ""],
            ["Description exceeds the character limit", "b".repeat(257)],
        ])("it displays an error when '%s'", async (message, description) => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            const descriptionInput = screen.getByLabelText("Description")
            fireEvent.change(descriptionInput, {target: {value: description}})

            const submitButton = screen.getByLabelText(/create recipe/i)
            fireEvent.submit(submitButton)

            await waitFor(() => {
                expect(screen.getByText(message)).toBeInTheDocument()
            })
        })

        it("displays an error if no recipe type is selected", async () => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            await waitFor(() => expect(getRecipeTypesMock).toHaveBeenCalled())

            const submitButton = screen.getByLabelText(/create recipe/i)
            fireEvent.submit(submitButton)

            await waitFor(() => expect(screen.getByText("Recipe type is required")))
        })

        test.each([
            ["Ingredients is required", ""],
            ["Ingredients exceeds the character limit", "i".repeat(2049)],
        ])("it displays an error when '%s'", async (message, ingredients) => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            const ingredientsInput = screen.getByLabelText("Ingredients")
            fireEvent.change(ingredientsInput, {target: {value: ingredients}})

            const submitButton = screen.getByLabelText(/create recipe/i)
            fireEvent.submit(submitButton)

            await waitFor(() => {
                expect(screen.getByText(message)).toBeInTheDocument()
            })
        })

        test.each([
            ["Preparing steps is required", ""],
            ["Preparing steps exceeds the character limit", "i".repeat(4099)],
        ])("it displays an error when '%s'", async (message, preparingSteps) => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            const preparingStepsInput = screen.getByLabelText("Preparing steps")
            fireEvent.change(preparingStepsInput, {target: {value: preparingSteps}})

            const submitButton = screen.getByLabelText(/create recipe/i)
            fireEvent.submit(submitButton)

            await waitFor(() => {
                expect(screen.getByText(message)).toBeInTheDocument()
            })
        })
    })

    it("calls the 'createRecipe' function on submit", async () => {
        createRecipeMock.mockResolvedValueOnce({id: 1})

        renderWithRoutes({
            "/recipe/new": () => <CreateRecipe/>,
            "/recipe/1": () => <div>I'm the recipe details page for id 1</div>
        }, "/recipe/new")

        await waitFor(() => expect(getRecipeTypesMock).toHaveBeenCalled())

        const nameInput = screen.getByLabelText("Name")
        fireEvent.change(nameInput, {target: {value: "name"}})
        const descriptionInput = screen.getByLabelText("Description")
        fireEvent.change(descriptionInput, {target: {value: "description"}})

        const selectElement = screen.getByText("Recipe type")
            .closest("div")
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        fireEvent.mouseDown(within(selectElement).getByRole("button"))
        fireEvent.click(screen.getByText("ABC"))

        const ingredientsInput = screen.getByLabelText("Ingredients")
        fireEvent.change(ingredientsInput, {target: {value: "ingredients"}})
        const preparingStepsInput = screen.getByLabelText("Preparing steps")
        fireEvent.change(preparingStepsInput, {
            target: {value: "preparing steps"}
        })

        const submitButton = screen.getByLabelText(/create recipe/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(createRecipeMock).toHaveBeenCalledWith({
                name: "name",
                description: "description",
                recipeTypeId: 1,
                userId: 666,
                ingredients: "ingredients",
                preparingSteps: "preparing steps"
            })
            expect(screen.getByText(/^recipe 'Name' created successfully!$/i)).toBeInTheDocument()
            expect(screen.getByText(/i'm the recipe details page for id 1/i)).toBeInTheDocument()
        })
    })
})