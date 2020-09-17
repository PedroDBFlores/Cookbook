import React, {useEffect, useState} from "react"
import PropTypes from "prop-types"
import SearchRecipeParameters from "../../../model/search-recipe-parameters"
import {RecipeDetails} from "../../../model"
import RecipeSearchList from "./search-list"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import RecipeType from "../../../model/recipe-type"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import RecipeSearchForm, {RecipeSearchFormData} from "./search-form"
import SearchResult from "../../../model/search-result"

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
        recipeTypeId: undefined
    })
    const [recipes, setRecipes] = useState<SearchResult<RecipeDetails>>({
        count: 0,
        numberOfPages: 0,
        results: []
    })

    const classes = useStyles()

    const handleOnFormSearch = (data: RecipeSearchFormData) => {
        setFormData(data)
        searchFn({
            ...data,
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
RecipeSearchPage.propTypes = {
    getAllRecipeTypesFn: PropTypes.func.isRequired,
    searchFn: PropTypes.func.isRequired
}

export default RecipeSearchPage