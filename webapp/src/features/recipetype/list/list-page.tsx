import React, {useRef} from "react"
import RecipeTypeList from "./list"
import {deleteRecipeType, getAllRecipeTypes} from "../../../services/recipe-type-service"
import {RecipeType} from "../../../dto"
import {useAsync, IfPending, IfRejected, IfFulfilled} from "react-async"
import {useHistory} from "react-router-dom"
import {Button, Grid, Typography} from "@material-ui/core"

const RecipeTypeListPage: React.FC<unknown> = () => {
    const history = useHistory()
    const getPromiseRef = useRef(() => getAllRecipeTypes())
    const onDelete = (id: number) => deleteRecipeType(id)
    const state = useAsync<Array<RecipeType>>({
        promiseFn: getPromiseRef.current
    })

    const navigateToCreateRecipeType = () => history.push("/recipetype/new")

    return <Grid container spacing={3}>
        <Grid item xs={11}>
            <Typography variant="h4">Recipe types</Typography>
        </Grid>
        <Grid item xs={1}>
            <Button variant="contained" aria-label="Create new recipe type"
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
                    <RecipeTypeList recipeTypes={data} onDelete={(id => onDelete(id))}/>}
            </IfFulfilled>
        </Grid>
    </Grid>
}

export default RecipeTypeListPage
