import React, {useContext, useRef} from "react"
import RecipeTypeList from "./list"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {useHistory} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Button, Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import Loader from "../../../components/loader/loader"

const RecipeTypeListPage: React.FC = () => {
    const {getAll, delete: deleteRecipeType} = createRecipeTypeService(useContext(ApiHandlerContext))
    const history = useHistory()
    const toast = useToast()

    const getPromiseRef = useRef(() => getAll())
    const state = useAsync<Array<RecipeType>>({
        promiseFn: getPromiseRef.current
    })
    const handleDelete = async (id: number, name: string) => {
        try {
            await deleteRecipeType(id)
            state.reload()
            toast({
                title: `Recipe type '${name}' was deleted`,
                status: "success"
            })
        } catch ({message}) {
            toast({
                title: `An error occurred while trying to delete recipe type '${name}': ${message}`,
                status: "error"
            })
        }
    }

    const navigateToCreateRecipeType = () => history.push("/recipetype/new")

    return <Grid templateColumns="repeat(12, 1fr)" gap={6}>
        <GridItem colSpan={11}>
            <Heading>Recipe types</Heading>
        </GridItem>
        <GridItem colSpan={1}>
            <Button aria-label="Create new recipe type"
                    onClick={navigateToCreateRecipeType}>Create</Button>
        </GridItem>
        <GridItem colSpan={12}>
            <IfPending state={state}>
                <Loader/>
            </IfPending>
            <IfRejected state={state}>
                {err => <span>Error: {err?.message}</span>}
            </IfRejected>
            <IfFulfilled state={state}>
                {data => <RecipeTypeList recipeTypes={data} onDelete={handleDelete}/>}
            </IfFulfilled>
        </GridItem>
    </Grid>
}

export default RecipeTypeListPage
