import React from "react"
import {fireEvent, render, screen, waitFor} from "@testing-library/react"
import RecipeForm from "./recipe-form"
import {Recipe} from "services/recipe-service"
import {RecipeType} from "services/recipe-type-service"
import userEvent from "@testing-library/user-event"

describe("Recipe form", () => {
    const recipeTypes: Array<RecipeType> = [
        {id: 1, name: "The first"},
        {id: 2, name: "The second"}
    ]

    describe("Initial rendering", () => {
        test.each([
            ["create", undefined, /create recipe/i],
            ["edit",
                {
                    id: 1,
                    recipeTypeId: 1,
                    name: "Name",
                    description: "Description",
                    ingredients: "Ingredients",
                    preparingSteps: "PreparingSteps"
                } as Recipe,
                /edit recipe/i]
        ])("renders the necessary elements for the %s form",
            (_, initialValues, submitRegExp) => {
                render(<RecipeForm recipeTypes={recipeTypes}
                                   initialValues={initialValues}
                                   onSubmit={jest.fn()}/>)

                expect(screen.getByLabelText(/^name$/i)).toBeInTheDocument()
                expect(screen.getByLabelText(/^description$/i)).toBeInTheDocument()
                expect(screen.getByLabelText(/^recipe type parameter$/i)).toBeInTheDocument()
                expect(screen.getByLabelText(/^ingredients$/i)).toHaveProperty("type", "textarea")
                expect(screen.getByLabelText(/^preparing steps$/i)).toHaveProperty("type", "textarea")
                expect(screen.getByLabelText(submitRegExp)).toHaveAttribute("type", "submit")
                expect(screen.getByLabelText(/reset form/i)).toHaveAttribute("type", "button")
            })

        test.each([
            [
                "creating",
                undefined,
                {recipeTypeId: undefined, name: "", description: "", ingredients: "", preparingSteps: ""}
            ],
            [
                "updating",
                {
                    id: 1,
                    recipeTypeId: 1,
                    name: "Name",
                    description: "Description",
                    ingredients: "Ingredients",
                    preparingSteps: "PreparingSteps"
                } as Recipe,
                {
                    recipeTypeId: 1,
                    name: "Name",
                    description: "Description",
                    ingredients: "Ingredients",
                    preparingSteps: "PreparingSteps"
                }
            ]
        ])("sets the initial values while %s", (_, initialValues, expectedValues) => {
            render(<RecipeForm recipeTypes={recipeTypes}
                               initialValues={initialValues}
                               onSubmit={jest.fn()}/>)

            expect(screen.getByLabelText(/^name$/i)).toHaveValue(expectedValues.name)
            expect(screen.getByLabelText(/^description$/i)).toHaveValue(expectedValues.description)
            expect(screen.getByLabelText(/^recipe type$/i)).toHaveValue(expectedValues.recipeTypeId?.toString() ?? "")
            expect(screen.getByLabelText(/^ingredients$/i)).toHaveValue(expectedValues.ingredients)
            expect(screen.getByLabelText(/^preparing steps$/i)).toHaveValue(expectedValues.preparingSteps)
        })
    })

    describe("Form validation", () => {
        test.each([
            ["Name is required", ""],
            ["Name exceeds the character limit", "a".repeat(129)],
        ])("it displays an error when '%s'", async (message, name) => {
            const onSubmitMock = jest.fn()
            render(<RecipeForm recipeTypes={recipeTypes}
                               onSubmit={onSubmitMock}/>)

            const nameInput = await screen.findByLabelText(/^name$/i)
            userEvent.clear(nameInput)
            userEvent.paste(nameInput, name)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
            expect(onSubmitMock).not.toHaveBeenCalled()
        })

        test.each([
            ["Description is required", ""],
            ["Description exceeds the character limit", "b".repeat(257)],
        ])("it displays an error when '%s'", async (message, description) => {
            const onSubmitMock = jest.fn()
            render(<RecipeForm recipeTypes={recipeTypes}
                               onSubmit={onSubmitMock}/>)

            const descriptionInput = await screen.findByLabelText(/^description$/i)
            userEvent.clear(descriptionInput)
            userEvent.paste(descriptionInput, description)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
            expect(onSubmitMock).not.toHaveBeenCalled()
        })

        it("displays an error if no recipe type is selected", async () => {
            const onSubmitMock = jest.fn()
            render(<RecipeForm recipeTypes={recipeTypes}
                               onSubmit={onSubmitMock}/>)

            userEvent.selectOptions(await screen.findByLabelText("Recipe type"), "")
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText("Recipe type is required")).toBeInTheDocument()
        })

        test.each([
            ["Ingredients is required", ""],
            ["Ingredients exceeds the character limit", "i".repeat(2049)],
        ])("it displays an error when '%s'", async (message, ingredients) => {
            const onSubmitMock = jest.fn()
            render(<RecipeForm recipeTypes={recipeTypes}
                               onSubmit={onSubmitMock}/>)

            const ingredientsInput = await screen.findByLabelText(/^ingredients$/i)
            userEvent.clear(ingredientsInput)
            userEvent.paste(ingredientsInput, ingredients)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
            expect(onSubmitMock).not.toHaveBeenCalled()
        })

        test.each([
            ["Preparing steps is required", ""],
            ["Preparing steps exceeds the character limit", "i".repeat(4099)],
        ])("it displays an error when '%s'", async (message, preparingSteps) => {
            const onSubmitMock = jest.fn()
            render(<RecipeForm recipeTypes={recipeTypes}
                               onSubmit={onSubmitMock}/>)

            const preparingStepsInput = await screen.findByLabelText(/^preparing steps$/i)
            userEvent.clear(preparingStepsInput)
            userEvent.paste(preparingStepsInput, preparingSteps)
            userEvent.click(screen.getByLabelText(/create recipe/i))

            expect(await screen.findByText(message)).toBeInTheDocument()
            expect(onSubmitMock).not.toHaveBeenCalled()
        })
    })

    describe("Submit action", () => {
        test.each([
            ["creating", {
                initialValues: undefined,
                submitRegExp: /create recipe/i
            }],
            ["updating", {
                initialValues: {
                    id: 1,
                    recipeTypeId: 1,
                    name: "The",
                    description: "Old",
                    ingredients: "Musty",
                    preparingSteps: "Recipe"
                } as Recipe,
                submitRegExp: /edit recipe/i
            }]
        ])("it provides the inputted values to the onSubmit callback while %s", async (_, {
            initialValues,
            submitRegExp
        }) => {
            const onSubmitMock = jest.fn()
            render(<RecipeForm recipeTypes={recipeTypes}
                               initialValues={initialValues}
                               onSubmit={onSubmitMock}/>)

            const nameInput = screen.getByLabelText(/^name$/i)
            const descriptionInput = screen.getByLabelText(/^description$/i)
            const ingredientsInput = screen.getByLabelText(/^ingredients$/i)
            const preparingStepsInput = screen.getByLabelText(/^preparing steps$/i)
            userEvent.clear(nameInput)
            userEvent.clear(descriptionInput)
            userEvent.clear(ingredientsInput)
            userEvent.clear(preparingStepsInput)

            userEvent.paste(nameInput, "One")
            userEvent.paste(descriptionInput, "Two")
            userEvent.selectOptions(screen.getByLabelText("Recipe type"), "1")
            userEvent.paste(ingredientsInput, "Three")
            userEvent.paste(preparingStepsInput, "Four")
            fireEvent.click(screen.getByLabelText(submitRegExp))

            await waitFor(() => expect(onSubmitMock).toHaveBeenCalledWith({
                id: initialValues?.id ?? 0,
                recipeTypeId: 1,
                name: "One",
                description: "Two",
                ingredients: "Three",
                preparingSteps: "Four"
            }))
        })
    })

    describe("Reset action", () => {
        test.each([
            ["creating", undefined],
            ["updating", {
                id: 1,
                recipeTypeId: 1,
                name: "The",
                description: "Old",
                ingredients: "Musty",
                preparingSteps: "Recipe"
            } as Recipe
            ]
        ])("resets the form to the original values while %s", (_, initialValues) => {
            render(<RecipeForm recipeTypes={recipeTypes}
                               initialValues={initialValues}
                               onSubmit={jest.fn()}/>)
            const nameInput = screen.getByLabelText(/name/i)
            const descriptionInput = screen.getByLabelText(/description/i)
            const recipeTypeInput = screen.getByLabelText(/^recipe type$/i)
            const ingredientsInput = screen.getByLabelText(/ingredients/i)
            const preparingStepsInput = screen.getByLabelText(/preparing steps/i)
            userEvent.clear(nameInput)
            userEvent.clear(descriptionInput)
            userEvent.selectOptions(recipeTypeInput, "")
            userEvent.clear(ingredientsInput)
            userEvent.clear(preparingStepsInput)
            userEvent.paste(nameInput, "One")
            userEvent.paste(descriptionInput, "Two")
            userEvent.selectOptions(recipeTypeInput, "2")
            userEvent.paste(ingredientsInput, "Three")
            userEvent.paste(preparingStepsInput, "Four")

            userEvent.click(screen.getByLabelText(/reset form/i))

            expect(nameInput).toHaveValue(initialValues?.name ?? "")
            expect(descriptionInput).toHaveValue(initialValues?.description?? "")
            expect(recipeTypeInput).toHaveValue(initialValues?.recipeTypeId?.toString() ?? undefined)
            expect(ingredientsInput).toHaveValue(initialValues?.ingredients?? "")
            expect(preparingStepsInput).toHaveValue(initialValues?.preparingSteps?? "")
        })
    })
})