import React from "react"
import {useNavigate} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import {useToast} from "@chakra-ui/react"
import RecipeTypeForm from "../../../components/recipe-type-form/recipe-type-form"
import Section from "components/section/section"
import {useTranslation} from "react-i18next"

const CreateRecipeType: React.FC = () => {
    const {t} = useTranslation()
    const {create} = createRecipeTypeService()
    const toast = useToast()
    const navigate = useNavigate()

    const handleOnSubmit = async ({name}: Omit<RecipeType, "id">) => {
        try {
            const {id} = await create({name})

            toast({
                title: t("recipe-type-feature.create.success", {name}), status: "success"
            })
            navigate(`/recipetype/${id}/details`)
        } catch ({message}) {
            toast({
                title: t("recipe-type-feature.create.failure"),
                description: <>{message}</>,
                status: "error",
                duration: 5000
            })
        }
    }

    return <Section title={t("recipe-type-feature.create-label")}>
        <RecipeTypeForm onSubmit={handleOnSubmit}/>
    </Section>
}

export default CreateRecipeType
