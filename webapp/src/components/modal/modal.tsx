import React from "react"
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
import {useTranslation} from "react-i18next"

interface ModalProps {
    title: string
    content: string
    actionText: string
    onAction: () => void
    onClose: () => void
}

const Modal: React.VFC<ModalProps> = ({
                                         title,
                                         content,
                                         actionText,
                                         onAction,
                                         onClose
                                     }) => {
    const {t} = useTranslation()

    return (
        <ChakraModal isOpen={true} onClose={onClose}>
            <ModalOverlay/>
            <ModalContent>
                <ModalHeader>{title}</ModalHeader>
                <ModalCloseButton/>
                <ModalBody>
                    {content}
                </ModalBody>
                <ModalFooter>
                    <ButtonGroup>
                        <Button aria-label={t("modal.cancel-modal-aria-label")}
                                onClick={onClose}>{t("common.close")}</Button>
                        <Button aria-label={t("modal.accept-modal-action-aria-label")}
                                onClick={onAction}>{actionText}</Button>
                    </ButtonGroup>
                </ModalFooter>
            </ModalContent>
        </ChakraModal>)
}

export default Modal
