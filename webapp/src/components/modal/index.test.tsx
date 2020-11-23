import React from "react"
import Modal from "./modal"
import {render, screen} from "@testing-library/react"
import userEvent from "@testing-library/user-event"

describe("Basic modal dialog", () => {
    it("renders the title and content", () => {
        render(<Modal title="A title" content="The content" dismiss={{
            text: "", onDismiss: jest.fn()
        }} onClose={jest.fn()}/>)

        expect(screen.getByText(/a title/i)).toBeInTheDocument()
        expect(screen.getByText(/the content/i)).toBeInTheDocument()
    })

    it("performs the dismiss action on click", () => {
        const dismissMock = jest.fn()
        render(<Modal title="A title" content="The content" dismiss={{
            text: "OK",
            onDismiss: dismissMock
        }} onClose={jest.fn()} />)

        userEvent.click(screen.getByLabelText(/dismiss modal/i))

        expect(dismissMock).toHaveBeenCalled()
    })

    it("closes the modal by clicking on the 'Cancel' button", () => {
        const closeFn = jest.fn()
        render(<Modal title="A title" content="The content"
                      dismiss={{
                                     text: "OK",
                                     onDismiss: jest.fn()
                                 }}
                      onClose={closeFn}
        />)

        userEvent.click(screen.getByLabelText(/cancel modal/i))

        expect(closeFn).toHaveBeenCalled()
    })
})