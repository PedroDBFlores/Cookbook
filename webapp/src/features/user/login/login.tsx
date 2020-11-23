import React, {useContext} from "react"
import PropTypes from "prop-types"
import * as yup from "yup"
import {Paper, Button, Grid, Typography} from "@material-ui/core"
import {Field, Formik, Form} from "formik"
import If from "../../../components/flow-control/if"
import {useHistory} from "react-router-dom"
import {makeStyles, createStyles, Theme} from "@material-ui/core/styles"
import {TextField} from "formik-material-ui"
import {Credentials, AuthInfo, AuthContext} from "../../../services/credentials-service/credentials-service"

interface LoginFormData {
    userName: string
    password: string
}

const schema = yup.object({
    userName: yup.string().required("Username is required").min(1, "Username is required"),
    password: yup.string().required("Password is required").min(1, "Password is required")
})

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        paper: {
            padding: theme.spacing(2),
            color: theme.palette.text.primary,
        },
    }),
)

interface LoginProps {
    loginFn: (credentials: Credentials) => Promise<AuthInfo>
    onUpdateAuth: (authInfo: AuthInfo) => void
}

const Login: React.FC<LoginProps> = ({loginFn, onUpdateAuth}) => {
    const history = useHistory()
    const classes = useStyles()
    const authContext = useContext(AuthContext)

    const onSubmit = (data: LoginFormData) => {
        loginFn({...data})
            .then((authInfo) => {
                onUpdateAuth(authInfo)
                history.goBack()
            })
    }

    const AlreadyLoggedIn = () => authContext ? <Typography
        variant="h4">{`You are already logged in as ${authContext.name} (${authContext.userName})`}</Typography> : null

    return <Grid container spacing={3}>
        <If condition={authContext === undefined}
            elseRender={<AlreadyLoggedIn/>}>
            <Grid item xs={12}>
                <Typography variant="h4">Login user</Typography>
            </Grid>
            <Grid item xs={12}>
                <Paper className={classes.paper}>
                    <Formik
                        initialValues={{userName: "", password: ""}}
                        validateOnBlur={true}
                        onSubmit={onSubmit}
                        validationSchema={schema}>
                        {
                            () => <Form>
                                <Grid container spacing={3}>
                                    <Grid item xs={12}>
                                        <Field
                                            component={TextField}
                                            id="userName"
                                            label="Username"
                                            name="userName"/>
                                    </Grid>
                                    <Grid item xs={12}>
                                        <Field
                                            component={TextField}
                                            type="password"
                                            id="password"
                                            label="Password"
                                            name="password"
                                        />
                                    </Grid>
                                    <Grid item xs={12}>
                                        <Button variant="contained" aria-label="Login to application"
                                                type="submit">Login</Button>
                                    </Grid>
                                </Grid>
                            </Form>
                        }
                    </Formik>
                </Paper>
            </Grid>
        </If>
    </Grid>
}
Login.propTypes = {
    loginFn: PropTypes.func.isRequired,
    onUpdateAuth: PropTypes.func.isRequired
}

export default Login