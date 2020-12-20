import React from "react"
import PropTypes from "prop-types"
import {
    Button,
    ButtonGroup,
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
    actionText: string
    onAction: () => void
    onClose: () => void
}

const Modal: React.FC<ModalProps> = ({
                                         title,
                                         content,
                                         actionText,
                                         onAction,
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
            <ButtonGroup>
                <Button aria-label="Cancel modal" onClick={onClose}>Close</Button>
                <Button aria-label="Accept action" onClick={onAction}>{actionText}</Button>
            </ButtonGroup>
        </ModalFooter>
    </ModalContent>
</ChakraModal>

Modal.propTypes = {
    title: PropTypes.string.isRequired,
    content: PropTypes.string.isRequired,
    actionText: PropTypes.string.isRequired,
    onAction: PropTypes.func.isRequired,
    onClose: PropTypes.func.isRequired
}

export default Modal