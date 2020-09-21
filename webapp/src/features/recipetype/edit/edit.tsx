import React, {useRef} from "react"
import * as yup from "yup"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {Field, Formik, FormikValues, Form} from "formik"
import {useHistory} from "react-router-dom"
import {useSnackbar} from "notistack"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import Button from "@material-ui/core/Button"
import {TextField} from "formik-material-ui"
import {RecipeType} from "../../../services/recipe-type-service"

interface EditRecipeTypeProps {
    id: number
    onFind: (id: number) => Promise<RecipeType>
    onUpdate: (recipeType: RecipeType) => Promise<void>
}

const schema = yup.object({
    name: yup.string().required("Name is required")
        .min(1, "Name is required")
})

const EditRecipeType: React.FC<EditRecipeTypeProps> = ({id, onFind, onUpdate}) => {
    const {enqueueSnackbar} = useSnackbar()
    const history = useHistory()
    const findPromiseRef = useRef(() => onFind(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    const onSubmit = (values: FormikValues) => {
        onUpdate({...values} as RecipeType)
            .then(() => history.push(`/recipetype/${id}`))
            .catch(() => enqueueSnackbar("An error occurred while updating the recipe type"))
    }

    return <>
        <IfPending state={state}>
            <span>Loading...</span>
        </IfPending>
        <IfRejected state={state}>
            {(error) => <span>Error: {error.message}</span>}
        </IfRejected>
        <IfFulfilled state={state}>
            {(data) => <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Typography variant="h4">Edit a recipe type</Typography>
                </Grid>
                <Formik
                    initialValues={{...data}}
                    validateOnBlur={true}
                    onSubmit={onSubmit}
                    validationSchema={schema}>
                    {
                        () => <Form>
                            <Grid item xs={12}>
                                <Field
                                    component={TextField}
                                    id="name"
                                    label="Name"
                                    name="name"/>
                            </Grid>
                            <Grid item xs={1}>
                                <Button variant="contained" aria-label="Edit recipe type"
                                        type="submit">Edit</Button>
                            </Grid>
                        </Form>
                    }
                </Formik>
            </Grid>
            }
        </IfFulfilled>
    </>
}

export default EditRecipeType
