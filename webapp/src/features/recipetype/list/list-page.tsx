import React, {useContext, useRef} from "react"
import RecipeTypeList from "./list"
import {useAsync} from "react-async"
import {useHistory} from "react-router-dom"
import Typography from "@material-ui/core/Typography"
import Grid from "@material-ui/core/Grid"
import Button from "@material-ui/core/Button"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {useSnackbar} from "notistack"
import {ApiHandlerContext} from "../../../services/api-handler"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import Paper from "@material-ui/core/Paper"
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
            <Paper className={classes.paper}>
                <Choose>
                    <When condition={state.isPending}>
                        <span>Loading...</span>
                    </When>
                    <When condition={state.isRejected}>
                        <span>Error: {state.error?.message}</span>
                    </When>
                    <When condition={state.isFulfilled}>
                        <RecipeTypeList recipeTypes={state.data || []} onDelete={(id => handleDelete(id))}/>
                    </When>
                </Choose>
            </Paper>
        </Grid>
    </Grid>
}

export default RecipeTypeListPage
