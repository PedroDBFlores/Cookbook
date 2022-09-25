import React from "react"
import { fireEvent, render, screen, waitFor } from "@testing-library/react"
import RecipeForm from "./recipe-form"
import { Recipe } from "services/recipe-service"
import { RecipeType } from "services/recipe-type-service"
import userEvent from "@testing-library/user-event"

describe("Recipe form", () => {
    const recipeTypes: Array<RecipeType> = [
        { id: 1, name: "The first" },
        { id: 2, name: "The second" }
    ]

    describe("Initial rendering", () => {
        test.each([
            ["create", undefined, /^translated common.create$/i],
            ["edit",
                {
                    id: 1,
                    recipeTypeId: 1,
                    name: "Name",
                    description: "Description",
                    ingredients: "Ingredients",
                    preparingSteps: "PreparingSteps"
                } as Recipe,
                /^translated common.edit$/i]
        ])("renders the necessary elements for the %s form",
            (_, initialValues, submitRegExp) => {
                render(<RecipeForm recipeTypes={recipeTypes}
                    initialValues={initialValues}
                    onSubmit={jest.fn()} />)

                expect(screen.getByLabelText(/^translated name$/i)).toBeInTheDocument()
                expect(screen.getByLabelText(/^translated description$/i)).toBeInTheDocument()
                expect(screen.getByLabelText(/^translated recipe-feature.recipe-type-parameter$/i)).toBeInTheDocument()
                expect(screen.getByLabelText(/^translated recipe-type-feature.singular$/i)).toBeInTheDocument()
                expect(screen.getByLabelText(/^translated ingredients$/i)).toHaveProperty("type", "textarea")
                expect(screen.getByLabelText(/^translated preparing-steps$/i)).toHaveProperty("type", "textarea")
                expect(screen.getByLabelText(submitRegExp)).toHaveAttribute("type", "submit")
                expect(screen.getByLabelText(/translated common.reset-form-aria-label/i)).toHaveAttribute("type", "button")
                expect(screen.getByText(/translated common.reset/i)).toBeInTheDocument()
            })

        test.each([
            [
                "creating",
                undefined,
                { recipeTypeId: undefined, name: "", description: "", ingredients: "", preparingSteps: "" }
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
                onSubmit={jest.fn()} />)

            expect(screen.getByLabelText(/^translated name$/i)).toHaveValue(expectedValues.name)
            expect(screen.getByLabelText(/^translated description$/i)).toHaveValue(expectedValues.description)
            expect(screen.getByLabelText(/^translated recipe-type-feature.singular$/i)).toHaveValue(expectedValues.recipeTypeId?.toString() ?? "")
            expect(screen.getByLabelText(/^translated ingredients$/i)).toHaveValue(expectedValues.ingredients)
            expect(screen.getByLabelText(/^translated preparing-steps$/i)).toHaveValue(expectedValues.preparingSteps)
        })
    })

    describe("Form validation", () => {

        [
            {
                condition: "Name is required",
                inputLabel: /^translated name$/i,
                inputValue: "",
                expectedMessage: /translated validations.is-required #translated name#/i
            },
            {
                condition: "Name exceeds the character limit",
                inputLabel: /^translated name$/i,
                inputValue: "a".repeat(129),
                expectedMessage: /translated validations.exceeds-the-character-limit #translated name#/i
            },
            {
                condition: "Description is required",
                inputLabel: /^translated description$/i,
                inputValue: "",
                expectedMessage: /translated validations.is-required #translated description#/i
            },
            {
                condition: "Description exceeds the character limit",
                inputLabel: /^translated description$/i,
                inputValue: "a".repeat(257),
                expectedMessage: /translated validations.exceeds-the-character-limit #translated description#/i
            },
            {
                condition: "Ingredients is required",
                inputLabel: /^translated ingredients$/i,
                inputValue: "",
                expectedMessage: /translated validations.is-required #translated ingredients#/i
            },
            {
                condition: "Ingredients exceeds the character limit",
                inputLabel: /^translated ingredients$/i,
                inputValue: "a".repeat(2049),
                expectedMessage: /translated validations.exceeds-the-character-limit #translated ingredients#/i
            },
            {
                condition: "Preparing steps is required",
                inputLabel: /^translated preparing-steps$/i,
                inputValue: "",
                expectedMessage: /translated validations.is-required #translated preparing-steps#/i
            },
            {
                condition: "Preparing steps exceeds the character limit",
                inputLabel: /^translated preparing-steps$/i,
                inputValue: "a".repeat(4097),
                expectedMessage: /translated validations.exceeds-the-character-limit #translated preparing-steps#/i
            }
        ].forEach(({ condition, inputLabel, inputValue, expectedMessage }) => {
            it(`displays an error when ${condition}`, async () => {
                const onSubmitMock = jest.fn()

                render(<RecipeForm recipeTypes={recipeTypes} onSubmit={onSubmitMock} />)

                const input = await screen.findByLabelText(inputLabel)

                await userEvent.clear(input)
                input.focus()
                await userEvent.paste(inputValue)
                await userEvent.click(screen.getByLabelText(/^translated common.create$/i))

                expect(await screen.findByText(expectedMessage)).toBeInTheDocument()
                expect(onSubmitMock).not.toHaveBeenCalled()
            })
        })

        it("displays an error if no recipe type is selected", async () => {
            const onSubmitMock = jest.fn()

            render(<RecipeForm recipeTypes={recipeTypes}
                onSubmit={onSubmitMock} />)

            await userEvent.selectOptions(await screen.findByLabelText("translated recipe-type-feature.singular"), "")
            await userEvent.click(screen.getByLabelText(/^translated common.create$/i))

            expect(await screen.findByText(/translated validations.is-required #translated recipe-type-feature.singular#/i)).toBeInTheDocument()
        })
    })

    describe("Submit action", () => {
        test.each([
            ["creating", {
                initialValues: undefined,
                submitRegExp: /^translated common.create$/i,
                expectedRecipeCalled: {
                    recipeTypeId: 1,
                    name: "One",
                    description: "Two",
                    ingredients: "Three",
                    preparingSteps: "Four"
                }
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
                submitRegExp: /^translated common.edit$/i,
                expectedRecipeCalled: {
                    id: 1,
                    recipeTypeId: 1,
                    name: "One",
                    description: "Two",
                    ingredients: "Three",
                    preparingSteps: "Four"
                }
            }]
        ])("it provides the inputted values to the onSubmit callback while %s", async (_, {
            initialValues,
            submitRegExp,
            expectedRecipeCalled
        }) => {
            const onSubmitMock = jest.fn()

            render(<RecipeForm recipeTypes={recipeTypes}
                initialValues={initialValues}
                onSubmit={onSubmitMock} />)

            const nameInput = screen.getByLabelText(/^translated name$/i)
            const descriptionInput = screen.getByLabelText(/^translated description$/i)
            const ingredientsInput = screen.getByLabelText(/^translated ingredients$/i)
            const preparingStepsInput = screen.getByLabelText(/^translated preparing-steps$/i)

            await userEvent.clear(nameInput)
            await userEvent.clear(descriptionInput)
            await userEvent.clear(ingredientsInput)
            await userEvent.clear(preparingStepsInput)

            await userEvent.type(nameInput, "One")
            await userEvent.type(descriptionInput, "Two")
            await userEvent.selectOptions(screen.getByLabelText("translated recipe-type-feature.singular"), "1")
            await userEvent.type(ingredientsInput, "Three")
            await userEvent.type(preparingStepsInput, "Four")
            fireEvent.click(screen.getByLabelText(submitRegExp))

            await waitFor(() => expect(onSubmitMock).toHaveBeenCalledWith(expectedRecipeCalled))
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
        ])("resets the form to the original values while %s", async (_, initialValues) => {
            render(<RecipeForm recipeTypes={recipeTypes}
                initialValues={initialValues}
                onSubmit={jest.fn()} />)

            const nameInput = screen.getByLabelText(/^translated name$/i)
            const descriptionInput = screen.getByLabelText(/^translated description$/i)
            const recipeTypeInput = screen.getByLabelText(/^translated recipe-type-feature.singular$/i)
            const ingredientsInput = screen.getByLabelText(/^translated ingredients$/i)
            const preparingStepsInput = screen.getByLabelText(/^translated preparing-steps$/i)

            await userEvent.clear(nameInput)
            await userEvent.clear(descriptionInput)
            await userEvent.selectOptions(recipeTypeInput, "")
            await userEvent.clear(ingredientsInput)
            await userEvent.clear(preparingStepsInput)

            await userEvent.type(nameInput, "One")
            await userEvent.type(descriptionInput, "Two")
            await userEvent.selectOptions(recipeTypeInput, "1")
            await userEvent.type(ingredientsInput, "Three")
            await userEvent.type(preparingStepsInput, "Four")

            await userEvent.click(screen.getByLabelText(/translated common.reset-form-aria-label/i))

            expect(nameInput).toHaveValue(initialValues?.name ?? "")
            expect(descriptionInput).toHaveValue(initialValues?.description ?? "")
            expect(recipeTypeInput).toHaveValue(initialValues?.recipeTypeId?.toString() ?? undefined)
            expect(ingredientsInput).toHaveValue(initialValues?.ingredients ?? "")
            expect(preparingStepsInput).toHaveValue(initialValues?.preparingSteps ?? "")
            await waitFor(() => expect(screen.getByLabelText(/translated common.reset-form-aria-label/i)).toBeDisabled())
        })
    })
})
