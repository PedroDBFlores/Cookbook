import React, { useContext, useEffect, useRef, useState } from "react"
import { SearchResult } from "model"
import RecipeSearchForm, { RecipeSearchFormData } from "./search-form"
import createRecipeService, { RecipeDetails } from "services/recipe-service"
import createRecipeTypeService, { RecipeType } from "services/recipe-type-service"
import { useHistory } from "react-router-dom"
import { ApiHandlerContext } from "services/api-handler"
import { Button, Grid, GridItem, Text, useToast } from "@chakra-ui/react"
import ModalContext from "components/modal/modal-context"
import { IfFulfilled, IfPending, IfRejected, useAsync } from "react-async"
import Loader from "components/loader/loader"
import RecipeSearchList from "./search-list"
import Section from "components/section/section"

const RecipeSearchPage: React.FC = () => {
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
    const { setModalState } = useContext(ModalContext)
    const history = useHistory()

    const { search, delete: deleteRecipe } = createRecipeService(useContext(ApiHandlerContext))
    const { getAll: getAllRecipeTypes } = createRecipeTypeService(useContext(ApiHandlerContext))
    const getAllRecipeTypesRef = useRef(getAllRecipeTypes)
    const getAllRecipeTypesState = useAsync<Array<RecipeType>>({
        promiseFn: getAllRecipeTypesRef.current,
        onReject: ({ message }) => toast({
            title: "An error occurred while fetching the recipe types",
            description: message,
            status: "error",
            duration: 5000
        })
    })

    const handleOnFormSearch = (recipeSearchFormData: RecipeSearchFormData) =>
        setFormData(recipeSearchFormData)

    const handleOnPageChange = (pageNumber: number) =>
        setPageParams({ ...pageParams, pageNumber })

    const handleOnNumberOfRowsChange = (itemsPerPage: number) =>
        setPageParams({ ...pageParams, itemsPerPage, pageNumber: 1 })

    const navigateToCreateRecipe = () => history.push("/recipe/new")

    const showModal = (id: number, name: string) => {
        setModalState({
            isOpen: true,
            props: {
                title: "Question",
                content: `Are you sure you want to delete recipe '${name}'?`,
                actionText: "Delete",
                onClose: () => setModalState({ isOpen: false }),
                onAction: () => handleDelete(id, name)
            }
        })
    }

    const handleDelete = async(id: number, name: string) => {
        try {
            await deleteRecipe(id)
            toast({ title: `Recipe '${name}' was deleted`, status: "success" })
            setRecipes({ ...recipes, results: recipes.results.filter(r => r.id != id) })
        } catch ({ message }) {
            toast({
                title: `An error occurred while trying to delete recipe '${name}'`,
                description: message,
                status: "error",
                duration: 5000
            })
        }
    }

    useEffect(() => {
        search({
            ...formData,
            ...pageParams,
            recipeTypeId: formData.recipeTypeId > 0 ? formData.recipeTypeId : undefined
        }).then(setRecipes)
    }, [formData, pageParams])

    return <Section title="Search recipes" actions={<Button aria-label="Create new recipe"
        onClick={navigateToCreateRecipe}>Create</Button>}>
        <IfPending state={getAllRecipeTypesState}>
            <Loader />
        </IfPending>
        <IfRejected state={getAllRecipeTypesState}>
            <Text>Failed to fetch the recipe types</Text>
        </IfRejected>
        <IfFulfilled state={getAllRecipeTypesState}>
            {recipeTypes => <>
                <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                    <GridItem colSpan={12}>
                        <RecipeSearchForm onSearch={handleOnFormSearch} recipeTypes={recipeTypes} />
                    </GridItem>
                    <GridItem colSpan={12}>
                        <RecipeSearchList searchResult={recipes}
                            onDelete={(id, name) => showModal(id, name)}
                            onChangeRowsPerPage={handleOnNumberOfRowsChange}
                            onPageChange={handleOnPageChange} />
                    </GridItem>
                </Grid>
            </>
            }
        </IfFulfilled>
    </Section>
}

export default RecipeSearchPage
