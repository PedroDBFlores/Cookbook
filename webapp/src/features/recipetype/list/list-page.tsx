import React, {useContext, useRef} from "react"
import RecipeTypeList from "./list"
import {useAsync} from "react-async"
import {useHistory} from "react-router-dom"
import {Typography, Grid, Button, Paper} from "@material-ui/core"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {useSnackbar} from "notistack"
import {ApiHandlerContext} from "../../../services/api-handler"
import {makeStyles, createStyles, Theme} from "@material-ui/core/styles"
import {Choose, When} from "../../../components/flow-control/choose"

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
    }),
)

const RecipeTypeListPage: React.FC = () => {
    const {getAll, delete: deleteRecipeType} = createRecipeTypeService(useContext(ApiHandlerContext))
    const history = useHistory()
    const classes = useStyles()
    const {enqueueSnackbar} = useSnackbar()

    const getPromiseRef = useRef(() => getAll())
    const handleDelete = (id: number) => deleteRecipeType(id).then(() => enqueueSnackbar(`Recipe type ${id} was deleted`, {variant: "success"}))
    const state = useAsync<Array<RecipeType>>({
        promiseFn: getPromiseRef.current
    })

    const navigateToCreateRecipeType = () => history.push("/recipetype/new")

    return <Grid container spacing={3}>
        <Grid item xs={11}>
            <Typography variant="h4">Recipe types</Typography>
        </Grid>
        <Grid item xs={1}>
            <Button variant="contained" color="primary" aria-label="Create new recipe type"
                    onClick={navigateToCreateRecipeType}>Create</Button>
        </Grid>
        <Grid item xs={12}>
            <Choose>
                <When condition={state.isPending}>
                    <Paper className={classes.paper}>
                        <span>Loading...</span>
                    </Paper>
                </When>
                <When condition={state.isRejected}>
                    <Paper className={classes.paper}>
                        <span>Error: {state.error?.message}</span>
                    </Paper>
                </When>
                <When condition={state.isFulfilled}>
                    <RecipeTypeList recipeTypes={state.data || []} onDelete={(id => handleDelete(id))}/>
                </When>
            </Choose>
        </Grid>
    </Grid>
}

export default RecipeTypeListPage
