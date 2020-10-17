import React from "react"
import {Formik, Form} from "formik"
import {render, screen, waitFor} from "@testing-library/react"
import FormikSelector from "./formik-selector"
import Button from "@material-ui/core/Button"
import userEvent from "@testing-library/user-event"

describe("Formik selector component", () => {
    const getFormWrappedSelector = (
        initialValues: { formValue?: number } = {formValue: 0},
        onSubmit: (data: { formValue?: number }) => void = jest.fn(),
        error: string | undefined = undefined) => {

        const handleSubmit = (data: { formValue?: number }) => onSubmit && onSubmit(data)

        return <Formik
            initialValues={initialValues}
            onSubmit={handleSubmit}>
            <Form>
                <FormikSelector
                    options={[{id: 1, name: "ABC"}, {id: 2, name: "DEF"}]}
                    label="Form value"
                    formName="formValue"
                    ariaLabel="Form value selector"
                    error={error}
                />
                <Button type="submit">Submit</Button>
            </Form>
        </Formik>
    }

    it("renders itself on clean form", () => {
        render(getFormWrappedSelector())

        expect(screen.getByText("Form value")).toBeInTheDocument()
        expect(screen.getByLabelText("Form value selector")).toBeInTheDocument()
        expect(screen.queryByText("ABC")).not.toBeInTheDocument()
        expect(screen.queryByText("DEF")).not.toBeInTheDocument()
    })

    it("renders itself on already with value form", () => {
        render(getFormWrappedSelector({formValue: 2}))

        expect(screen.getByText("DEF")).toBeInTheDocument()
    })

    it("renders an error if provided", () => {
        render(getFormWrappedSelector({formValue: 0}, jest.fn(), "an error"))

        expect(screen.getByText("an error")).toBeInTheDocument()
    })

    it("returns the expected value on submission", async () => {
        const onSubmitMock = jest.fn()
        render(getFormWrappedSelector({formValue: 1}, onSubmitMock))

        userEvent.click(screen.getByText("Submit"))

        await waitFor(() => expect(onSubmitMock).toHaveBeenCalledWith({formValue: 1}))
    })
})