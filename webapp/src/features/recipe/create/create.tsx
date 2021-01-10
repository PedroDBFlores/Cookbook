import React, { useContext, useRef } from "react"
import createRecipeTypeService, { RecipeType } from "services/recipe-type-service"
import createRecipeService, { Recipe } from "services/recipe-service"
import { useHistory } from "react-router-dom"
import { ApiHandlerContext } from "services/api-handler"
import { Text, useToast } from "@chakra-ui/react"
import { IfFulfilled, IfPending, IfRejected, useAsync } from "react-async"
import Loader from "components/loader/loader"
import RecipeForm from "components/recipe-form/recipe-form"
import Section from "components/section/section"

const CreateRecipe: React.FC = () => {
    const history = useHistory()
    const toast = useToast()

    const { create } = createRecipeService(useContext(ApiHandlerContext))
    const { getAll: getAllRecipeTypes } = createRecipeTypeService(useContext(ApiHandlerContext))
    const getAllRecipeTypesFn = useRef(() => getAllRecipeTypes())
    const state = useAsync<RecipeType[]>({
        promiseFn: getAllRecipeTypesFn.current,
        onReject: ({ message }) => toast({
            title: "An error occurred while fetching the recipe types",
            description: message,
            status: "error",
            duration: 5000
        })
    })

    const handleOnSubmit = async(recipe: Omit<Recipe, "id">) => {
        try {
            const { id } = await create(recipe)

            toast({ title: `Recipe '${recipe.name}' created successfully!`, status: "success" })
            history.push(`/recipe/${id}/details`)
        } catch ({ message }) {
            toast({
                title: "An error occurred while creating the recipe",
                description: message,
                status: "error",
                duration: 5000
            })
        }
    }

    return <Section title="Create a new recipe">
        <IfPending state={state}>
            <Loader />
        </IfPending>
        <IfRejected state={state}>
            <Text>Failed to fetch the recipe types</Text>
        </IfRejected>
        <IfFulfilled state={state}>
            {recipeTypes => <RecipeForm recipeTypes={recipeTypes} onSubmit={handleOnSubmit} />}
        </IfFulfilled>
    </Section>
}

export default CreateRecipe
