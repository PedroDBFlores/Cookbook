import React from "react"
import BasicModalDialog from "../../../src/components/modal/basic-modal-dialog"
import {render, screen, fireEvent} from "@testing-library/react"

describe("Basic modal dialog", () => {
    it("renders the title and content", () => {
        render(<BasicModalDialog title="A title" content="The content" dismiss={{
            text: "", onDismiss: jest.fn()
        }} onClose={jest.fn()}/>)

        expect(screen.getByText(/a title/i)).toBeInTheDocument()
        expect(screen.getByText(/the content/i)).toBeInTheDocument()
    })

    it("performs the dismiss action on click", () => {
        const dismissMock = jest.fn()
        render(<BasicModalDialog title="A title" content="The content" dismiss={{
            text: "OK",
            onDismiss: dismissMock
        }} onClose={jest.fn()} />)
        const dismissButton = screen.getByLabelText(/dismiss modal/i)

        fireEvent.click(dismissButton)

        expect(dismissMock).toHaveBeenCalled()
    })

    it("closes the modal by clicking on the 'Cancel' button", () => {
        const closeFn = jest.fn()
        render(<BasicModalDialog title="A title" content="The content"
                                 dismiss={{
                                     text: "OK",
                                     onDismiss: jest.fn()
                                 }}
                                 onClose={closeFn}
        />)
        const closeButton = screen.getByLabelText(/cancel modal/i)

        fireEvent.click(closeButton)

        expect(closeFn).toHaveBeenCalled()
    })

    it("closes the modal by using the aria tag on the close 'X'", () => {
        const closeFn = jest.fn()
        render(<BasicModalDialog title="A title" content="The content"
                                 dismiss={{
                                     text: "OK",
                                     onDismiss: jest.fn()
                                 }}
                                 onClose={closeFn}
        />)
        const closeButton = screen.getByText(/close modal/i)

        fireEvent.click(closeButton)

        expect(closeFn).toHaveBeenCalled()
    })

})