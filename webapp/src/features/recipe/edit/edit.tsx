import React, {useRef} from "react"
import PropTypes from "prop-types"
import {FormikValues} from "formik"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import createRecipeService, {Recipe, RecipeDetails} from "services/recipe-service"
import {useHistory} from "react-router-dom"
import {Text, useToast} from "@chakra-ui/react"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import Loader from "components/loader/loader"
import RecipeForm from "components/recipe-form/recipe-form"
import Section from "components/section/section"
import {useTranslation} from "react-i18next"

interface EditRecipeProps {
    id: number
}

const EditRecipe: React.VFC<EditRecipeProps> = ({id}) => {
    const {t} = useTranslation()
    const history = useHistory()
    const toast = useToast()

    const {getAll: getAllRecipeTypes} = createRecipeTypeService()
    const {find, update} = createRecipeService()
    const getAllRecipeTypesRef = useRef(getAllRecipeTypes)
    const getAllRecipeTypesState = useAsync<Array<RecipeType>>({
        promiseFn: getAllRecipeTypesRef.current,
        onResolve: () => findRecipeState.run(),
        onReject: ({message}) => toast({
            title: t("recipe-type-feature.errors.occurred-fetching"),
            description: message,
            status: "error",
            duration: 5000
        })
    })
    const findPromiseRef = useRef(() => find(id))
    const findRecipeState = useAsync<RecipeDetails>({
        deferFn: findPromiseRef.current,
        onReject: ({message}) => toast({
            title: t("recipe-feature.errors.occurred-fetching"),
            description: message,
            status: "error",
            duration: 5000
        })
    })

    const handleOnSubmit = async (formData: FormikValues) => {
        try {
            await update({...formData, recipeTypeId: Number(formData.recipeTypeId)} as Recipe)
            toast({
                    title: t("recipe-feature.update.success",
                        {name: formData.name}),
                    status: "success"
                }
            )
            history.push(`/recipe/${id}`)
        } catch ({message}) {
            toast(
                {
                    title:  t("recipe-feature.update.failure"),
                    description: message,
                    status: "error"
                }
            )
        }
    }

    return <Section title={t("recipe-feature.edit.title")}>
        <IfPending state={getAllRecipeTypesState}>
            <Loader/>
        </IfPending>
        <IfPending state={findRecipeState}>
            <Loader/>
        </IfPending>
        <IfRejected state={getAllRecipeTypesState}>
            <Text>{t("recipe-type-feature.errors.cannot-load")}</Text>
        </IfRejected>
        <IfRejected state={findRecipeState}>
            <Text>{t("recipe-type-feature.errors.cannot-load")}</Text>
        </IfRejected>
        <IfFulfilled state={getAllRecipeTypesState}>
            {
                recipeTypes => <IfFulfilled state={findRecipeState}>
                    {data => <RecipeForm recipeTypes={recipeTypes} initialValues={data} onSubmit={handleOnSubmit}/>}
                </IfFulfilled>
            }
        </IfFulfilled>
    </Section>
}

EditRecipe.propTypes = {
    id: PropTypes.number.isRequired
}

export default EditRecipe
