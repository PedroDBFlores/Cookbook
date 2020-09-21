import React, {useEffect, useState} from "react"
import {SearchResult} from "../../../model"
import RecipeSearchList from "./search-list"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import RecipeSearchForm, {RecipeSearchFormData} from "./search-form"
import {RecipeDetails, SearchRecipeParameters} from "../../../services/recipe-service"
import {RecipeType} from "../../../services/recipe-type-service"

interface RecipeSearchPageProps {
    getAllRecipeTypesFn: () => Promise<Array<RecipeType>>
    searchFn: (parameters: SearchRecipeParameters) => Promise<SearchResult<RecipeDetails>>
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        base: {
            color: theme.palette.text.primary,
        },
    }),
)

const RecipeSearchPage: React.FC<RecipeSearchPageProps> = ({getAllRecipeTypesFn, searchFn}) => {
    const [recipeTypes, setRecipeTypes] = useState<Array<RecipeType>>([])
    const [formData, setFormData] = useState<RecipeSearchFormData>({
        name: undefined,
        description: undefined,
        recipeTypeId: 0
    })
    const [recipes, setRecipes] = useState<SearchResult<RecipeDetails>>({
        count: 0,
        numberOfPages: 0,
        results: []
    })

    const classes = useStyles()

    const handleOnFormSearch = ({name, description, recipeTypeId}: RecipeSearchFormData) => {
        setFormData({name, description, recipeTypeId})
        searchFn({
            name,
            description,
            recipeTypeId: recipeTypeId > 0 ? recipeTypeId : undefined,
            pageNumber: 0,
            itemsPerPage: 10
        }).then(setRecipes)
    }

    const handleOnPageChange = (page: number) => {
        searchFn({
            ...formData,
            pageNumber: page,
            itemsPerPage: 10
        }).then(setRecipes)
    }

    useEffect(() => {
        getAllRecipeTypesFn().then(setRecipeTypes)
    }, [])

    return <Grid className={classes.base} container spacing={3}>
        <Grid item xs={11}>
            <Typography variant="h4">Search recipes</Typography>
        </Grid>
        <Grid item xs={12}>
            <RecipeSearchForm onSearch={handleOnFormSearch} recipeTypes={recipeTypes}/>
        </Grid>
        <Grid item xs={12}>
            <RecipeSearchList searchResult={recipes} onDelete={() => {
            }} onPageChange={handleOnPageChange}/>
        </Grid>
    </Grid>
}

export default RecipeSearchPage