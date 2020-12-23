import React, {useContext, useRef} from "react"
import PropTypes from "prop-types"
import {Form, Formik, FormikValues} from "formik"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import createRecipeService, {Recipe, RecipeDetails} from "services/recipe-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "services/api-handler"
import {Box, ButtonGroup, Grid, GridItem, Heading, Text, useToast} from "@chakra-ui/react"
import {InputControl, ResetButton, SelectControl, SubmitButton, TextareaControl} from "formik-chakra-ui"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import Loader from "components/loader/loader"
import RecipeFormSchema from "../common/form-schema"

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
        <Box>
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
            <IfFulfilled state={findRecipeState}>
                {data => <Formik
                    initialValues={data}
                    validateOnBlur={true}
                    onSubmit={handleOnSubmit}
                    validationSchema={RecipeFormSchema}>
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
                                        getAllRecipeTypesState.data?.map(({id, name}) => (
                                            <option key={`recipeType-${id}`} value={id}>
                                                {name}
                                            </option>)
                                        )
                                    }
                                </SelectControl>
                            </GridItem>
                            <GridItem colSpan={6}>
                                <TextareaControl name={"ingredients"} label={"Ingredients"}/>
                            </GridItem>
                            <GridItem colSpan={6}>
                                <TextareaControl name={"preparingSteps"} label={"Preparing steps"}/>
                            </GridItem>
                            <GridItem colSpan={12}>
                                <ButtonGroup>
                                    <SubmitButton aria-label="Edit recipe">Edit</SubmitButton>
                                    <ResetButton aria-label="Reset form">Reset</ResetButton>
                                </ButtonGroup>
                            </GridItem>
                        </Grid>
                    </Form>
                </Formik>
                }
            </IfFulfilled>
        </Box>
    </>
}
EditRecipe.propTypes = {
    id: PropTypes.number.isRequired
}

export default EditRecipe
