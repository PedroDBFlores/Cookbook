import React from "react"
import {render, screen, within} from "@testing-library/react"
import CreateRecipe from "./create"
import createRecipeService from "../../../services/recipe-service"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {WrapperWithRoutes, WrapWithCommonContexts} from "../../../../tests/render-helpers"
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

        render(<WrapWithCommonContexts apiHandler={apiHandlerMock}>
            <CreateRecipe/>
        </WrapWithCommonContexts>)

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
        ])("displays an error when '%s'", async (message, name) => {
            render(<WrapWithCommonContexts>
                <CreateRecipe/>
            </WrapWithCommonContexts>)

            userEvent.paste(await screen.findByLabelText(/^name$/i), name)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })

        test.each([
            ["Description is required", ""],
            ["Description exceeds the character limit", "b".repeat(257)],
        ])("displays an error when '%s'", async (message, description) => {
            render(<WrapWithCommonContexts>
                <CreateRecipe/>
            </WrapWithCommonContexts>)

            userEvent.paste(await screen.findByLabelText(/^description$/i), description)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })

        it("displays an error if no recipe type is selected", async () => {
            render(<WrapWithCommonContexts>
                <CreateRecipe/>
            </WrapWithCommonContexts>)

            userEvent.click(await screen.findByLabelText(/create recipe/i))

            expect(await screen.findByText("Recipe type is required")).toBeInTheDocument()
        })

        test.each([
            ["Ingredients is required", ""],
            ["Ingredients exceeds the character limit", "i".repeat(2049)],
        ])("displays an error when '%s'", async (message, ingredients) => {
            render(<WrapWithCommonContexts>
                <CreateRecipe/>
            </WrapWithCommonContexts>)

            userEvent.paste(await screen.findByLabelText(/^ingredients$/i), ingredients)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })

        test.each([
            ["Preparing steps is required", ""],
            ["Preparing steps exceeds the character limit", "i".repeat(4099)],
        ])("it displays an error when '%s'", async (message, preparingSteps) => {
            render(<WrapWithCommonContexts>
                <CreateRecipe/>
            </WrapWithCommonContexts>)

            userEvent.paste(await screen.findByLabelText(/^preparing steps$/i), preparingSteps)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })
    })

    it("calls the 'createRecipe' function on submit", async () => {
        createRecipeMock.mockResolvedValueOnce({id: 1})
        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipe/new" routeConfiguration={[
                {path: "/recipe/new", exact: true, component: () => <CreateRecipe/>},
                {path: "/recipe/1", exact: true, component: () => <>I'm the recipe details page for id 1</>}
            ]}/>
        </WrapWithCommonContexts>)

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
            ingredients: "ingredients",
            preparingSteps: "preparing steps"
        })
    })

    it("shows an error message if the create API call fails", async () => {
        createRecipeMock.mockRejectedValueOnce({message: "A wild error has appeared"})
        render(<WrapWithCommonContexts>
            <CreateRecipe/>
        </WrapWithCommonContexts>)

        await userEvent.type(await screen.findByLabelText(/^name$/i), "i")
        await userEvent.type(await screen.findByLabelText(/^description$/i), "will")

        // @ts-ignore
        userEvent.click(within(screen.getByText("Recipe type").closest("div")).getByRole("button"))
        userEvent.click(screen.getByText("ABC"))

        await userEvent.type(await screen.findByLabelText(/^ingredients$/i), "fail")
        await userEvent.type(await screen.findByLabelText(/^preparing steps$/i), "preparing steps")

        userEvent.click(screen.getByLabelText(/create recipe/i))

        expect(await screen.findByText(/an error occurred while creating the recipe: A wild error has appeared/i)).toBeInTheDocument()
        expect(createRecipeMock).toHaveBeenCalledWith({
            name: "i",
            description: "will",
            recipeTypeId: 1,
            ingredients: "fail",
            preparingSteps: "preparing steps"
        })
    })
})