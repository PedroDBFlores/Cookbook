import React from "react"
import {useHistory} from "react-router-dom"
import {Formik} from "formik"
import * as yup from "yup"
import {createRecipeType} from "../../../services/recipe-type-service"
import {Button, FormControl, InputLabel, Input, Grid, Typography} from "@material-ui/core"
import FormHelperText from "@material-ui/core/FormHelperText"
import If from "../../../components/flow-control/if"

interface CreateRecipeFormData {
    name: string
}

const schema = yup.object({
    name: yup.string().required("Name is required").min(1, "Name is required")
})

const CreateRecipeType: React.FC<unknown> = () => {
    const history = useHistory()

    const onSubmit = (data: CreateRecipeFormData) => {
        createRecipeType({name: data.name})
            .then(recipeType => history.push(`/recipetype/${recipeType.id}`))

    }

    return <Grid container spacing={3}>
        <Grid item xs={12}>
            <Typography variant="h4">Create a new recipe type</Typography>
        </Grid>
        <Formik
            initialValues={{name: ""}}
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
                            <FormControl error={!!errors.name}>
                                <InputLabel htmlFor="name">Name</InputLabel>
                                <Input
                                    id="name"
                                    name="name"
                                    value={values.name}
                                    onChange={handleChange}
                                    aria-describedby="component-error-text-name"
                                />
                                <If condition={!!errors?.name}>
                                    <FormHelperText id="component-error-text-name">Name is required</FormHelperText>
                                </If>
                            </FormControl>
                        </Grid>
                        <Grid item xs={1}>
                            <Button variant="contained" aria-label="Create recipe type"
                                    type="submit">Create</Button>
                        </Grid>
                    </form>
                )
            }
        </Formik>
    </Grid>
}

export default CreateRecipeType
