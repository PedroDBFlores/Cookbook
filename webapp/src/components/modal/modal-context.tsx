import React, {useState} from "react"
import Modal from "./modal"

interface ModalStateProps {
    title: string
    content: string
    actionText: string
    onAction: () => void
    onClose: () => void
}

interface ModalState {
    isOpen: boolean
    props: ModalStateProps
}

// eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
export const useModalState = (initialState?: ModalState) => {
    const [modalState, setModalState] = useState<ModalState>(initialState ?? {
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

    return {modalState, openModal, closeModal}
}

export const WithModal: React.FC = ({children}) => {
    const {modalState, openModal, closeModal} = useModalState()
    const {title, content, actionText, onAction, onClose} = {...modalState.props}

    return <ModalContext.Provider value={{modalState, openModal, closeModal}}>
        {modalState.isOpen && <Modal title={title}
                                     content={content}
                                     actionText={actionText}
                                     onAction={onAction}
                                     onClose={onClose}/>}
        {children}
    </ModalContext.Provider>
}

interface ModalContextProps {
    modalState: ModalState
    openModal: (props: ModalStateProps) => void
    closeModal: () => void
}

const ModalContext = React.createContext<ModalContextProps>({
    modalState: {isOpen: false, props: {} as ModalStateProps},
    openModal: () => void (0),
    closeModal: () => void (0)
})

export default ModalContext
