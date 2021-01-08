import React from "react"
import PropTypes from "prop-types"
import {Recipe} from "services/recipe-service"
import {RecipeType} from "services/recipe-type-service"
import {Form, Formik, FormikValues} from "formik"
import {ButtonGroup, Grid, GridItem} from "@chakra-ui/react"
import {InputControl, ResetButton, SelectControl, SubmitButton, TextareaControl} from "formik-chakra-ui"
import * as yup from "yup"

const RecipeFormSchema = yup.object({
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

interface RecipeFormProps {
    recipeTypes: Array<RecipeType>
    initialValues?: Recipe
    onSubmit: (recipe: Recipe) => void
}

const RecipeForm: React.FC<RecipeFormProps> = ({initialValues, recipeTypes, onSubmit}) => {
    const isCreateMode = initialValues?.id === undefined
    const buttonLabel = isCreateMode ? "Create" : "Edit"

    const handleOnSubmit = (formValues: FormikValues) => {
        onSubmit({
            ...formValues,
            recipeTypeId: Number(formValues.recipeTypeId)
        } as Recipe)
    }

    return <Formik
        initialValues={initialValues ?? {
            name: "",
            description: "",
            recipeTypeId: undefined,
            ingredients: "",
            preparingSteps: ""
        }}
        validateOnBlur={true}
        onSubmit={handleOnSubmit}
        validationSchema={RecipeFormSchema}>
        <Form>
            <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                <GridItem colSpan={6}>
                    <InputControl name="name" label="Name"/>
                </GridItem>
                <GridItem colSpan={6}>
                    <InputControl name="description" label="Description"/>
                </GridItem>
                <GridItem colSpan={6}>
                    <SelectControl aria-label="Recipe type parameter"
                                   name="recipeTypeId"
                                   label="Recipe type"
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
                    <TextareaControl name="ingredients" label="Ingredients"/>
                </GridItem>
                <GridItem colSpan={6}>
                    <TextareaControl name="preparingSteps" label="Preparing steps"/>
                </GridItem>
                <GridItem colSpan={12}>
                    <ButtonGroup>
                        <SubmitButton aria-label={`${buttonLabel} recipe`}>{buttonLabel}</SubmitButton>
                        <ResetButton aria-label="Reset form">Reset</ResetButton>
                    </ButtonGroup>
                </GridItem>
            </Grid>
        </Form>
    </Formik>
}

RecipeForm.propTypes = {
    recipeTypes: PropTypes.array.isRequired,
    initialValues: PropTypes.any,
    onSubmit: PropTypes.func.isRequired
}

export default RecipeForm