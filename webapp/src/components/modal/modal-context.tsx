import React, {useState} from "react"
import Modal from "./modal"

interface ModalState {
    isOpen: boolean
    props?: {
        title: string
        content: string
        actionText: string
        onAction: () => void
        onClose: () => void
    }
}

// eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
export const useModalState = (initialState?: ModalState)  => {
    const [modalState, setModalState] = useState<ModalState>(initialState ?? {
        isOpen: false,
        props: undefined
    })

    return {modalState, setModalState}
}

export const WithModal: React.FC = ({children}) => {
    const {modalState, setModalState} = useModalState()
    const {title, content, actionText, onAction, onClose} = {...modalState.props}

    return <ModalContext.Provider value={{modalState, setModalState}}>
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
    setModalState: (modalState: ModalState) => void
}

const ModalContext = React.createContext<ModalContextProps>({
        modalState: {isOpen: false},
        setModalState: () => void (0)
    }
)

export default ModalContext