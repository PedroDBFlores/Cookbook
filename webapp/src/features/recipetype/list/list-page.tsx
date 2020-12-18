import React, {useContext, useRef} from "react"
import RecipeTypeList from "./list"
import {useAsync} from "react-async"
import {useHistory} from "react-router-dom"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Choose, When} from "../../../components/flow-control/choose"
import {Button, Grid, GridItem, Text} from "@chakra-ui/react"
import {useToast} from "@chakra-ui/react"

const RecipeTypeListPage: React.FC = () => {
    const {getAll, delete: deleteRecipeType} = createRecipeTypeService(useContext(ApiHandlerContext))
    const history = useHistory()
    const toast = useToast()

    const getPromiseRef = useRef(() => getAll())
    const handleDelete = (id: number) => deleteRecipeType(id).then(() =>
        toast({
            title: `Recipe type ${id} was deleted`,
            status: "success"
        }))
    const state = useAsync<Array<RecipeType>>({
        promiseFn: getPromiseRef.current
    })

    const navigateToCreateRecipeType = () => history.push("/recipetype/new")

    return <Grid templateColumns="repeat(12, 1fr)" gap={6}>
        <GridItem colSpan={11}>
            <Text as="h4">Recipe types</Text>
        </GridItem>
        <GridItem colSpan={1}>
            <Button aria-label="Create new recipe type"
                    onClick={navigateToCreateRecipeType}>Create</Button>
        </GridItem>
        <GridItem colSpan={12}>
            <Choose>
                <When condition={state.isPending}>
                    <span>Loading...</span>
                </When>
                <When condition={state.isRejected}>
                    <span>Error: {state.error?.message}</span>
                </When>
                <When condition={state.isFulfilled}>
                    <RecipeTypeList recipeTypes={state.data || []} onDelete={(id => handleDelete(id))}/>
                </When>
            </Choose>
        </GridItem>
    </Grid>
}

export default RecipeTypeListPage
