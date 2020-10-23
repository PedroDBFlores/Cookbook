import React, {useContext, useEffect, useRef, useState} from "react"
import PropTypes from "prop-types"
import createRecipeService, {RecipeDetails} from "../../../services/recipe-service"
import {ApiHandlerContext} from "../../../services/api-handler"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {Grid, Typography, Paper, Button} from "@material-ui/core"
import {Choose, When} from "../../../components/flow-control/choose"
import {Field, Form, Formik} from "formik"
import {TextField} from "formik-material-ui"
import FormikSelector from "../../../components/formik-selector/formik-selector"
import {makeStyles, createStyles, Theme} from "@material-ui/core/styles"
import {AuthContext} from "../../../services/credentials-service"
import {useHistory} from "react-router-dom"
import {useSnackbar} from "notistack"
import * as yup from "yup"
import {useAsync} from "react-async"

interface UpdateRecipeFormData {
    name: string
    description: string
    recipeTypeId: number
    ingredients: string
    preparingSteps: string
}

interface EditRecipeProps {
    id: number
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
            width: "100%"
        }
    }),
)

const EditRecipe: React.FC<EditRecipeProps> = ({id}) => {
    const [recipeTypes, setRecipeTypes] = useState<Array<RecipeType>>()
    const classes = useStyles()
    const authContext = useContext(AuthContext)
    const history = useHistory()
    const {enqueueSnackbar} = useSnackbar()

    const {find, update} = createRecipeService(useContext(ApiHandlerContext))
    const {getAll: getAllRecipeTypes} = createRecipeTypeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeDetails>({
        promiseFn: findPromiseRef.current
    })

    useEffect(() => {
        getAllRecipeTypes().then(setRecipeTypes)
    }, [])

    const handleOnSubmit = (data: UpdateRecipeFormData) => {
        if (authContext && state.data) {
            const userId = authContext.userId
            update({...data, id: state.data.id, userId: userId})
                .then(() => {
                    enqueueSnackbar(`Recipe '${data.name}' updated successfully!`)
                    history.push(`/recipe/${id}`)
                }).catch(err =>
                enqueueSnackbar(`An error occurred while updating the recipe: ${err.message}`))
        }
    }

    return <Grid container spacing={3}>
        <Grid item xs={12}>
            <Typography variant="h4">Edit recipe</Typography>
        </Grid>
        <Grid item xs={12}>
            <Paper className={classes.paper}>
                <Choose>
                    <When condition={!recipeTypes && state.isPending}>
                        <span>Loading...</span>
                    </When>
                    <When condition={!!recipeTypes && state.isFulfilled}>
                        <Formik
                            initialValues={{...state.data}}
                            validateOnBlur={true}
                            onSubmit={handleOnSubmit}
                            validationSchema={schema}>
                            {
                                ({errors}) => <Form>
                                    <Grid container spacing={3}>
                                        <Grid item xs={6}>
                                            <Field
                                                className={classes.formControl}
                                                component={TextField}
                                                id="name"
                                                label="Name"
                                                name="name"/>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Field
                                                className={classes.formControl}
                                                component={TextField}
                                                id="description"
                                                label="Description"
                                                name="description"/>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <FormikSelector
                                                className={classes.formControl}
                                                options={recipeTypes}
                                                label="Recipe type"
                                                formName="recipeTypeId"
                                                ariaLabel="Recipe type parameter"
                                                error={errors.recipeTypeId}/>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Field
                                                className={classes.formControl}
                                                component={TextField}
                                                id="ingredients"
                                                label="Ingredients"
                                                name="ingredients"/>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Field
                                                className={classes.formControl}
                                                component={TextField}
                                                id="preparingSteps"
                                                label="Preparing steps"
                                                name="preparingSteps"/>
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Button variant="contained" aria-label="Edit recipe"
                                                    type="submit">Edit</Button>
                                        </Grid>
                                    </Grid>
                                </Form>
                            }
                        </Formik>
                    </When>
                </Choose>
            </Paper>
        </Grid>
    </Grid>
}
EditRecipe.propTypes = {
    id: PropTypes.number.isRequired
}

export default EditRecipe
