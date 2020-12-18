import React from "react"
import PropTypes from "prop-types"
import {
    Button,
    Modal as ChakraModal,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalFooter,
    ModalHeader,
    ModalOverlay
} from "@chakra-ui/react"

interface ModalProps {
    title: string
    content: string
    dismiss: {
        text: string
        onDismiss: () => void
    }
    onClose: () => void
}

const Modal: React.FC<ModalProps> = ({
                                         title, content,
                                         dismiss,
                                         onClose
                                     }) => <ChakraModal isOpen={true} onClose={onClose}>
    <ModalOverlay/>
    <ModalContent>
        <ModalHeader>{title}</ModalHeader>
        <ModalCloseButton/>
        <ModalBody>
            {content}
        </ModalBody>
        <ModalFooter>
            <Button aria-label="Cancel modal" onClick={onClose}>Close</Button>
            <Button aria-label="Dismiss modal" onClick={dismiss.onDismiss}>{dismiss.text}</Button>
        </ModalFooter>
    </ModalContent>
</ChakraModal>

Modal.propTypes = {
    title: PropTypes.string.isRequired,
    content: PropTypes.string.isRequired,
    dismiss: PropTypes.shape({
        text: PropTypes.string.isRequired,
        onDismiss: PropTypes.func.isRequired
    }).isRequired,
    onClose: PropTypes.func.isRequired
}

export default Modal