import React, {useContext} from "react"
import {useHistory} from "react-router-dom"
import {Button, Typography} from "@material-ui/core"
import {makeStyles} from "@material-ui/core/styles"
import {AuthContext} from "../../services/credentials-service"

const useStyles = makeStyles({
    greeting: {
        padding: "0px 8px"
    }
})

const UserArea: React.FC = () => {
    const history = useHistory()
    const classes = useStyles()
    const authContext = useContext(AuthContext)

    const onLogin = () => {
        history.push("/login")
    }

    const onLogout = () => {
        history.push("/logout")
    }

    return <>
        {
            authContext ?
                <>
                    <Typography className={classes.greeting}>Welcome {authContext.name}</Typography>
                    <Button variant="contained" aria-label={`Logout ${authContext.name}`} color="secondary"
                            onClick={() => onLogout()}>Logout</Button>
                </>
                :
                <>
                    <Button variant="contained" aria-label="Login" color="primary"
                            onClick={() => onLogin()}>Login</Button>
                </>
        }
    </>
}

export default UserArea