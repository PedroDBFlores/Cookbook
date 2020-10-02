import React, {useContext, useRef} from "react"
import RecipeTypeList from "./list"
import {useAsync, IfPending, IfRejected, IfFulfilled} from "react-async"
import {useHistory} from "react-router-dom"
import Typography from "@material-ui/core/Typography"
import Grid from "@material-ui/core/Grid"
import Button from "@material-ui/core/Button"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {useSnackbar} from "notistack"
import {ApiHandlerContext} from "../../../services/api-handler"

const RecipeTypeListPage: React.FC = () => {
    const {getAll, delete: deleteRecipeType} = createRecipeTypeService(useContext(ApiHandlerContext))
    const history = useHistory()
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
            <IfPending state={state}>
                <span>Loading...</span>
            </IfPending>
            <IfRejected state={state}>
                {(error) => <span>Error: {error.message}</span>}
            </IfRejected>
            <IfFulfilled state={state}>
                {(data) =>
                    <RecipeTypeList recipeTypes={data} onDelete={(id => handleDelete(id))}/>}
            </IfFulfilled>
        </Grid>
    </Grid>
}

export default RecipeTypeListPage
