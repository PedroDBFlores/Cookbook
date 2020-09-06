import React from "react"
import PropTypes from "prop-types"
import {useHistory} from "react-router-dom"
import {Formik} from "formik"
import * as yup from "yup"
import {RecipeTypeService} from "../../../services/recipe-type-service"
import Button from "@material-ui/core/Button"
import FormControl from "@material-ui/core/FormControl"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import TextField from "@material-ui/core/TextField"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import Paper from "@material-ui/core/Paper"
import {CreateResult, RecipeType} from "../../../model"

interface CreateRecipeFormData {
    name: string
}

const schema = yup.object({
    name: yup.string().required("Name is required").min(1, "Name is required")
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

const CreateRecipeType: React.FC<CreateRecipeTypeProps> = ({onCreate}) => {
    const history = useHistory()
    const classes = useStyles()

    const onSubmit = (data: CreateRecipeFormData) => {
        onCreate({name: data.name})
            .then(recipeType => history.push(`/recipetype/${recipeType.id}`))

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
                                <Grid container spacing={3}>
                                    <Grid item xs={12}>
                                        <TextField
                                            id="name"
                                            label="Name"
                                            name="name"
                                            value={values.name}
                                            onChange={handleChange}
                                            variant="outlined"
                                            error={!!errors?.name}
                                            helperText={!!errors?.name && "Name is required"}
                                        />
                                    </Grid>
                                    <Grid item xs={1}>
                                        <Button variant="contained" aria-label="Create recipe type"
                                                type="submit">Create</Button>
                                    </Grid>
                                </Grid>
                            </form>
                        )
                    }
                </Formik>
            </Paper>
        </Grid>
    </Grid>
}

CreateRecipeType.propTypes = {
    onCreate: PropTypes.func.isRequired
}
export default CreateRecipeType
