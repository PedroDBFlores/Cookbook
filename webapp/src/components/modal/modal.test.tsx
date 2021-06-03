import React from "react"
import Modal from "./modal"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

describe("Basic modal dialog", () => {
    it("renders the title and content", () => {
        render(<Modal title="A title" content="The content" actionText=""
            onAction={jest.fn()} onClose={jest.fn()}/>)

        expect(screen.getByText(/a title/i)).toBeInTheDocument()
        expect(screen.getByText(/the content/i)).toBeInTheDocument()
        expect(screen.getByText(/translated common.close/i)).toBeInTheDocument()
    })

    it("performs the modal action on click", () => {
        const modalActionMock = jest.fn()

        render(<Modal title="A title" content="The content" actionText="OK"
            onAction={modalActionMock} onClose={jest.fn()}/>)

        userEvent.click(screen.getByLabelText(/translated modal.accept-modal-action-aria-label/i))

        expect(modalActionMock).toHaveBeenCalled()
    })

    it("Performs the onClose action by clicking on the 'Cancel' button", () => {
        const closeFn = jest.fn()

        render(<Modal title="A title" content="The content"
            actionText="OK"
            onAction={jest.fn}
            onClose={closeFn}
        />)

        userEvent.click(screen.getByLabelText(/translated modal.cancel-modal-aria-label/i))

        expect(closeFn).toHaveBeenCalled()
    })
})
