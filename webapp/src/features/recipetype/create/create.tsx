import React, {useContext} from "react"
import {useHistory} from "react-router-dom"
import {Formik, Field, Form} from "formik"
import * as yup from "yup"
import Button from "@material-ui/core/Button"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import Paper from "@material-ui/core/Paper"
import {TextField} from "formik-material-ui"
import createRecipeTypeService from "../../../services/recipe-type-service"
import {useSnackbar} from "notistack"
import {ApiHandlerContext} from "../../../services/api-handler"

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
        buttonArea: {
            "& > *": {
                margin: theme.spacing(1),
            },
        }
    }),
)

const CreateRecipeType: React.FC = () => {
    const {create} = createRecipeTypeService(useContext(ApiHandlerContext))
    const {enqueueSnackbar} = useSnackbar()
    const history = useHistory()
    const classes = useStyles()

    const handleOnSubmit = async ({name}: CreateRecipeTypeFormData) => {
        try {
            const recipeType = await create({name})
            enqueueSnackbar(`Recipe type '${name}' created successfully!`)
            history.push(`/recipetype/${recipeType.id}`)
        } catch ({message}) {
            enqueueSnackbar(`An error occurred while creating the recipe type: ${message}`, {variant: "error"})
        }
    }

    return <Grid container spacing={3}>
        <Grid item xs={12}>
            <Typography variant="h4">Create a new recipe type</Typography>
        </Grid>
        <Grid item xs={12}>
            <Paper className={classes.paper}>
                <Formik
                    initialValues={{name: ""}}
                    validateOnBlur={true}
                    onSubmit={handleOnSubmit}
                    validationSchema={schema}>
                    <Form>
                        <Grid container spacing={3}>
                            <Grid item xs={12}>
                                <Field
                                    component={TextField}
                                    id="name"
                                    label="Name"
                                    name="name"/>
                            </Grid>
                            <Grid item className={classes.buttonArea}>
                                <Button color="primary" variant="contained" aria-label="Create recipe type"
                                        type="submit">Create</Button>
                                <Button variant="contained" aria-label="Reset form"
                                        type="reset">Reset</Button>
                            </Grid>
                        </Grid>
                    </Form>
                </Formik>
            </Paper>
        </Grid>
    </Grid>
}

export default CreateRecipeType
