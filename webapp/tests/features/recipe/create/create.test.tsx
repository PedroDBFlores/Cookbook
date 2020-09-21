import React from "react"
import {fireEvent, render, screen, waitFor, within} from "@testing-library/react"
import CreateRecipe from "../../../../src/features/recipe/create/create"
import {Recipe} from "../../../../src/services/recipe-service"
import {CreateResult} from "../../../../src/model"
import {RecipeType} from "../../../../src/services/recipe-type-service"
import {AuthContext} from "../../../../src/services/credentials-service"

describe("Create recipe component", () => {
    const getRecipeTypesMock = jest.fn().mockImplementation(() =>
        Promise.resolve([
            {id: 1, name: "ABC"}
        ] as Array<RecipeType>))

    beforeEach(() => getRecipeTypesMock.mockClear())

    const wrapCreateRecipe = (
        onCreate: (recipe: Omit<Recipe, "id">) => Promise<CreateResult> = jest.fn()
    ) =>
        <AuthContext.Provider value={{userId: 666, name: "ALARMA", userName: "alarma"}}>
            <CreateRecipe getRecipeTypes={getRecipeTypesMock} onCreate={onCreate}/>
        </AuthContext.Provider>

    it("renders the initial form", async () => {
        render(wrapCreateRecipe())

        expect(screen.getByText(/create a new recipe/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/description/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe type parameter/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/ingredients/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/preparing steps/i)).toBeInTheDocument()
        const submitButton = screen.getByLabelText(/create recipe/i)
        expect(submitButton).toHaveAttribute("type", "submit")

        await waitFor(() => expect(getRecipeTypesMock).toHaveBeenCalled())
    })

    describe("Form validation", () => {
        test.each([
            ["Name is required", ""],
            ["Name exceeds the character limit", "a".repeat(129)],
        ])("it displays an error when '%s'", async (message, name) => {
            render(wrapCreateRecipe())

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
            render(wrapCreateRecipe())

            const descriptionInput = screen.getByLabelText("Description")
            fireEvent.change(descriptionInput, {target: {value: description}})

            const submitButton = screen.getByLabelText(/create recipe/i)
            fireEvent.submit(submitButton)

            await waitFor(() => {
                expect(screen.getByText(message)).toBeInTheDocument()
            })
        })

        it("displays an error if no recipe type is selected", async () => {
            render(wrapCreateRecipe())

            await waitFor(() => expect(getRecipeTypesMock).toHaveBeenCalled())

            const submitButton = screen.getByLabelText(/create recipe/i)
            fireEvent.submit(submitButton)

            await waitFor(() => expect(screen.getByText("Recipe type is required")))
        })

        test.each([
            ["Ingredients is required", ""],
            ["Ingredients exceeds the character limit", "i".repeat(2049)],
        ])("it displays an error when '%s'", async (message, ingredients) => {
            render(wrapCreateRecipe())

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
            render(wrapCreateRecipe())

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
        const onCreateMock = jest.fn().mockResolvedValueOnce({id: 1})
        render(wrapCreateRecipe(onCreateMock))

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
            expect(onCreateMock).toHaveBeenCalledWith({
                name: "name",
                description: "description",
                recipeTypeId: 1,
                userId: 666,
                ingredients: "ingredients",
                preparingSteps: "preparing steps"
            } as Omit<Recipe, "id">)
        })
    })
})