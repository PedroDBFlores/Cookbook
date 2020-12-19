import React, {useContext, useEffect, useState} from "react"
import {SearchResult} from "../../../model"
import RecipeSearchList from "./search-list"
import RecipeSearchForm, {RecipeSearchFormData} from "./search-form"
import createRecipeService, {RecipeDetails} from "../../../services/recipe-service"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Button, Grid, GridItem, Heading, Text} from "@chakra-ui/react"

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
        pageNumber: 1,
        itemsPerPage: 10
    })
    const [recipes, setRecipes] = useState<SearchResult<RecipeDetails>>({
        count: 0,
        numberOfPages: 0,
        results: []
    })

    const history = useHistory()

    const {search, delete: deleteRecipe} = createRecipeService(useContext(ApiHandlerContext))
    const {getAll: getAllRecipeTypes} = createRecipeTypeService(useContext(ApiHandlerContext))

    const handleOnFormSearch = (recipeSearchFormData: RecipeSearchFormData) =>
        setFormData(recipeSearchFormData)

    const handleOnPageChange = (pageNumber: number) =>
        setPageParams({...pageParams, pageNumber})

    const handleOnNumberOfRowsChange = (itemsPerPage: number) =>
        setPageParams({...pageParams, itemsPerPage, pageNumber: 1})

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

    return <Grid templateColumns="repeat(12, 1fr)" gap={6}>
        <GridItem colSpan={11}>
            <Heading>Search recipes</Heading>
        </GridItem>
        <GridItem colSpan={1}>
            <Button aria-label="Create new recipe"
                    onClick={navigateToCreateRecipe}>Create</Button>
        </GridItem>
        <GridItem colSpan={12}>
            <RecipeSearchForm onSearch={handleOnFormSearch} recipeTypes={recipeTypes}/>
        </GridItem>
        <GridItem colSpan={12}>
            <RecipeSearchList searchResult={recipes}
                              onDelete={(id) => deleteRecipe(id)}
                              onChangeRowsPerPage={handleOnNumberOfRowsChange}
                              onPageChange={handleOnPageChange}/>
        </GridItem>
    </Grid>
}

export default RecipeSearchPage