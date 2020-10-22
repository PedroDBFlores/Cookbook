import React, {useContext, useRef} from "react"
import PropTypes from "prop-types"
import * as yup from "yup"
import {useAsync} from "react-async"
import {Field, Formik, FormikValues, Form} from "formik"
import {useHistory} from "react-router-dom"
import {useSnackbar} from "notistack"
import {Grid, Typography, Button, Paper} from "@material-ui/core"
import {TextField} from "formik-material-ui"
import createRecipeTypeService, {RecipeType} from "../../../services/recipe-type-service"
import {makeStyles, createStyles, Theme} from "@material-ui/core/styles"
import {ApiHandlerContext} from "../../../services/api-handler"
import {Choose, When} from "../../../components/flow-control/choose"

const schema = yup.object({
    name: yup.string().required("Name is required")
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

const EditRecipeType: React.FC<{ id: number }> = ({id}) => {
    const {enqueueSnackbar} = useSnackbar()
    const classes = useStyles()
    const history = useHistory()

    const {find, update} = createRecipeTypeService(useContext(ApiHandlerContext))
    const findPromiseRef = useRef(() => find(id))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current
    })

    const handleOnSubmit = async (values: FormikValues) => {
        try {
            await update({...values} as RecipeType)
            history.push(`/recipetype/${id}`)
        } catch ({message}) {
            enqueueSnackbar(`An error occurred while updating the recipe type: ${message}`, {variant: "error"})
        }
    }

    return <Grid container spacing={3}>
        <Choose>
            <When condition={state.isPending}>
                <span>Loading...</span>
            </When>
            <When condition={state.isRejected}>
                <span>Error: {state.error?.message}</span>
            </When>
            <When condition={state.isFulfilled}>
                <>
                    <Grid item xs={12}>
                        <Typography variant="h4">Edit a recipe type</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Paper className={classes.paper}>
                            <Formik
                                initialValues={{...state.data}}
                                validateOnBlur={true}
                                onSubmit={handleOnSubmit}
                                validationSchema={schema}>
                                <Form>
                                    <Grid container spacing={3}/>
                                    <Grid item xs={12}>
                                        <Field
                                            component={TextField}
                                            id="name"
                                            label="Name"
                                            name="name"/>
                                    </Grid>
                                    <Grid item className={classes.buttonArea}>
                                        <Button color="primary" variant="contained" aria-label="Edit recipe type"
                                                type="submit">Edit</Button>
                                        <Button variant="contained" aria-label="Reset form"
                                                type="reset">Reset</Button>
                                    </Grid>
                                </Form>
                            </Formik>
                        </Paper>
                    </Grid>
                </>
            </When>
        </Choose>
    </Grid>
}
EditRecipeType.propTypes = {
    id: PropTypes.number.isRequired
}

export default EditRecipeType
