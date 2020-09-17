import React from "react"
import {Formik, Form} from "formik"
import {render, screen, fireEvent, waitFor} from "@testing-library/react"
import FormikSelector from "../../../src/components/formik-selector/formik-selector"
import Button from "@material-ui/core/Button"

describe("Formik selector component", () => {
    const getFormWrappedSelector = (
        initialValues: { formValue?: number } = {formValue: undefined},
        onSubmit: (data: { formValue?: number }) => void = jest.fn()) => {

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

    it("returns the expected value on submission", async () => {
        const onSubmitMock = jest.fn()
        render(getFormWrappedSelector({formValue: 1}, onSubmitMock))

        fireEvent.submit(screen.getByText("Submit"))

        await waitFor(() =>
            expect(onSubmitMock).toHaveBeenCalledWith({formValue: 1}))
    })
})