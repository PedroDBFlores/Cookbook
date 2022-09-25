import React, { useContext, useState } from "react"
import Modal from "./modal"

interface ModalStateProps {
    title: string
    content: string
    actionText: string
    onAction: () => void
    onClose: () => void
}

interface InitialModelState {
    isOpen: boolean
    props: ModalStateProps
}

interface ModelState {
    openModal: (props: ModalStateProps) => void
    closeModal: () => void
    modalState: InitialModelState
}

const useModalState = (initialState?: InitialModelState): ModelState => {
    const [modalState, setModalState] = useState<InitialModelState>(initialState ?? {
        isOpen: false,
        props: {} as ModalStateProps
    })

    const openModal = (props: ModalStateProps) => setModalState({
        ...modalState,
        isOpen: true,
        props
    })

    const closeModal = () => setModalState({
        isOpen: false,
        props: {} as ModalStateProps
    })

    return { openModal, closeModal, modalState }
}

export const WithModal: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
    const { modalState, openModal, closeModal } = useModalState()
    const { title, content, actionText, onAction, onClose } = { ...modalState.props }

    return <ModalContext.Provider value={{ modalState, openModal, closeModal }}>
        {modalState.isOpen && <Modal title={title}
            content={content}
            actionText={actionText}
            onAction={onAction}
            onClose={onClose} />}
        {children}
    </ModalContext.Provider>
}

interface ModalContextProps {
    modalState: InitialModelState
    openModal: (props: ModalStateProps) => void
    closeModal: () => void
}

export const ModalContext = React.createContext<ModalContextProps>({
    modalState: { isOpen: false, props: {} as ModalStateProps },
    openModal: () => void (0),
    closeModal: () => void (0)
})

export const useModalContext = (): ModalContextProps => useContext(ModalContext)

export default useModalContext
