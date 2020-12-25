import React, {useContext, useRef} from "react"
import PropTypes from "prop-types"
import {FormikValues} from "formik"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import createRecipeService, {Recipe, RecipeDetails} from "services/recipe-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "services/api-handler"
import {Heading, Text, useToast} from "@chakra-ui/react"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import Loader from "components/loader/loader"
import RecipeForm from "components/recipe-form/recipe-form"

interface EditRecipeProps {
    id: number
}

const EditRecipe: React.FC<EditRecipeProps> = ({id}) => {
    const history = useHistory()
    const toast = useToast()

    const {getAll: getAllRecipeTypes} = createRecipeTypeService(useContext(ApiHandlerContext))
    const {find, update} = createRecipeService(useContext(ApiHandlerContext))
    const getAllRecipeTypesRef = useRef(getAllRecipeTypes)
    const getAllRecipeTypesState = useAsync<Array<RecipeType>>({
        promiseFn: getAllRecipeTypesRef.current,
        onResolve: () => findRecipeState.run(),
        onReject: ({message}) => toast({
            title: "An error occurred while fetching the recipe types",
            description: message,
            status: "error",
            duration: null
        })
    })
    const findPromiseRef = useRef(() => find(id))
    const findRecipeState = useAsync<RecipeDetails>({
        deferFn: findPromiseRef.current,
        onReject: ({message}) => toast({
            title: "An error occurred while fetching the recipe",
            description: message,
            status: "error",
            duration: null,
        })
    })

    const handleOnSubmit = async (formData: FormikValues) => {
        try {
            await update({...formData, recipeTypeId: Number(formData.recipeTypeId)} as Recipe)
            toast({title: `Recipe '${formData.name}' updated successfully!`, status: "success"})
            history.push(`/recipe/${id}`)
        } catch ({message}) {
            toast({title: `An error occurred while updating the recipe: ${message}`, status: "error"})
        }
    }

    return <>
        <Heading>Edit recipe</Heading>
        <IfPending state={getAllRecipeTypesState}>
            <Loader/>
        </IfPending>
        <IfPending state={findRecipeState}>
            <Loader/>
        </IfPending>
        <IfRejected state={getAllRecipeTypesState}>
            <Text>Failed to fetch the recipe types</Text>
        </IfRejected>
        <IfRejected state={findRecipeState}>
            <Text>Failed to fetch the recipe</Text>
        </IfRejected>
        <IfFulfilled state={getAllRecipeTypesState}>
            {
                recipeTypes => <IfFulfilled state={findRecipeState}>
                    {data => <RecipeForm recipeTypes={recipeTypes} initialValues={data} onSubmit={handleOnSubmit}/>}
                </IfFulfilled>
            }
        </IfFulfilled>
    </>
}
EditRecipe.propTypes = {
    id: PropTypes.number.isRequired
}

export default EditRecipe
