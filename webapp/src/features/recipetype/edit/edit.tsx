import React, {useRef} from "react"
import {RecipeType} from "../../../dto"
import PropTypes from "prop-types"
import * as yup from "yup"
import {Button, FormControl, Grid, Input, InputLabel, Typography} from "@material-ui/core"
import FormHelperText from "@material-ui/core/FormHelperText"
import {findRecipeType} from "../../../services/recipe-type-service"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {Formik, FormikValues} from "formik"
import If from "../../../components/flow-control/if"


interface EditRecipeTypeProps {
    id: number
}

const schema = yup.object({
    name: yup.string().required("Name is required").min(1, "Name is required")
})

const EditRecipeType: React.FC<EditRecipeTypeProps> = ({id}) => {
    const findPromiseRef = useRef(() => findRecipeType(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    const onSubmit = (values: FormikValues) => {
        console.log(values)
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
                            <form>
                                <Grid item xs={12}>
                                    <FormControl>
                                        <InputLabel htmlFor="name">Name</InputLabel>
                                        <Input
                                            id="name"
                                            name="name"
                                            value={values.name}
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
    id: PropTypes.number.isRequired
}

export default EditRecipeType