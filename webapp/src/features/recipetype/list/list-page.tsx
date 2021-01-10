import React, { useContext, useRef } from "react"
import RecipeTypeList from "./list"
import { IfFulfilled, IfPending, IfRejected, useAsync } from "react-async"
import { useHistory } from "react-router-dom"
import createRecipeTypeService, { RecipeType } from "services/recipe-type-service"
import { ApiHandlerContext } from "services/api-handler"
import { Button, useToast, Text } from "@chakra-ui/react"
import Loader from "components/loader/loader"
import Section from "components/section/section"

const RecipeTypeListPage: React.FC = () => {
    const { getAll, delete: deleteRecipeType } = createRecipeTypeService(useContext(ApiHandlerContext))
    const history = useHistory()
    const toast = useToast()

    const getPromiseRef = useRef(() => getAll())
    const state = useAsync<Array<RecipeType>>({
        promiseFn: getPromiseRef.current,
        onReject: ({ message }) => toast({
            title: "An error occurred while fetching the recipe types",
            description: message,
            status: "error",
            duration: 5000
        })
    })
    const handleDelete = async (id: number, name: string) => {
        try {
            await deleteRecipeType(id)
            state.reload()
            toast({
                title: `Recipe type '${name}' was deleted`,
                status: "success"
            })
        } catch ({ message }) {
            toast({
                title: `An error occurred while trying to delete recipe type '${name}': ${message}`,
                status: "error"
            })
        }
    }

    const navigateToCreateRecipeType = () => history.push("/recipetype/new")

    return <Section title="Recipe types" actions={<Button aria-label="Create new recipe type"
        onClick={navigateToCreateRecipeType}>Create</Button>}>
        <IfPending state={state}>
            <Loader />
        </IfPending>
        <IfRejected state={state}>
            <Text>Failed to fetch the recipe types</Text>
        </IfRejected>
        <IfFulfilled state={state}>
            {data => <RecipeTypeList recipeTypes={data} onDelete={handleDelete} />}
        </IfFulfilled>
    </Section >
}

export default RecipeTypeListPage
