import React, {useContext} from "react"
import ModalContext, {useModalState, WithModal} from "./modal-context"
import {render, screen} from "@testing-library/react"
import {act, renderHook} from "@testing-library/react-hooks"
import userEvent from "@testing-library/user-event"

jest.mock("./modal", () => ({
    __esModule: true,
    default: () => <>I'm the modal you're looking for</>
}))

describe("Modal context", () => {
    describe("Modal context provider", () => {
        it("provides open/close state and props to the modal", () => {
            const Component = () => {
                const {modalState: {props}} = useContext(ModalContext)

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

    describe("Use modal state hook", () => {
        it("starts closed and with empty state information if no initial state is provided", () => {
            const {result: {current}} = renderHook(() => useModalState())
            const {modalState} = current

            expect(modalState).toStrictEqual({
                isOpen: false,
                props: expect.anything()
            })
        })

        it("starts with the provided initial state", () => {
            const {result: {current}} = renderHook(() => useModalState({
                isOpen: true,
                props: {
                    title: "Ein",
                    content: "Zwei",
                    actionText: "Drei",
                    onAction: () => void (0),
                    onClose: () => void (0)
                }
            }))
            const {modalState} = current

            expect(modalState).toStrictEqual({
                isOpen: true,
                props: {
                    title: "Ein",
                    content: "Zwei",
                    actionText: "Drei",
                    onAction: expect.any(Function),
                    onClose: expect.any(Function)
                }
            })
        })

        it("updates the modal state", () => {
            const {result} = renderHook(() => useModalState(undefined))

            act(() => result.current.openModal({
                title: "One",
                content: "Two",
                actionText: "Three",
                onAction: () => void (0),
                onClose: () => void (0)
            }))

            expect(result.current.modalState).toStrictEqual({
                isOpen: true,
                props: {
                    title: "One",
                    content: "Two",
                    actionText: "Three",
                    onAction: expect.any(Function),
                    onClose: expect.any(Function)
                }
            })
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

        it("allows the children to open and close the modal", () => {
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

            userEvent.click(screen.getByText(/Open Modal/i))

            expect(screen.getByText(/I'm the modal you're looking for/i)).toBeInTheDocument()

            userEvent.click(screen.getByText(/Close Modal/i))

            expect(screen.queryByText(/I'm the modal you're looking for/i)).not.toBeInTheDocument()
        })
    })
})
