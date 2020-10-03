import React from "react"
import {screen, within} from "@testing-library/react"
import CreateRecipe from "../../../../src/features/recipe/create/create"
import createRecipeService from "../../../../src/services/recipe-service"
import createRecipeTypeService, {RecipeType} from "../../../../src/services/recipe-type-service"
import {renderWithRoutes, renderWrappedInCommonContexts} from "../../../render"
import userEvent from "@testing-library/user-event"

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
        expect(screen.getByText(/loading.../i)).toBeInTheDocument()

        expect(await screen.findByLabelText(/^name$/i)).toBeInTheDocument()
        expect(await screen.findByLabelText(/^description$/i)).toBeInTheDocument()
        expect(await screen.findByLabelText(/^recipe type parameter$/i)).toBeInTheDocument()
        expect(await screen.findByLabelText(/^ingredients$/i)).toBeInTheDocument()
        expect(await screen.findByLabelText(/^preparing steps$/i)).toBeInTheDocument()
        expect(await screen.findByLabelText(/^create recipe$/i)).toHaveAttribute("type", "submit")

        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(createRecipeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(getRecipeTypesMock).toHaveBeenCalled()
    })

    describe("Form validation", () => {
        test.each([
            ["Name is required", ""],
            ["Name exceeds the character limit", "a".repeat(129)],
        ])("it displays an error when '%s'", async (message, name) => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            await userEvent.paste(await screen.findByLabelText(/^name$/i), name)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })

        test.each([
            ["Description is required", ""],
            ["Description exceeds the character limit", "b".repeat(257)],
        ])("it displays an error when '%s'", async (message, description) => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            await userEvent.paste(await screen.findByLabelText(/^description$/i), description)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })

        it("displays an error if no recipe type is selected", async () => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            userEvent.click(await screen.findByLabelText(/create recipe/i))

            expect(await screen.findByText("Recipe type is required")).toBeInTheDocument()
        })

        test.each([
            ["Ingredients is required", ""],
            ["Ingredients exceeds the character limit", "i".repeat(2049)],
        ])("it displays an error when '%s'", async (message, ingredients) => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            await userEvent.paste(await screen.findByLabelText(/^ingredients$/i), ingredients)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })

        test.each([
            ["Preparing steps is required", ""],
            ["Preparing steps exceeds the character limit", "i".repeat(4099)],
        ])("it displays an error when '%s'", async (message, preparingSteps) => {
            renderWrappedInCommonContexts(<CreateRecipe/>)

            await userEvent.paste(await screen.findByLabelText(/^preparing steps$/i), preparingSteps)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })
    })

    it("calls the 'createRecipe' function on submit", async () => {
        createRecipeMock.mockResolvedValueOnce({id: 1})
        renderWithRoutes({
            "/recipe/new": () => <CreateRecipe/>,
            "/recipe/1": () => <div>I'm the recipe details page for id 1</div>
        }, "/recipe/new")

        await userEvent.type(await screen.findByLabelText(/^name$/i), "name")
        await userEvent.type(await screen.findByLabelText(/^description$/i), "description")

        // @ts-ignore
        userEvent.click(within(screen.getByText("Recipe type").closest("div")).getByRole("button"))
        userEvent.click(screen.getByText("ABC"))

        await userEvent.type(await screen.findByLabelText(/^ingredients$/i), "ingredients")
        await userEvent.type(await screen.findByLabelText(/^preparing steps$/i), "preparing steps")

        userEvent.click(screen.getByLabelText(/create recipe/i))

        expect(await screen.findByText(/^recipe 'Name' created successfully!$/i)).toBeInTheDocument()
        expect(await screen.findByText(/i'm the recipe details page for id 1/i)).toBeInTheDocument()
        expect(createRecipeMock).toHaveBeenCalledWith({
            name: "name",
            description: "description",
            recipeTypeId: 1,
            userId: 666,
            ingredients: "ingredients",
            preparingSteps: "preparing steps"
        })
    })
})