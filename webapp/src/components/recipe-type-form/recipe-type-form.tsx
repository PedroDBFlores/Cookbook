import React from "react"
import {ButtonGroup, Grid, GridItem} from "@chakra-ui/react"
import {Form, Formik, FormikValues} from "formik"
import {InputControl, ResetButton, SubmitButton} from "formik-chakra-ui"
import {RecipeType} from "services/recipe-type-service"
import * as yup from "yup"
import {useTranslation} from "react-i18next"

interface RecipeTypeFormProps {
    initialValues?: RecipeType
    onSubmit: (recipeType: RecipeType) => void
}

const RecipeTypeForm: React.VFC<RecipeTypeFormProps> = ({initialValues, onSubmit}) => {
    const {t} = useTranslation()

    const RecipeTypeFormSchema = yup.object({
        name: yup.string()
            .required(t("validations.is-required", {field: t("name")}))
            .min(1, t("validations.is-required", {field: t("name")}))
            .max(64, t("validations.exceeds-the-character-limit", {field: t("name")}))
    })

    const isCreateMode = initialValues?.id === undefined
    const buttonLabel = isCreateMode ? t("common.create") : t("common.edit")

    const handleOnSubmit = (formValues: FormikValues) => onSubmit(formValues as RecipeType)

    return <Formik
        initialValues={initialValues ?? {name: ""}}
        validateOnBlur={true}
        onSubmit={handleOnSubmit}
        validationSchema={RecipeTypeFormSchema}>
        <Form>
            <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                <GridItem colSpan={12}>
                    <InputControl name={"name"} label={t("name")}/>
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

export default RecipeTypeForm
