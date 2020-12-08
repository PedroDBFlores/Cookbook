import React from "react"
import {render, screen, within} from "@testing-library/react"
import createRecipeService, {RecipeDetails} from "../../../services/recipe-service"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {WrapperWithRoutes, WrapWithCommonContexts} from "../../../../tests/render-helpers"
import userEvent from "@testing-library/user-event"
import EditRecipe from "./edit"

jest.mock("../../../../src/services/recipe-type-service")
jest.mock("../../../../src/services/recipe-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>
const createRecipeServiceMock = createRecipeService as jest.MockedFunction<typeof createRecipeService>

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
        create: jest.fn(),
        search: jest.fn(),
        find: findRecipeMock,
        delete: jest.fn(),
        update: updateRecipeMock,
        getAll: jest.fn()
    }))

    it("renders the initial layout", async () => {
        const apiHandlerMock = jest.fn().mockReturnValue("My api handler")

        render(<WrapWithCommonContexts apiHandler={apiHandlerMock}>
            <EditRecipe id={1}/>
        </WrapWithCommonContexts>)

        expect(screen.getByText(/Edit recipe/i)).toBeInTheDocument()
        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(await screen.findByLabelText(/^name$/i)).toHaveValue("A great")
        expect(await screen.findByLabelText(/^description$/i)).toHaveValue("recipe for winter times")
        expect(await screen.findByLabelText(/^recipe type$/i)).toHaveValue("1")
        expect(await screen.findByLabelText(/^ingredients$/i)).toHaveValue("A lot of love")
        expect(await screen.findByLabelText(/^preparing steps$/i)).toHaveValue("and care")
        expect(await screen.findByLabelText(/^edit recipe$/i)).toHaveAttribute("type", "submit")
        expect(createRecipeTypeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(createRecipeServiceMock).toHaveBeenCalledWith(apiHandlerMock())
        expect(findRecipeMock).toHaveBeenCalledWith(1)
        expect(getRecipeTypesMock).toHaveBeenCalled()
    })

    describe("Form validation", () => {
        test.each([
            ["Name is required", ""],
            ["Name exceeds the character limit", "a".repeat(129)],
        ])("it displays an error when '%s'", async (message, name) => {
            render(<WrapWithCommonContexts>
                <EditRecipe id={1}/>
            </WrapWithCommonContexts>)

            const nameInput = await screen.findByLabelText(/^name$/i)
            userEvent.clear(nameInput)
            userEvent.paste(nameInput, name)
            userEvent.click(screen.getByLabelText(/edit recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })

        test.each([
            ["Description is required", ""],
            ["Description exceeds the character limit", "b".repeat(257)],
        ])("it displays an error when '%s'", async (message, description) => {
            render(<WrapWithCommonContexts>
                <EditRecipe id={1}/>
            </WrapWithCommonContexts>)

            const descriptionInput = await screen.findByLabelText(/^description$/i)
            userEvent.clear(descriptionInput)
            userEvent.paste(descriptionInput, description)
            userEvent.click(screen.getByLabelText(/edit recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })

        it("displays an error if no recipe type is selected", async () => {
            render(<WrapWithCommonContexts>
                <EditRecipe id={1}/>
            </WrapWithCommonContexts>)

            const recipeTypeInput = await screen.findByText("Recipe type")

            //@ts-ignore
            userEvent.click(within(recipeTypeInput.closest("div")).getByRole("button"))
            userEvent.click(screen.getByText("", {selector: "li"}))
            userEvent.click(await screen.findByLabelText(/edit recipe/i))

            expect(await screen.findByText("Recipe type is required")).toBeInTheDocument()
        })

        test.each([
            ["Ingredients is required", ""],
            ["Ingredients exceeds the character limit", "i".repeat(2049)],
        ])("it displays an error when '%s'", async (message, ingredients) => {
            render(<WrapWithCommonContexts>
                <EditRecipe id={1}/>
            </WrapWithCommonContexts>)

            const ingredientsInput = await screen.findByLabelText(/^ingredients$/i)
            userEvent.clear(ingredientsInput)
            userEvent.paste(ingredientsInput, ingredients)
            userEvent.click(screen.getByLabelText(/edit recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })

        test.each([
            ["Preparing steps is required", ""],
            ["Preparing steps exceeds the character limit", "i".repeat(4099)],
        ])("it displays an error when '%s'", async (message, preparingSteps) => {
            render(<WrapWithCommonContexts>
                <EditRecipe id={1}/>
            </WrapWithCommonContexts>)

            const preparingStepsInput = await screen.findByLabelText(/^preparing steps$/i)
            userEvent.clear(preparingStepsInput)
            userEvent.paste(preparingStepsInput, preparingSteps)
            userEvent.click(screen.getByLabelText(/edit recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
        })
    })

    it("calls the 'updateRecipe' function on submit", async () => {
        updateRecipeMock.mockResolvedValueOnce({})
        render(<WrapWithCommonContexts>
            <WrapperWithRoutes initialPath="/recipe/1/edit" routeConfiguration={[
                {path: "/recipe/1/edit", exact: true, component: () => <EditRecipe id={1}/>},
                {path: "/recipe/1", exact: true, component: () => <>I'm the recipe details page for id 1</>},
            ]}/>
        </WrapWithCommonContexts>)

        const nameInput = await screen.findByLabelText(/^name$/i)
        const descriptionInput = await screen.findByLabelText(/^description$/i)
        const ingredientsInput = await screen.findByLabelText(/^ingredients$/i)
        const preparingStepsInput = await screen.findByLabelText(/^preparing steps$/i)
        userEvent.clear(nameInput)
        userEvent.clear(descriptionInput)
        userEvent.clear(ingredientsInput)
        userEvent.clear(preparingStepsInput)

        await userEvent.type(nameInput, "name")
        await userEvent.type(descriptionInput, "description")
        // @ts-ignore
        userEvent.click(within(screen.getByText("Recipe type").closest("div")).getByRole("button"))
        userEvent.click(screen.getByText("ABC", {selector: "li"}))
        await userEvent.type(ingredientsInput, "ingredients")
        await userEvent.type(preparingStepsInput, "preparing steps")
        userEvent.click(screen.getByLabelText(/edit recipe/i))

        expect(await screen.findByText(/^recipe 'name' updated successfully!$/i)).toBeInTheDocument()
        expect(await screen.findByText(/i'm the recipe details page for id 1/i)).toBeInTheDocument()
        expect(updateRecipeMock).toHaveBeenCalledWith({
            id: 1,
            name: "name",
            description: "description",
            recipeTypeId: 1,
            ingredients: "ingredients",
            preparingSteps: "preparing steps"
        })
    })

    it("shows an error message if the update API call fails", async () => {
        updateRecipeMock.mockRejectedValueOnce({message: "A wild error has appeared"})
        render(<WrapWithCommonContexts>
            <EditRecipe id={1}/>
        </WrapWithCommonContexts>)

        const nameInput = await screen.findByLabelText(/^name$/i)
        const descriptionInput = await screen.findByLabelText(/^description$/i)
        const ingredientsInput = await screen.findByLabelText(/^ingredients$/i)
        const preparingStepsInput = await screen.findByLabelText(/^preparing steps$/i)
        userEvent.clear(nameInput)
        userEvent.clear(descriptionInput)
        userEvent.clear(ingredientsInput)
        userEvent.clear(preparingStepsInput)

        await userEvent.type(nameInput, "i")
        await userEvent.type(descriptionInput, "will")

        // @ts-ignore
        userEvent.click(within(screen.getByText("Recipe type").closest("div")).getByRole("button"))
        userEvent.click(screen.getByText("ABC", {selector: "li"}))

        await userEvent.type(ingredientsInput, "fail")
        await userEvent.type(preparingStepsInput, "preparing steps")

        userEvent.click(screen.getByLabelText(/edit recipe/i))

        expect(await screen.findByText(/an error occurred while updating the recipe: A wild error has appeared/i)).toBeInTheDocument()
        expect(updateRecipeMock).toHaveBeenCalledWith({
            id: 1,
            name: "i",
            description: "will",
            recipeTypeId: 1,
            ingredients: "fail",
            preparingSteps: "preparing steps"
        })
    })
})