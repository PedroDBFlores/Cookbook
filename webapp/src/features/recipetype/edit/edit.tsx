import React, {useRef} from "react"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {useHistory} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import {Text, useToast} from "@chakra-ui/react"
import Loader from "components/loader/loader"
import RecipeTypeForm from "components/recipe-type-form/recipe-type-form"
import Section from "components/section/section"
import {useTranslation} from "react-i18next"

const EditRecipeType: React.VFC<{ id: number }> = ({id}) => {
    const {t} = useTranslation()
    const toast = useToast()
    const history = useHistory()

    const {find, update} = createRecipeTypeService()
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current,
        onReject: ({message}) => toast({
            title: t("recipe-type-feature.errors.occurred-fetching"),
            description: <>{message}</>,
            status: "error",
            duration: 5000
        })
    })

    const handleOnSubmit = async (recipeType: RecipeType) => {
        try {
            await update(recipeType)
            toast({title: t("recipe-type-feature.update.success", {name: recipeType.name}), status: "success"})
            history.push(`/recipetype/${id}`)
        } catch ({message}) {
            toast({
                title: t("recipe-type-feature.update.failure"),
                description: <>{message}</>,
                status: "error",
                duration: 5000
            })
        }
    }

    return <Section title={t("recipe-type-feature.edit.title")}>
        <IfPending state={state}>
            <Loader/>
        </IfPending>
        <IfRejected state={state}>
            <Text>{t("recipe-type-feature.errors.cannot-load")}</Text>
        </IfRejected>
        <IfFulfilled state={state}>
            {data => <RecipeTypeForm initialValues={data} onSubmit={handleOnSubmit}/>}
        </IfFulfilled>
    </Section>
}

export default EditRecipeType
