import React, {useContext, useRef, useState} from "react"
import PropTypes from "prop-types"
import {useAsync} from "react-async"
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
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {useSnackbar} from "notistack"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Choose, When} from "../../../components/flow-control/choose"

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
    }),
)

const RecipeTypeDetails: React.FC<{ id: number }> = ({id}) => {
    const [showModal, setShowModal] = useState<boolean>(false)
    const history = useHistory()
    const classes = useStyles()
    const {enqueueSnackbar} = useSnackbar()

    const {find, delete: deleteRecipeType} = createRecipeTypeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    const handleDelete = (id: number) => deleteRecipeType(id)
        .then(() => {
            enqueueSnackbar(`Recipe type ${id} was deleted`, {variant: "success"})
            history.push("/recipetype")
        })

    const onEdit = (id: number) => history.push(`/recipetype/${id}/edit`)

    return <Choose>
        <When condition={state.isLoading}>
            <span>Loading...</span>
        </When>
        <When condition={state.isRejected}>
            <span>Error: {state.error?.message}</span>
        </When>
        <When condition={state.isFulfilled}>
            <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Typography variant="h4">Recipe type details</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Paper className={classes.paper}>
                            <Grid container component="dl" spacing={2}>
                                <Grid item xs={12}>
                                    <Typography component="dt" variant="h6">Id:</Typography>
                                    <Typography component='dd' variant='body2'>{state.data?.id}</Typography>
                                    <Typography component="dt" variant="h6">Name:</Typography>
                                    <Typography component='dd' variant='body2'>{state.data?.name}</Typography>
                                </Grid>
                            </Grid>
                            <ButtonGroup>
                                <Button aria-label={`Edit recipe type with id ${state.data?.id}`}
                                        onClick={() => onEdit(Number(state.data?.id))}>
                                    <Edit/>
                                </Button>
                                <Button aria-label={`Delete recipe type with id ${state.data?.id}`}
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
                                              onDismiss: () => handleDelete(Number(state.data?.id))
                                          }}
                                          onClose={() => setShowModal(false)}/>
                    </If>
                </Grid>
        </When>
    </Choose>
}
RecipeTypeDetails.propTypes = {
    id: PropTypes.number.isRequired
}

export default RecipeTypeDetails
