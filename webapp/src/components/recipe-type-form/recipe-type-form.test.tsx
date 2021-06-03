import React from "react"
import {render, screen, waitFor} from "@testing-library/react"
import RecipeTypeForm from "./recipe-type-form"
import userEvent from "@testing-library/user-event"
import {RecipeType} from "services/recipe-type-service"

describe("Recipe type form", () => {
    describe("Initial rendering", () => {
        test.each([
            ["create", undefined, /^translated common.create$/i],
            ["edit", {id: 1, name: ""}, /^translated common.edit$/i]
        ])("renders the necessary elements for the %s form", (formModeName, initialValues, submitRegExp) => {
            render(<RecipeTypeForm initialValues={initialValues} onSubmit={jest.fn()}/>)

            expect(screen.getByLabelText(/^translated name$/i)).toBeInTheDocument()
            expect(screen.getByLabelText(submitRegExp, {exact: false})).toHaveAttribute("type", "submit")
            expect(screen.getByLabelText(/translated common.reset-form-aria-label/i)).toHaveAttribute("type", "button")
            expect(screen.getByText(/translated common.reset/i)).toBeInTheDocument()
        })

        it("sets no initial values when creating", () => {
            render(<RecipeTypeForm onSubmit={jest.fn()}/>)

            expect(screen.getByLabelText(/^translated name$/i)).toHaveValue("")
        })

        it("sets the initial values when editing", () => {
            render(<RecipeTypeForm initialValues={
                {id: 1, name: "The great recipe type"}
            } onSubmit={jest.fn()}/>)

            expect(screen.getByLabelText(/^translated name$/i)).toHaveValue("The great recipe type")
        })
    })

    describe("Validation", () => {
        it("disallows submitting with an empty name", async () => {
            const onSubmitMock = jest.fn()

            render(<RecipeTypeForm onSubmit={onSubmitMock}/>)

            userEvent.clear(screen.getByLabelText(/^translated name$/i))
            userEvent.click(screen.getByLabelText(/^translated common.create$/i))

            expect(await screen.findByText(/translated validations.is-required #translated name#/i)).toBeInTheDocument()
            expect(onSubmitMock).not.toHaveBeenCalled()
        })

        it("disallows submitting with a name that exceeds the limit of 64 characters", async () => {
            const onSubmitMock = jest.fn()

            render(<RecipeTypeForm onSubmit={onSubmitMock}/>)

            userEvent.clear(screen.getByLabelText(/^translated name$/i))
            userEvent.paste(screen.getByLabelText(/^translated name$/i), "a".repeat(65))
            userEvent.click(screen.getByLabelText(/^translated common.create$/i))


            expect(await screen.findByText(/translated validations.exceeds-the-character-limit #translated name#/i)).toBeInTheDocument()
            expect(onSubmitMock).not.toHaveBeenCalled()
        })
    })

    describe("Submit action", () => {
        test.each([
            ["creating", undefined, /^translated common.create$/i, {name: "The best recipe type"}],
            ["updating", {
                id: 1,
                name: "The great recipe type"
            } as RecipeType, /^translated common.edit$/i, {
                id: 1,
                name: "The best recipe type"
            }]
        ])("it provides the inputted values to the onSubmit callback while %s", async (_, initialValues, submitRegExp, expectedRecipeType) => {
            const onSubmitMock = jest.fn()

            render(<RecipeTypeForm initialValues={initialValues} onSubmit={onSubmitMock}/>)

            userEvent.clear(screen.getByLabelText(/^translated name$/i))
            userEvent.paste(screen.getByLabelText(/^translated name$/i), "The best recipe type")
            userEvent.click(screen.getByLabelText(submitRegExp))

            await waitFor(() => {
                expect(onSubmitMock).toHaveBeenCalledWith(expectedRecipeType)
                expect(screen.getByLabelText(/^translated name$/i)).toHaveValue()
            })
        })
    })

    describe("Reset action", () => {
        test.each([
            ["creating", undefined],
            ["updating", {id: 1, name: "The great recipe type"} as RecipeType]
        ])("resets the form to the original values while %s", async (_, initialValues) => {
            render(<RecipeTypeForm initialValues={initialValues} onSubmit={jest.fn}/>)

            userEvent.clear(screen.getByLabelText(/^translated name$/i))
            userEvent.paste(screen.getByLabelText(/^translated name$/i), "Definitely I don't want this")

            userEvent.click(screen.getByLabelText(/translated common.reset-form-aria-label/i))

            expect(screen.getByLabelText(/^translated name$/i)).toHaveValue(initialValues?.name ?? "")
            await waitFor(() => expect(screen.getByLabelText(/translated common.reset-form-aria-label/i)).toBeDisabled())
        })
    })
})
