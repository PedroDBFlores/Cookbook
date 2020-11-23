import React, {useContext, useRef, useState} from "react"
import PropTypes from "prop-types"
import {useAsync} from "react-async"
import {Button, ButtonGroup, Typography, Paper, Grid} from "@material-ui/core"
import {Delete, Edit} from "@material-ui/icons"
import If from "../../../components/flow-control/if"
import Modal from "../../../components/modal/modal"
import {createStyles, makeStyles, Theme} from "@material-ui/core/styles"
import {useSnackbar} from "notistack"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Choose, When} from "../../../components/flow-control/choose"
import {useHistory} from "react-router-dom"
import createRecipeService, {RecipeDetails as RecipeDetail} from "../../../services/recipe-service"

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
    }),
)

const RecipeDetails: React.FC<{ id: number }> = ({id}) => {
    const [showModal, setShowModal] = useState<boolean>(false)
    const history = useHistory()
    const classes = useStyles()
    const {enqueueSnackbar} = useSnackbar()

    const {find, delete: deleteRecipe} = createRecipeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeDetail>({
        promiseFn: findPromiseRef.current
    })

    const handleDelete = (id: number) => deleteRecipe(id)
        .then(() => {
            enqueueSnackbar(`Recipe ${id} was deleted`, {variant: "success"})
            history.push("/recipe")
        }).catch(err => enqueueSnackbar(`An error occurred while trying to delete this recipe: ${err.message}`, {variant: "error"}))

    const onEdit = (id: number) => history.push(`/recipe/${id}/edit`)

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
                    <Typography variant="h4">Recipe details</Typography>
                </Grid>
                <Grid item xs={12}>
                    <Paper className={classes.paper}>
                        <Grid container component="dl" spacing={2}>
                            <Grid item xs={12}>
                                <Typography component="dt" variant="h6">Id:</Typography>
                                <Typography component='dd' variant='body2'>{state.data?.id}</Typography>
                                <Typography component="dt" variant="h6">Name:</Typography>
                                <Typography component='dd' variant='body2'>{state.data?.name}</Typography>
                                <Typography component="dt" variant="h6">Description:</Typography>
                                <Typography component='dd' variant='body2'>{state.data?.description}</Typography>
                                <Typography component="dt" variant="h6">Ingredients:</Typography>
                                <Typography component='dd' variant='body2'>{state.data?.ingredients}</Typography>
                                <Typography component="dt" variant="h6">Preparing steps:</Typography>
                                <Typography component='dd' variant='body2'>{state.data?.preparingSteps}</Typography>
                            </Grid>
                        </Grid>
                        <ButtonGroup>
                            <Button aria-label={`Edit recipe with id ${state.data?.id}`}
                                    onClick={() => onEdit(Number(state.data?.id))}>
                                <Edit/>
                            </Button>
                            <Button aria-label={`Delete recipe with id ${state.data?.id}`}
                                    onClick={() => setShowModal(true)}>
                                <Delete/>
                            </Button>
                        </ButtonGroup>
                    </Paper>
                </Grid>
                <If condition={showModal}>
                    <Modal title="Question"
                           content="Are you sure you want to delete this recipe?"
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

RecipeDetails.propTypes = {
    id: PropTypes.number.isRequired
}

export default RecipeDetails