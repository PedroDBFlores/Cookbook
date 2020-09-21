import React from "react"
import { useHistory } from "react-router-dom"
import { Formik, Field, Form } from "formik"
import * as yup from "yup"
import Button from "@material-ui/core/Button"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import makeStyles from "@material-ui/core/styles/makeStyles"
import { Theme } from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import Paper from "@material-ui/core/Paper"
import { TextField } from "formik-material-ui"
import { CreateResult } from "../../../model"
import {RecipeType} from "../../../services/recipe-type-service"

interface CreateRecipeTypeFormData {
    name: string
}

const schema = yup.object({
    name: yup.string()
        .required("Name is required")
        .min(1, "Name is required")
        .max(64, "Name exceeds the character limit")
})

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
    }),
)

interface CreateRecipeTypeProps {
    onCreate: (recipe: Omit<RecipeType, "id">) => Promise<CreateResult>
}

const CreateRecipeType: React.FC<CreateRecipeTypeProps> = ({ onCreate }) => {
    const history = useHistory()
    const classes = useStyles()

    const handleOnSubmit = ({ name }: CreateRecipeTypeFormData) => {
        onCreate({ name })
            .then(recipeType => history.push(`/recipetype/${recipeType.id}`))

    }

    return <Grid container spacing={3}>
        <Grid item xs={12}>
            <Typography variant="h4">Create a new recipe type</Typography>
        </Grid>
        <Grid item xs={12}>
            <Paper className={classes.paper}>
                <Formik
                    initialValues={{ name: "" }}
                    validateOnBlur={true}
                    onSubmit={handleOnSubmit}
                    validationSchema={schema}>
                    {
                        () => <Form>
                            <Grid container spacing={3}>
                                <Grid item xs={12}>
                                    <Field
                                        component={TextField}
                                        id="name"
                                        label="Name"
                                        name="name" />
                                </Grid>
                                <Grid item xs={1}>
                                    <Button variant="contained" aria-label="Create recipe type"
                                        type="submit">Create</Button>
                                </Grid>
                            </Grid>
                        </Form>
                    }
                </Formik>
            </Paper>
        </Grid>
    </Grid>
}

export default CreateRecipeType
