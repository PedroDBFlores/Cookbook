import React, { useEffect, useRef, useState } from "react"
import { SearchResult } from "model"
import RecipeSearchForm, { RecipeSearchFormData } from "./search-form"
import createRecipeService, { RecipeDetails } from "services/recipe-service"
import createRecipeTypeService, { RecipeType } from "services/recipe-type-service"
import { useNavigate } from "react-router-dom"
import { Button, Grid, GridItem, Text, useToast } from "@chakra-ui/react"
import useModalContext from "components/modal/modal-context"
import { IfFulfilled, IfPending, IfRejected, useAsync } from "react-async"
import Loader from "components/loader/loader"
import RecipeSearchList from "./search-list"
import Section from "components/section/section"
import { useTranslation } from "react-i18next"

const RecipeSearchPage: React.VFC = () => {
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

    const { t } = useTranslation()
    const toast = useToast()
    const { openModal, closeModal } = useModalContext()
    const navigate = useNavigate()

    const { search, delete: deleteRecipe } = createRecipeService()
    const { getAll: getAllRecipeTypes } = createRecipeTypeService()
    const getAllRecipeTypesRef = useRef(getAllRecipeTypes)
    const getAllRecipeTypesState = useAsync<Array<RecipeType>>({
        promiseFn: getAllRecipeTypesRef.current,
        onReject: ({ message }) => toast({
            title: t("recipe-feature.errors.occurred-fetching-recipe-types"),
            description: <>{message}</>,
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

    const navigateToCreateRecipe = () => navigate("/recipe/new")

    const showModal = (id: number, name: string) => openModal({
        title: t("common.question"),
        content: t("recipe-feature.delete.question", { name }),
        actionText: t("common.delete"),
        onAction: () => handleDelete(id, name),
        onClose: closeModal
    })

    const handleDelete = async (id: number, name: string) => {
        try {
            await deleteRecipe(id)
            toast({ title: t("recipe-feature.delete.success", { name }), status: "success" })
            setRecipes({ ...recipes, results: recipes.results.filter(r => r.id != id) })
        } catch ({ message }) {
            toast({
                title: t("recipe-feature.delete.failure", { name }),
                description: <>{message}</>,
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

    return <Section title={t("recipe-feature.search.title")}
        actions={<Button aria-label={t("recipe-feature.create-label")}
            onClick={navigateToCreateRecipe}>{t("common.create")}</Button>}>
        <IfPending state={getAllRecipeTypesState}>
            <Loader />
        </IfPending>
        <IfRejected state={getAllRecipeTypesState}>
            <Text>{t("recipe-feature.errors.cannot-load-recipe-types")}</Text>
        </IfRejected>
        <IfFulfilled state={getAllRecipeTypesState}>
            {recipeTypes =>
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
            }
        </IfFulfilled>
    </Section>
}

export default RecipeSearchPage
