import React, {useContext, useEffect, useRef, useState} from "react"
import PropTypes from "prop-types"
import {Form, Formik, FormikValues} from "formik"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import createRecipeService, {Recipe, RecipeDetails} from "../../../services/recipe-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Box, ButtonGroup, Grid, GridItem, Heading, useToast} from "@chakra-ui/react"
import {InputControl, ResetButton, SelectControl, SubmitButton, TextareaControl} from "formik-chakra-ui"
import {IfFulfilled, IfPending, useAsync} from "react-async"
import Loader from "../../../components/loader/loader"
import RecipeFormSchema from "../common/form-schema"

interface EditRecipeProps {
    id: number
}

const EditRecipe: React.FC<EditRecipeProps> = ({id}) => {
    const [recipeTypes, setRecipeTypes] = useState<Array<RecipeType>>()
    const history = useHistory()
    const toast = useToast()

    const {find, update} = createRecipeService(useContext(ApiHandlerContext))
    const {getAll: getAllRecipeTypes} = createRecipeTypeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeDetails>({
        promiseFn: findPromiseRef.current
    })

    useEffect(() => {
        getAllRecipeTypes().then(setRecipeTypes)
    }, [])

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
            <IfPending state={state}>
                <Loader/>
            </IfPending>
            <IfFulfilled state={state}>
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
                                        recipeTypes?.map(({id, name}) => (
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
