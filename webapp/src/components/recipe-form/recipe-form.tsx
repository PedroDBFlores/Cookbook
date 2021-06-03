import React from "react"
import PropTypes from "prop-types"
import {Recipe} from "services/recipe-service"
import {RecipeType} from "services/recipe-type-service"
import {Form, Formik, FormikValues} from "formik"
import {ButtonGroup, Grid, GridItem} from "@chakra-ui/react"
import {InputControl, ResetButton, SelectControl, SubmitButton, TextareaControl} from "formik-chakra-ui"
import * as yup from "yup"
import {useTranslation} from "react-i18next"

interface RecipeFormProps {
    recipeTypes: Array<RecipeType>
    initialValues?: Recipe
    onSubmit: (recipe: Recipe) => void
}

const RecipeForm: React.VFC<RecipeFormProps> = ({initialValues, recipeTypes, onSubmit}) => {
    const {t} = useTranslation()

    const RecipeFormSchema = yup.object({
        name: yup.string()
            .required(t("validations.is-required", {field: t("name")}))
            .min(1, t("validations.is-required", {field: t("name")}))
            .max(128, t("validations.exceeds-the-character-limit", {field: t("name")})),
        description: yup.string()
            .required(t("validations.is-required", {field: t("description")}))
            .min(1, t("validations.is-required", {field: t("description")}))
            .max(256, t("validations.exceeds-the-character-limit", {field: t("description")})),
        recipeTypeId: yup.number()
            .required(t("validations.is-required", {field: t("recipe-type-feature.singular")}))
            .min(1, t("validations.is-required", {field: t("recipe-type-feature.singular")})),
        ingredients: yup.string()
            .required(t("validations.is-required", {field: t("ingredients")}))
            .min(1, t("validations.is-required", {field: t("ingredients")}))
            .max(2048, t("validations.exceeds-the-character-limit", {field: t("ingredients")})),
        preparingSteps: yup.string()
            .required(t("validations.is-required", {field: t("preparing-steps")}))
            .min(1, t("validations.is-required", {field: t("preparing-steps")}))
            .max(4096, t("validations.exceeds-the-character-limit", {field: t("preparing-steps")})),
    })

    const isCreateMode = initialValues?.id === undefined
    const buttonLabel = isCreateMode ? t("common.create") : t("common.edit")

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
                    <InputControl name="name" label={t("name")}/>
                </GridItem>
                <GridItem colSpan={6}>
                    <InputControl name="description" label={t("description")}/>
                </GridItem>
                <GridItem colSpan={6}>
                    <SelectControl aria-label={t("recipe-feature.recipe-type-parameter")}
                                   name="recipeTypeId"
                                   label={t("recipe-type-feature.singular")}
                                   selectProps={{placeholder: " "}}>
                        {
                            recipeTypes?.map(({id, name}) => (
                                <option key={`recipeType-${id}`} value={id}>
                                    {name}
                                </option>))
                        }
                    </SelectControl>
                </GridItem>
                <GridItem colSpan={6}>
                    <TextareaControl name="ingredients" label={t("ingredients")}/>
                </GridItem>
                <GridItem colSpan={6}>
                    <TextareaControl name="preparingSteps" label={t("preparing-steps")}/>
                </GridItem>
                <GridItem colSpan={12}>
                    <ButtonGroup>
                        <SubmitButton aria-label={buttonLabel}>{buttonLabel}</SubmitButton>
                        <ResetButton aria-label={t("common.reset-form-aria-label")}>{t("common.reset")}</ResetButton>
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
