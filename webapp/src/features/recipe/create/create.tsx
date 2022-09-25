import React, {useRef} from "react"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import createRecipeService, {Recipe} from "services/recipe-service"
import {useNavigate} from "react-router-dom"
import {Text, useToast} from "@chakra-ui/react"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import Loader from "components/loader/loader"
import RecipeForm from "components/recipe-form/recipe-form"
import Section from "components/section/section"
import {useTranslation} from "react-i18next"

const CreateRecipe: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const toast = useToast()

    const {create} = createRecipeService()
    const {getAll: getAllRecipeTypes} = createRecipeTypeService()
    const getAllRecipeTypesFn = useRef(() => getAllRecipeTypes())
    const state = useAsync<RecipeType[]>({
        promiseFn: getAllRecipeTypesFn.current,
        onReject: ({message}) => toast({
            title: t("recipe-feature.errors.occurred-fetching"),
            description: <>{message}</>,
            status: "error",
            duration: 5000
        })
    })

    const handleOnSubmit = async (recipe: Omit<Recipe, "id">) => {
        try {
            const {id} = await create(recipe)

            toast({title: t("recipe-feature.create.success", {name: recipe.name}), status: "success"})
            navigate(`/recipe/${id}/details`)
        } catch ({message}) {
            toast({
                title: t("recipe-type-feature.create.failure"),
                description: <>{message}</>,
                status: "error",
                duration: 5000
            })
        }
    }

    return <Section title={t("recipe-feature.create-label")}>
        <IfPending state={state}>
            <Loader/>
        </IfPending>
        <IfRejected state={state}>
            <Text>{t("recipe-feature.errors.cannot-load")}</Text>
        </IfRejected>
        <IfFulfilled state={state}>
            {recipeTypes => <RecipeForm recipeTypes={recipeTypes} onSubmit={handleOnSubmit}/>}
        </IfFulfilled>
    </Section>
}

export default CreateRecipe
