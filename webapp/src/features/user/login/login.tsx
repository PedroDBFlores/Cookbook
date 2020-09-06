import React, {useContext} from "react"
import PropTypes from "prop-types"
import Button from "@material-ui/core/Button"
import Grid from "@material-ui/core/Grid"
import Typography from "@material-ui/core/Typography"
import * as yup from "yup"
import {Formik} from "formik"
import If from "../../../components/flow-control/if"
import {useHistory} from "react-router-dom"
import {Credentials} from "../../../model"
import makeStyles from "@material-ui/core/styles/makeStyles"
import {Theme} from "@material-ui/core/styles/createMuiTheme"
import createStyles from "@material-ui/core/styles/createStyles"
import Paper from "@material-ui/core/Paper"
import TextField from "@material-ui/core/TextField"
import AuthContext, {AuthInfo} from "../../../contexts/auth-context"

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
    loginFn: (credentials: Credentials) => Promise<void>
    onUpdateAuth: (authInfo: AuthInfo) => void
}

const Login: React.FC<LoginProps> = ({loginFn, onUpdateAuth}) => {
    const history = useHistory()
    const classes = useStyles()
    const {isLoggedIn, userName} = useContext(AuthContext)

    const onSubmit = (data: LoginFormData) => {
        loginFn({...data})
            .then(() => {
                onUpdateAuth({isLoggedIn: true, userName: data.userName})
                history.goBack()
            })
    }

    const AlreadyLoggedIn = () => <Typography variant="h4">{`You are already logged in as ${userName}`}</Typography>

    return <Grid container spacing={3}>
        <If condition={!isLoggedIn}
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
                                                id="userName"
                                                label="Username"
                                                name="userName"
                                                value={values.userName}
                                                onChange={handleChange}
                                                variant="outlined"
                                                error={!!errors?.userName}
                                                helperText={!!errors?.userName && "Username is required"}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TextField
                                                id="password"
                                                label="Password"
                                                name="password"
                                                type="password"
                                                value={values.password}
                                                onChange={handleChange}
                                                variant="outlined"
                                                error={!!errors?.password}
                                                helperText={!!errors?.password && "Password is required"}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Button variant="contained" aria-label="Login to application"
                                                    type="submit">Login</Button>
                                        </Grid>
                                    </Grid>
                                </form>
                            )
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