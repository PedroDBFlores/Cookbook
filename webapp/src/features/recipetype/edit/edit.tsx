import React, {useRef} from "react"
import {RecipeType} from "../../../model"
import PropTypes from "prop-types"
import * as yup from "yup"
import {Button, FormControl, Grid, Input, InputLabel, Typography} from "@material-ui/core"
import FormHelperText from "@material-ui/core/FormHelperText"
import {RecipeTypeService} from "../../../services/recipe-type-service"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {Formik, FormikValues} from "formik"
import If from "../../../components/flow-control/if"
import {useHistory} from "react-router-dom"
import {useSnackbar} from "notistack"

interface EditRecipeTypeProps {
    id: number
    recipeTypeService: RecipeTypeService
}

const schema = yup.object({
    name: yup.string().required("Name is required")
        .min(1, "Name is required")
})

const EditRecipeType: React.FC<EditRecipeTypeProps> = ({id, recipeTypeService}) => {
    const {enqueueSnackbar} = useSnackbar()
    const history = useHistory()
    const findPromiseRef = useRef(() => recipeTypeService.find(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    const onSubmit = (values: FormikValues) => {
        recipeTypeService.update({...values} as RecipeType)
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
                        ({
                             values,
                             errors,
                             handleChange,
                             handleSubmit
                         }) => (
                            <form onSubmit={event => handleSubmit(event)}>
                                <Grid item xs={12}>
                                    <FormControl>
                                        <InputLabel htmlFor="name">Name</InputLabel>
                                        <Input
                                            id="name"
                                            name="name"
                                            value={values.name}
                                            onChange={handleChange}
                                            aria-describedby="component-error-text-name"
                                        />
                                        <If condition={!!errors?.name}>
                                            <FormHelperText id="component-error-text-name">Name is
                                                required</FormHelperText>
                                        </If>
                                    </FormControl>
                                </Grid>
                                <Grid item xs={1}>
                                    <Button variant="contained" aria-label="Edit recipe type"
                                            type="submit">Edit</Button>
                                </Grid>
                            </form>)}
                </Formik>
            </Grid>
            }
        </IfFulfilled>
    </>
}

EditRecipeType.propTypes = {
    id: PropTypes.number.isRequired,
    recipeTypeService: PropTypes.any.isRequired
}

export default EditRecipeType
