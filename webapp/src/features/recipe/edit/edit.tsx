import React, {useContext, useEffect, useRef, useState} from "react"
import PropTypes from "prop-types"
import {Form, Formik} from "formik"
import * as yup from "yup"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import createRecipeService, {RecipeDetails} from "../../../services/recipe-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Choose, When} from "../../../components/flow-control/choose"
import {Box, Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import {InputControl, ResetButton, SelectControl, SubmitButton} from "formik-chakra-ui"
import {useAsync} from "react-async"

interface UpdateRecipeFormData {
    name: string
    description: string
    recipeTypeId: number
    ingredients: string
    preparingSteps: string
}

interface EditRecipeProps {
    id: number
}

const schema = yup.object({
    name: yup.string()
        .required("Name is required")
        .min(1, "Name is required")
        .max(128, "Name exceeds the character limit"),
    description: yup.string()
        .required("Description is required")
        .min(1, "Description is required")
        .max(256, "Description exceeds the character limit"),
    recipeTypeId: yup.number()
        .required("Recipe type is required")
        .min(1, "Recipe type is required"),
    ingredients: yup.string()
        .required("Ingredients is required")
        .min(1, "Ingredients is required")
        .max(2048, "Ingredients exceeds the character limit"),
    preparingSteps: yup.string()
        .required("Preparing steps is required")
        .min(1, "Preparing steps is required")
        .max(4096, "Preparing steps exceeds the character limit"),
})

const EditRecipe: React.FC<EditRecipeProps> = ({id}) => {
    const [recipeTypes, setRecipeTypes] = useState<Array<RecipeType>>()
    const history = useHistory()
    const toast = useToast()

    const {find, update} = createRecipeService(useContext(ApiHandlerContext))
    const {getAll: getAllRecipeTypes} = createRecipeTypeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const {isLoading, isRejected, isFulfilled, data} = useAsync<RecipeDetails>({
        promiseFn: findPromiseRef.current
    })

    useEffect(() => {
        getAllRecipeTypes().then(setRecipeTypes)
    }, [])

    const handleOnSubmit = (formData: UpdateRecipeFormData) => {
        if (data) {
            update({...formData, id: data.id, recipeTypeId: Number(formData.recipeTypeId)})
                .then(() => {
                    toast({title: `Recipe '${formData.name}' updated successfully!`, status: "success"})
                    history.push(`/recipe/${id}`)
                }).catch(err =>
                toast({title: `An error occurred while updating the recipe: ${err.message}`, status: "error"}))
        }
    }

    return <>
        <Heading as="h4">Edit recipe</Heading>
        <Box>
            <Choose>
                <When condition={!recipeTypes && isLoading}>
                    <span>Loading...</span>
                </When>
                <When condition={!!recipeTypes && isFulfilled}>
                    <Formik
                        initialValues={data as RecipeDetails}
                        validateOnBlur={true}
                        onSubmit={handleOnSubmit}
                        validationSchema={schema}>
                        <Form>
                            <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                                <GridItem colSpan={6}>
                                    <InputControl name={"name"} label={"Name"}/>
                                </GridItem>
                                <GridItem colSpan={6}>
                                    <InputControl name={"description"} label={"Description"}/>
                                </GridItem>
                                <GridItem colSpan={6}>
                                    <SelectControl aria-label="Recipe type parameter"
                                                   name={"recipeTypeId"}
                                                   label={"Recipe type"}
                                                   selectProps={{placeholder: " "}}>
                                        {
                                            recipeTypes?.map(({id, name}) => (
                                                <option key={`recipeType-${id}`} value={id}>
                                                    {name}
                                                </option>)
                                            )
                                        }
                                    </SelectControl>
                                </GridItem>
                                <GridItem colSpan={6}>
                                    <InputControl name={"ingredients"} label={"Ingredients"}/>
                                </GridItem>
                                <GridItem colSpan={6}>
                                    <InputControl name={"preparingSteps"} label={"Preparing steps"}/>
                                </GridItem>
                                <GridItem colSpan={12}>
                                    <SubmitButton aria-label="Edit recipe">Edit</SubmitButton>
                                    <ResetButton aria-label="Reset form">Reset</ResetButton>
                                </GridItem>
                            </Grid>
                        </Form>
                    </Formik>
                </When>
            </Choose>
        </Box>
    </>
}
EditRecipe.propTypes = {
    id: PropTypes.number.isRequired
}

export default EditRecipe
