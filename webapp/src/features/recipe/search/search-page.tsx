import React, {useContext, useEffect, useState} from "react"
import {SearchResult} from "../../../model"
import RecipeSearchList from "./search-list"
import {Grid, Typography, Button} from "@material-ui/core"
import {makeStyles, createStyles, Theme} from "@material-ui/core/styles"
import RecipeSearchForm, {RecipeSearchFormData} from "./search-form"
import createRecipeService, {RecipeDetails} from "../../../services/recipe-service"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "../../../services/api-handler"

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        base: {
            color: theme.palette.text.primary,
        },
    }),
)

const RecipeSearchPage: React.FC = () => {
    const [recipeTypes, setRecipeTypes] = useState<Array<RecipeType>>([])
    const [formData, setFormData] = useState<RecipeSearchFormData>({
        name: undefined,
        description: undefined,
        recipeTypeId: 0
    })
    const [pageParams, setPageParams] = useState<{
        pageNumber: number
        itemsPerPage: number
    }>({
        pageNumber: 0,
        itemsPerPage: 10
    })
    const [recipes, setRecipes] = useState<SearchResult<RecipeDetails>>({
        count: 0,
        numberOfPages: 0,
        results: []
    })

    const classes = useStyles()
    const history = useHistory()

    const {search, delete: deleteRecipe} = createRecipeService(useContext(ApiHandlerContext))
    const {getAll: getAllRecipeTypes} = createRecipeTypeService(useContext(ApiHandlerContext))

    const handleOnFormSearch = (recipeSearchFormData: RecipeSearchFormData) =>
        setFormData(recipeSearchFormData)

    const handleOnPageChange = (pageNumber: number) =>
        setPageParams({...pageParams, pageNumber})

    const handleOnNumberOfRowsChange = (itemsPerPage: number) =>
        setPageParams({...pageParams, itemsPerPage})

    const navigateToCreateRecipe = () => history.push("/recipe/new")

    useEffect(() => {
        getAllRecipeTypes().then(setRecipeTypes)
    }, [])

    useEffect(() => {
        search({
            ...formData, ...pageParams,
            recipeTypeId: formData.recipeTypeId > 0 ? formData.recipeTypeId : undefined
        }).then(setRecipes)
    }, [formData, pageParams])

    return <Grid className={classes.base} container spacing={3}>
        <Grid item xs={11}>
            <Typography variant="h4">Search recipes</Typography>
        </Grid>
        <Grid item xs={1}>
            <Button variant="contained" color="primary" aria-label="Create new recipe"
                    onClick={navigateToCreateRecipe}>Create</Button>
        </Grid>
        <Grid item xs={12}>
            <RecipeSearchForm onSearch={handleOnFormSearch} recipeTypes={recipeTypes}/>
        </Grid>
        <Grid item xs={12}>
            <RecipeSearchList searchResult={recipes}
                              onDelete={(id) => deleteRecipe(id)}
                              onNumberOfRowsChange={handleOnNumberOfRowsChange}
                              onPageChange={handleOnPageChange}/>
        </Grid>
    </Grid>
}

export default RecipeSearchPage