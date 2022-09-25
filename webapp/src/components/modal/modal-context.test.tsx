import React, {useContext} from "react"
import useModalContext, {WithModal, ModalContext} from "./modal-context"
import {render, screen} from "@testing-library/react"
import userEvent from "@testing-library/user-event"

jest.mock("./modal", () => ({
    __esModule: true,
    default: () => <>I'm the modal you're looking for</>
}))

describe("Modal context", () => {
    describe("Modal context provider", () => {
        it("provides open/close state and props to the modal", () => {
            const Component = () => {
                const {modalState: {props}} = useModalContext()

                return <>
                    <span>{props?.title}</span>
                    <span>{props?.content}</span>
                    <span>{props?.actionText}</span>
                </>
            }

            render(<ModalContext.Provider
                value={{
                    modalState: {
                        isOpen: true,
                        props: {
                            title: "One",
                            content: "Two",
                            actionText: "Three",
                            onClose: jest.fn(),
                            onAction: jest.fn()
                        }
                    },
                    openModal: jest.fn(),
                    closeModal: jest.fn()
                }}>
                <Component/>
            </ModalContext.Provider>)

            expect(screen.getByText("One")).toBeInTheDocument()
            expect(screen.getByText("Two")).toBeInTheDocument()
            expect(screen.getByText("Three")).toBeInTheDocument()
        })
    })

    describe("WithModal HOC", () => {
        it("renders the children with the modal closed", () => {
            render(<WithModal>
                <p>The actual content</p>
            </WithModal>)

            expect(screen.getByText(/the actual content/i)).toBeInTheDocument()
            expect(screen.queryByText(/I'm the modal you're looking for/i)).not.toBeInTheDocument()
        })

        it("allows the children to open and close the modal", async () => {
            const Component = () => {
                const {openModal, closeModal} = useContext(ModalContext)

                const handleOnOpenModal = () => openModal({
                    title: "A",
                    content: "Great",
                    actionText: "Modal",
                    onAction: () => void (0),
                    onClose: () => void (0)
                })

                const handleOnCloseModal = () => closeModal()

                return <>
                    <button onClick={handleOnOpenModal}>Open Modal</button>
                    <button onClick={handleOnCloseModal}>Close Modal</button>
                </>
            }

            render(<WithModal>
                <Component/>
            </WithModal>)

            await userEvent.click(screen.getByText(/Open Modal/i))

            expect(screen.getByText(/I'm the modal you're looking for/i)).toBeInTheDocument()

            await userEvent.click(screen.getByText(/Close Modal/i))

            expect(screen.queryByText(/I'm the modal you're looking for/i)).not.toBeInTheDocument()
        })
    })
})
