import React from "react"
import PropTypes from "prop-types"
import Button from "react-bootstrap/Button"
import Modal from "react-bootstrap/Modal"

interface ModalProps {
    title: string
    content: string
    dismiss: {
        text: string
        onDismiss: () => void
    }
    onClose: () => void
}

const BasicModalDialog: React.FC<ModalProps> = ({
                                              title, content,
                                              dismiss,
                                              onClose
                                          }) => <Modal.Dialog>
    <Modal.Header closeButton closeLabel="Close modal" onHide={onClose}>
        <Modal.Title>{title}</Modal.Title>
    </Modal.Header>
    <Modal.Body>
        {content}
    </Modal.Body>
    <Modal.Footer>
        <Button aria-label="Cancel modal" onClick={onClose}>{dismiss.text}</Button>
        <Button aria-label="Dismiss modal" onClick={dismiss.onDismiss}>{dismiss.text}</Button>
    </Modal.Footer>
</Modal.Dialog>

BasicModalDialog.propTypes = {
    title: PropTypes.string.isRequired,
    content: PropTypes.string.isRequired,
    dismiss: PropTypes.shape({
        text: PropTypes.string.isRequired,
        onDismiss: PropTypes.func.isRequired
    }).isRequired,
    onClose: PropTypes.func.isRequired
}

export default BasicModalDialog