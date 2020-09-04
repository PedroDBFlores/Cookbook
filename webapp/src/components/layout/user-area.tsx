import React, {useContext} from "react"
import {useHistory} from "react-router-dom"
import Button from "@material-ui/core/Button"
import Typography from "@material-ui/core/Typography"
import makeStyles from "@material-ui/core/styles/makeStyles"
import AuthContext from "../../contexts/auth-context"

const useStyles = makeStyles({
    greeting: {
        padding: "0px 8px"
    }
})

const UserArea: React.FC<unknown> = () => {
    const history = useHistory()
    const classes = useStyles()
    const {isLoggedIn, userName} = useContext(AuthContext)

    const onLogin = () => {
        history.push("/login")
    }

    const onLogout = () => {
        history.push("/logout")
    }

    return <>
        {
            isLoggedIn ?
                <>
                    <Typography className={classes.greeting}>Welcome {userName}</Typography>
                    <Button variant="contained" aria-label={`Logout ${userName}`} color="secondary"
                            onClick={() => onLogout()}>Logout</Button>
                </>
                :
                <>
                    <Button variant="contained" aria-label="Login" color="default"
                            onClick={() => onLogin()}>Login</Button>
                </>
        }
    </>
}

export default UserArea