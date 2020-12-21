import React, {useContext, useEffect, useState} from "react"
import {SearchResult} from "../../../model"
import RecipeSearchList from "./search-list"
import RecipeSearchForm, {RecipeSearchFormData} from "./search-form"
import createRecipeService, {RecipeDetails} from "../../../services/recipe-service"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Button, Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import ModalContext from "../../../components/modal/modal-context"

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

    const toast = useToast()
    const {setModalState} = useContext(ModalContext)
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

    const showModal = (id: number, name: string) => {
        setModalState({
            isOpen: true,
            props: {
                title: "Question",
                content: `Are you sure you want to delete recipe '${name}'?`,
                actionText: "Delete",
                onClose: () => setModalState({isOpen: false}),
                onAction: () => handleDelete(id, name)
            }
        })
    }

    const handleDelete = async (id: number, name: string) => {
        try {
            await deleteRecipe(id)
            toast({title: `Recipe '${name}' was deleted`, status: "success"})
            setRecipes({...recipes, results: recipes.results.filter(r => r.id != id)})
        } catch ({message}) {
            toast({
                title: `An error occurred while trying to delete recipe '${name}': ${message}`,
                status: "error"
            })
        }
    }

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
                              onDelete={(id, name) => showModal(id, name)}
                              onChangeRowsPerPage={handleOnNumberOfRowsChange}
                              onPageChange={handleOnPageChange}/>
        </GridItem>
    </Grid>
}

export default RecipeSearchPage