import React from "react"
import PropTypes from "prop-types"
import {ButtonGroup, Grid, GridItem} from "@chakra-ui/react"
import RecipeTypeFormSchema from "features/recipetype/common/form-schema"
import {Form, Formik, FormikValues} from "formik"
import {InputControl, ResetButton, SubmitButton} from "formik-chakra-ui"
import {RecipeType} from "services/recipe-type-service"

interface RecipeTypeFormProps {
    initialValues?: RecipeType
    onSubmit: (recipeType: RecipeType) => void
}

const RecipeTypeForm: React.FC<RecipeTypeFormProps> = ({initialValues, onSubmit}) => {
    const isCreateMode = initialValues?.id === undefined
    const buttonLabel = isCreateMode ? "Create" : "Edit"

    const handleOnSubmit = (formValues: FormikValues) => onSubmit({
        id: formValues?.id ?? 0,
        name: formValues.name
    })

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