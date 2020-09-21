import React from "react"
import {Formik, Field, Form, FormikHelpers} from "formik"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import Typography from "@material-ui/core/Typography"
import Grid from "@material-ui/core/Grid"
import Paper from "@material-ui/core/Paper"
import Button from "@material-ui/core/Button"
import {TextField} from "formik-material-ui"
import FormikSelector from "../../../components/formik-selector/formik-selector"
import {RecipeType} from "../../../services/recipe-type-service"

export interface RecipeSearchFormData {
    name: string | undefined
    description: string | undefined
    recipeTypeId: number
}

interface RecipeSearchFormProps {
    recipeTypes: Array<RecipeType>
    onSearch: (data: RecipeSearchFormData) => void
}

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
        formControl: {
            width: "66%"
        },
    }),
)

const RecipeSearchForm: React.FC<RecipeSearchFormProps> = ({recipeTypes, onSearch}) => {
    const classes = useStyles()

    const handleSubmit = (values: RecipeSearchFormData,
                          {setSubmitting}: FormikHelpers<{
                              name: string
                              description: string
                              recipeTypeId: number
                          }>): void => {
        onSearch(values)
        setSubmitting(false)
    }

    return <Paper className={classes.paper}>
        <Typography variant="subtitle1">Parameters</Typography>
        <Formik
            initialValues={{name: "", description: "", recipeTypeId: 0}}
            validateOnBlur={true}
            onSubmit={
                (values, helpers) =>
                    handleSubmit(values, helpers)
            }>
            <Form>
                <Grid container spacing={3}>
                    <Grid item xs>
                        <Field className={classes.formControl}
                               component={TextField}
                               id="name-field"
                               label="Name"
                               name="name"
                               aria-label="Recipe name parameter"/>
                    </Grid>
                    <Grid item xs>
                        <Field className={classes.formControl}
                               component={TextField}
                               id="description-field"
                               label="Description"
                               name="description"
                               aria-label="Recipe description parameter"/>
                    </Grid>
                    <Grid item xs>
                        <FormikSelector
                            className={classes.formControl}
                            options={recipeTypes}
                            label="Recipe type"
                            formName="recipeTypeId"
                            ariaLabel="Recipe type parameter"/>
                    </Grid>
                    <Grid item xs={12}>
                        <Button variant="contained" aria-label="Search recipe with parameters"
                                type="submit">Search</Button>
                    </Grid>
                </Grid>
            </Form>
        </Formik>
    </Paper>
}

export default RecipeSearchForm