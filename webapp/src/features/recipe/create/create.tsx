import React, {useContext, useEffect, useState} from "react"
import Button from "@material-ui/core/Button"
import Grid from "@material-ui/core/Grid"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import makeStyles from "@material-ui/core/styles/makeStyles"
import Typography from "@material-ui/core/Typography"
import {Field, Form, Formik} from "formik"
import {TextField} from "formik-material-ui"
import FormikSelector from "../../../components/formik-selector/formik-selector"
import * as yup from "yup"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import createRecipeService from "../../../services/recipe-service"
import {AuthContext} from "../../../services/credentials-service"
import {useHistory} from "react-router-dom"
import {ApiHandlerContext} from "../../../services/api-handler"
import {useSnackbar} from "notistack"

interface CreateRecipeFormData {
    name: string
    description: string
    recipeTypeId: number
    ingredients: string
    preparingSteps: string
}

const schema = yup.object({
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

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
        formControl: {
            width: "66%"
        }
    }),
)

const CreateRecipe: React.FC = () => {
    const [recipeTypes, setRecipeTypes] = useState<Array<RecipeType>>([])
    const classes = useStyles()
    const authContext = useContext(AuthContext)
    const history = useHistory()
    const {enqueueSnackbar} = useSnackbar()

    const {create} = createRecipeService(useContext(ApiHandlerContext))
    const {getAll: getAllRecipeTypes} = createRecipeTypeService(useContext(ApiHandlerContext))

    useEffect(() => {
        getAllRecipeTypes().then(setRecipeTypes)
    }, [])

    const handleOnSubmit = (data: CreateRecipeFormData) => {
        if (authContext) {
            const userId = authContext.userId
            create({
                name: data.name,
                description: data.description,
                recipeTypeId: data.recipeTypeId,
                ingredients: data.ingredients,
                preparingSteps: data.preparingSteps,
                userId
            }).then(({id}) => {
                enqueueSnackbar(`Recipe '${data.name}' created successfully!`)
                history.push(`/recipe/${id}`)
            })
        }
    }

    return <Grid container spacing={3}>
        <Grid item xs={12}>
            <Typography variant="h4">Create a new recipe</Typography>
        </Grid>
        <Grid item xs={12}>
            <Formik
                initialValues={{
                    name: "",
                    description: "",
                    recipeTypeId: 0,
                    ingredients: "",
                    preparingSteps: ""
                }}
                validateOnBlur={true}
                onSubmit={handleOnSubmit}
                validationSchema={schema}>
                {
                    ({errors}) => <Form>
                        <Grid container spacing={3}>
                            <Grid item xs>
                                <Field
                                    className={classes.formControl}
                                    component={TextField}
                                    id="name"
                                    label="Name"
                                    name="name"/>
                            </Grid>
                            <Grid item xs>
                                <Field
                                    className={classes.formControl}
                                    component={TextField}
                                    id="description"
                                    label="Description"
                                    name="description"/>
                            </Grid>
                            <Grid item xs>
                                <FormikSelector
                                    className={classes.formControl}
                                    options={recipeTypes}
                                    label="Recipe type"
                                    formName="recipeTypeId"
                                    ariaLabel="Recipe type parameter"
                                    error={errors.recipeTypeId}/>
                            </Grid>
                            <Grid item xs>
                                <Field
                                    className={classes.formControl}
                                    component={TextField}
                                    id="ingredients"
                                    label="Ingredients"
                                    name="ingredients"/>
                            </Grid>
                            <Grid item xs>
                                <Field
                                    className={classes.formControl}
                                    component={TextField}
                                    id="preparingSteps"
                                    label="Preparing steps"
                                    name="preparingSteps"/>
                            </Grid>
                            <Grid item xs={12}>
                                <Button variant="contained" aria-label="Create recipe"
                                        type="submit">Create</Button>
                            </Grid>
                        </Grid>
                    </Form>
                }
            </Formik>
        </Grid>
    </Grid>
}

export default CreateRecipe