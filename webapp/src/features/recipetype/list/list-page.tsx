import React, { useRef } from "react"
import RecipeTypeList from "./list"
import { IfFulfilled, IfPending, IfRejected, useAsync } from "react-async"
import { useHistory } from "react-router-dom"
import createRecipeTypeService, { RecipeType } from "services/recipe-type-service"
import { Button, useToast, Text } from "@chakra-ui/react"
import Loader from "components/loader/loader"
import Section from "components/section/section"
import {useTranslation} from "react-i18next"

const RecipeTypeListPage: React.VFC = () => {
    const {t} = useTranslation()
    const { getAll, delete: deleteRecipeType } = createRecipeTypeService()
    const history = useHistory()
    const toast = useToast()

    const getPromiseRef = useRef(() => getAll())
    const state = useAsync<Array<RecipeType>>({
        promiseFn: getPromiseRef.current,
        onReject: ({ message }) => toast({
            title: t("recipe-type-feature.errors.occurred-fetching"),
            description: <>{message}</>,
            status: "error",
            duration: 5000
        })
    })
    
    const handleDelete = async(id: number, name: string) => {
        try {
            await deleteRecipeType(id)
            state.reload()
            toast({
                title: t("recipe-type-feature.delete.success", {name}),
                status: "success",
                duration: 5000
            })
        } catch ({ message }) {
            toast({
                title: t("recipe-type-feature.delete.failure", {name}),
                description: <>{message}</>,
                status: "error",
                duration: 5000
            })
        }
    }

    const navigateToCreateRecipeType = () => history.push("/recipetype/new")

    return <Section title={t("recipe-type-feature.plural")} actions={<Button aria-label={t("recipe-type-feature.create-label")}
        onClick={navigateToCreateRecipeType}>{t("common.create")}</Button>}>
        <IfPending state={state}>
            <Loader />
        </IfPending>
        <IfRejected state={state}>
            <Text>{t("recipe-type-feature.errors.cannot-load")}</Text>
        </IfRejected>
        <IfFulfilled state={state}>
            {data => <RecipeTypeList recipeTypes={data} onDelete={handleDelete} />}
        </IfFulfilled>
    </Section >
}

export default RecipeTypeListPage
