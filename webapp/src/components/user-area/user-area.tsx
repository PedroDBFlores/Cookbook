import React, {useEffect, useState} from "react"
import jwt_decode from "jwt-decode"
import {useHistory} from "react-router-dom"
import Button from "@material-ui/core/Button"
import Typography from "@material-ui/core/Typography"
import makeStyles from "@material-ui/core/styles/makeStyles"

interface TokenPayload {
    name: string
}

const useStyles = makeStyles({
    greeting: {
        padding: "0px 8px"
    }
})

const UserArea: React.FC<unknown> = () => {
    const history = useHistory()
    const classes = useStyles()
    const [payload, setPayload] = useState<TokenPayload | undefined>()

    useEffect(() => {
        const token = localStorage.getItem("token")
        const payload = token ? jwt_decode(token as string) as TokenPayload : undefined
        setPayload(payload)
    }, [])

    const onLogin = () => {
        history.push("/login")
    }

    const onLogout = () => {
        history.push("/logout")
    }

    return <>
        {
            payload ?
                <>
                    <Typography className={classes.greeting}>Welcome {payload.name}</Typography>
                    <Button variant="contained" aria-label={`Logout ${payload.name}`} color="secondary"
                            onClick={() => onLogout()}>Logout</Button>
                </>
                :
                <>
                    <Button variant="contained" aria-label="Login" color="secondary"
                            onClick={() => onLogin()}>Login</Button>
                </>
        }
    </>
}

export default UserArea