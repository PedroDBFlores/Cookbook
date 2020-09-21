import React, {useRef, useState} from "react"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import Button from "@material-ui/core/Button"
import Delete from "@material-ui/icons/Delete"
import Edit from "@material-ui/icons/Edit"
import {useHistory} from "react-router-dom"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import Paper from "@material-ui/core/Paper"
import ButtonGroup from "@material-ui/core/ButtonGroup"
import If from "../../../components/flow-control/if"
import BasicModalDialog from "../../../components/modal/basic-modal-dialog"
import createStyles from "@material-ui/core/styles/createStyles"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import {RecipeType} from "../../../services/recipe-type-service"

interface RecipeTypeDetailsProps {
    id: number
    onFind: (id: number) => Promise<RecipeType>
    onDelete: (id: number) => Promise<void>
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
    }),
)

const RecipeTypeDetails: React.FC<RecipeTypeDetailsProps> = ({id, onFind, onDelete}) => {
    const history = useHistory()
    const classes = useStyles()

    const findPromiseRef = useRef(() => onFind(id))
    const [showModal, setShowModal] = useState<boolean>(false)
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    const handleDelete = (id: number) => onDelete(id)
        .then(() => history.push("/recipetype"))

    const onEdit = (id: number) => history.push(`/recipetype/${id}/edit`)

    return <>
        <IfPending state={state}>
            <span>Loading...</span>
        </IfPending>
        <IfRejected state={state}>
            {(error) => <span>Error: {error.message}</span>}
        </IfRejected>
        <IfFulfilled state={state}>
            {(data) => (
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Typography variant="h4">Recipe type details</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Paper className={classes.paper}>
                            <Grid container component="dl" spacing={2}>
                                <Grid item xs={12}>
                                    <Typography component="dt" variant="h6">Id:</Typography>
                                    <Typography component='dd' variant='body2'>{data.id}</Typography>
                                    <Typography component="dt" variant="h6">Name:</Typography>
                                    <Typography component='dd' variant='body2'>{data.name}</Typography>
                                </Grid>
                            </Grid>
                            <ButtonGroup>
                                <Button aria-label={`Edit recipe type with id ${data.id}`}
                                        onClick={() => onEdit(data.id)}>
                                    <Edit/>
                                </Button>
                                <Button aria-label={`Delete recipe type with id ${data.id}`}
                                        onClick={() => setShowModal(true)}>
                                    <Delete/>
                                </Button>
                            </ButtonGroup>
                        </Paper>
                    </Grid>
                    <If condition={showModal}>
                        <BasicModalDialog title="Question"
                                          content="Are you sure you want to delete this recipe type?"
                                          dismiss={{
                                              text: "Delete",
                                              onDismiss: () => handleDelete(data.id)
                                          }}
                                          onClose={() => setShowModal(false)}/>
                    </If>
                </Grid>
            )}
        </IfFulfilled>
    </>
}

export default RecipeTypeDetails
