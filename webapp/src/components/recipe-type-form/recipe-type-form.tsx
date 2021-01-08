import React from "react"
import PropTypes from "prop-types"
import {ButtonGroup, Grid, GridItem} from "@chakra-ui/react"
import {Form, Formik, FormikValues} from "formik"
import {InputControl, ResetButton, SubmitButton} from "formik-chakra-ui"
import {RecipeType} from "services/recipe-type-service"
import * as yup from "yup"

const RecipeTypeFormSchema = yup.object({
    name: yup.string()
        .required("Name is required")
        .min(1, "Name is required")
        .max(64, "Name exceeds the character limit")
})

interface RecipeTypeFormProps {
    initialValues?: RecipeType
    onSubmit: (recipeType: RecipeType) => void
}

const RecipeTypeForm: React.FC<RecipeTypeFormProps> = ({initialValues, onSubmit}) => {
    const isCreateMode = initialValues?.id === undefined
    const buttonLabel = isCreateMode ? "Create" : "Edit"

    const handleOnSubmit = (formValues: FormikValues) => onSubmit(formValues as RecipeType)

    return <Formik
        initialValues={initialValues ?? {name: ""}}
        validateOnBlur={true}
        onSubmit={handleOnSubmit}
        validationSchema={RecipeTypeFormSchema}>
        <Form>
            <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                <GridItem colSpan={12}>
                    <InputControl name={"name"} label={"Name"}/>
                </GridItem>
                <GridItem colSpan={12}>
                    <ButtonGroup>
                        <SubmitButton aria-label={`${buttonLabel} recipe type`}>{buttonLabel}</SubmitButton>
                        <ResetButton aria-label="Reset form">Reset</ResetButton>
                    </ButtonGroup>
                </GridItem>
            </Grid>
        </Form>
    </Formik>
}
RecipeTypeForm.propTypes = {
    initialValues: PropTypes.any,
    onSubmit: PropTypes.func.isRequired
}

export default RecipeTypeForm