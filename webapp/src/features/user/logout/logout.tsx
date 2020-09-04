import React, {useEffect} from "react"
import PropTypes from "prop-types"
import {useHistory} from "react-router-dom"
import {AuthInfo} from "../../../contexts/auth-context"
import Typography from "@material-ui/core/Typography"

interface LogoutProps {
    logoutFn: () => Promise<void>
    updateAuthContextFn: (authInfo: AuthInfo) => void
}

const Logout: React.FC<LogoutProps> = ({logoutFn, updateAuthContextFn}) => {
    const history = useHistory()

    useEffect(() => {
        logoutFn().then(() => {
            updateAuthContextFn({isLoggedIn: false, userName: undefined})
            history.push("/")
        })
    }, [])

    return <Typography variant="h4">Logging you out</Typography>
}
Logout.propTypes = {
    logoutFn: PropTypes.func.isRequired,
    updateAuthContextFn: PropTypes.func.isRequired
}

export default Logout