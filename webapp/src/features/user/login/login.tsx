import React, {useContext} from "react"
import Button from "@material-ui/core/Button"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import * as yup from "yup"
import {Field, Formik, Form} from "formik"
import If from "../../../components/flow-control/if"
import {useHistory} from "react-router-dom"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import Paper from "@material-ui/core/Paper"
import {TextField} from "formik-material-ui"
import {Credentials, AuthInfo, AuthContext} from "../../../services/credentials-service"

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

    const AlreadyLoggedIn = () => authContext ? <Typography variant="h4">{`You are already logged in as ${authContext.name} (${authContext.userName})`}</Typography> : null

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
                                            id="password"
                                            label="Password"
                                            name="password"/>
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

export default Login