import React from "react"
import {render, screen, waitFor} from "@testing-library/react"
import RecipeTypeForm from "./recipe-type-form"
import userEvent from "@testing-library/user-event"
import {RecipeType} from "services/recipe-type-service"

describe("Recipe type form", () => {
    describe("Initial rendering", () => {
        test.each([
            ["create", undefined],
            ["edit", {id: 1, name: ""}]
        ])("renders the necessary elements for the %s form", (formModeName, initialValues) => {
            render(<RecipeTypeForm initialValues={initialValues} onSubmit={jest.fn()}/>)

            expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
            expect(screen.getByLabelText(`${formModeName} recipe type`, {exact: false})).toHaveAttribute("type", "submit")
            expect(screen.getByLabelText(/reset form/i)).toHaveAttribute("type", "button")
        })

        it("sets no initial values when creating", () => {
            render(<RecipeTypeForm onSubmit={jest.fn()}/>)

            expect(screen.getByLabelText(/name/i)).toHaveValue("")
        })

        it("sets the initial values when editing", () => {
            render(<RecipeTypeForm initialValues={
                {id: 1, name: "The great recipe type"}
            } onSubmit={jest.fn()}/>)

            expect(screen.getByLabelText(/name/i)).toHaveValue("The great recipe type")
        })
    })

    describe("Validation", () => {
        it("disallows submitting with an empty name", async () => {
            const onSubmitMock = jest.fn()
            render(<RecipeTypeForm onSubmit={onSubmitMock}/>)

            userEvent.clear(screen.getByLabelText(/name/i))
            userEvent.click(screen.getByLabelText(/^create recipe type$/i))

            expect(await screen.findByText(/name is required/i)).toBeInTheDocument()
            expect(onSubmitMock).not.toHaveBeenCalled()
        })

        it("disallows submitting with a name that exceeds the limit of 64 characters", async () => {
            const onSubmitMock = jest.fn()
            render(<RecipeTypeForm onSubmit={onSubmitMock}/>)

            userEvent.clear(screen.getByLabelText(/name/i))
            userEvent.paste(screen.getByLabelText(/name/i), "a".repeat(65))
            userEvent.click(screen.getByLabelText(/^create recipe type$/i))

            expect(await screen.findByText(/name exceeds the character limit/i)).toBeInTheDocument()
            expect(onSubmitMock).not.toHaveBeenCalled()
        })
    })

    describe("Submit action", () => {
        test.each([
            ["creating", undefined, /^create recipe type$/i, {id: 0, name: "The best recipe type"}],
            ["updating", {id: 1, name: "The great recipe type"} as RecipeType, /^edit recipe type$/i, {
                id: 1,
                name: "The best recipe type"
            }],
        ])("it provides the inputted values to the onSubmit callback while %s", async (_, initialValues, submitRegExp, expectedRecipeType) => {
            const onSubmitMock = jest.fn()
            render(<RecipeTypeForm initialValues={initialValues} onSubmit={onSubmitMock}/>)

            userEvent.clear(screen.getByLabelText(/name/i))
            userEvent.paste(screen.getByLabelText(/name/i), "The best recipe type")
            userEvent.click(screen.getByLabelText(submitRegExp))

            await waitFor(() => expect(onSubmitMock).toHaveBeenCalledWith(expectedRecipeType))
        })
    })

    describe("Reset action", () => {
        test.each([
            ["creating", undefined, {name: ""}],
            ["updating", {id: 1, name: "The great recipe type"} as RecipeType, {name: "The great recipe type"}]
        ])("resets the form to the original values while %s", (_, initialValues, expectedResetValues) => {
            render(<RecipeTypeForm initialValues={initialValues} onSubmit={jest.fn}/>)

            userEvent.clear(screen.getByLabelText(/name/i))
            userEvent.paste(screen.getByLabelText(/name/i), "Definitely I don't want this")
            userEvent.click(screen.getByLabelText(/reset form/i))

            expect(screen.getByLabelText(/name/i)).toHaveValue(expectedResetValues.name)
        })
    })
})