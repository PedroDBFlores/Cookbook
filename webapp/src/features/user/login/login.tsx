import React from "react"
import PropTypes from "prop-types"
import Button from "@material-ui/core/Button"
import FormControl from "@material-ui/core/FormControl"
import Grid from "@material-ui/core/Grid"
import Input from "@material-ui/core/Input"
import InputLabel from "@material-ui/core/InputLabel"
import Typography from "@material-ui/core/Typography"
import * as yup from "yup"
import {Formik} from "formik"
import If from "../../../components/flow-control/if"
import FormHelperText from "@material-ui/core/FormHelperText"
import {CredentialsService} from "../../../services/credentials-service"
import {useHistory} from "react-router-dom"

interface LoginFormData {
    userName: string
    password: string
}

const schema = yup.object({
    userName: yup.string().required("Username is required").min(1, "Username is required"),
    password: yup.string().required("Password is required").min(1, "Password is required")
})

const Login: React.FC<{ credentialsService: CredentialsService }> = ({credentialsService}) => {
    const history = useHistory()

    const onSubmit = (data: LoginFormData) => {
        credentialsService.login({...data})
            .then(() => history.goBack())
    }

    return <Grid container spacing={3}>
        <Grid item xs={12}>
            <Typography variant="h4">Login user</Typography>
        </Grid>
        <Formik
            initialValues={{userName: "", password: ""}}
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
                            <FormControl error={!!errors.userName}>
                                <InputLabel htmlFor="userName">Username</InputLabel>
                                <Input
                                    id="userName"
                                    name="userName"
                                    value={values.userName}
                                    onChange={handleChange}
                                    aria-describedby="component-error-text-name"
                                />
                                <If condition={!!errors?.userName}>
                                    <FormHelperText id="component-error-text-name">Username is required</FormHelperText>
                                </If>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12}>
                            <FormControl error={!!errors.password}>
                                <InputLabel htmlFor="password">Password</InputLabel>
                                <Input
                                    id="password"
                                    name="password"
                                    type="password"
                                    value={values.password}
                                    onChange={handleChange}
                                    aria-describedby="component-error-text-name"
                                />
                                <If condition={!!errors?.password}>
                                    <FormHelperText id="component-error-text-name">Password is required</FormHelperText>
                                </If>
                            </FormControl>
                        </Grid>
                        <Grid item xs={1}>
                            <Button variant="contained" aria-label="Login to application"
                                    type="submit">Login</Button>
                        </Grid>
                    </form>
                )
            }
        </Formik>
    </Grid>
}

Login.propTypes = {
    credentialsService: PropTypes.any.isRequired
}

export default Login