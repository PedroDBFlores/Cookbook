import React from "react"
import PropTypes from "prop-types"
import {Form, Formik, FormikHelpers} from "formik"
import {RecipeType} from "services/recipe-type-service"
import {Button, Grid, GridItem} from "@chakra-ui/react"
import {InputControl, SelectControl} from "formik-chakra-ui"
import {useTranslation} from "react-i18next";

export interface RecipeSearchFormData {
    name: string | undefined
    description: string | undefined
    recipeTypeId: number
}

interface RecipeSearchFormProps {
    recipeTypes: Array<RecipeType>
    onSearch: (data: RecipeSearchFormData) => void
}

const RecipeSearchForm: React.VFC<RecipeSearchFormProps> = ({recipeTypes, onSearch}) => {
    const {t} = useTranslation()

    const handleSubmit = (values: RecipeSearchFormData,
                          {setSubmitting}: FormikHelpers<{
                              name: string
                              description: string
                              recipeTypeId: number
                          }>): void => {
        onSearch(values)
        setSubmitting(false)
    }

    return <Formik
        initialValues={{name: "", description: "", recipeTypeId: 0}}
        validateOnBlur={true}
        onSubmit={
            (values, helpers) =>
                handleSubmit(values, helpers)
        }>
        <Form>
            <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                <GridItem colSpan={12}>
                    <InputControl aria-label={t("recipe-feature.search.recipe-name-parameter")}
                                  name="name"
                                  label={t("name")}/>
                </GridItem>
                <GridItem colSpan={12}>
                    <InputControl aria-label={t("recipe-feature.search.recipe-description-parameter")}
                                  name="description"
                                  label={t("description")}/>
                </GridItem>
                <GridItem colSpan={12}>
                    <SelectControl aria-label={t("recipe-feature.recipe-type-parameter")}
                                   name="recipeTypeId"
                                   label={t("recipe-type-feature.singular")}
                                   selectProps={{placeholder: " "}}>
                        {
                            recipeTypes.map(({id, name}) => (
                                <option key={`recipeType-${id}`} value={id}>
                                    {name}
                                </option>))
                        }
                    </SelectControl>
                </GridItem>
                <Grid colSpan={12}>
                    <Button aria-label={t("common.search")}
                            type="submit">{t("common.search")}</Button>
                </Grid>
            </Grid>
        </Form>
    </Formik>
}

RecipeSearchForm.propTypes = {
    recipeTypes: PropTypes.array.isRequired,
    onSearch: PropTypes.func.isRequired
}

export default RecipeSearchForm
