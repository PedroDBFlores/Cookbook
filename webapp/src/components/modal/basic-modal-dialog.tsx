import React from "react"
import Dialog from "@material-ui/core/Dialog"
import DialogTitle from "@material-ui/core/DialogTitle"
import DialogContent from "@material-ui/core/DialogContent"
import DialogActions from "@material-ui/core/DialogActions"
import Button from "@material-ui/core/Button"

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
                                                }) => <Dialog open={true} onClose={onClose}>
    <DialogTitle>{title}</DialogTitle>
    <DialogContent>
        {content}
    </DialogContent>
    <DialogActions>
        <Button aria-label="Cancel modal" onClick={onClose}>Close</Button>
        <Button aria-label="Dismiss modal" onClick={dismiss.onDismiss}>{dismiss.text}</Button>
    </DialogActions>
</Dialog>

export default BasicModalDialog