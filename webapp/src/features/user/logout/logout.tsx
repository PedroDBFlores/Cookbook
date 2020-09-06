import React, {useEffect} from "react"
import PropTypes from "prop-types"
import {useHistory} from "react-router-dom"
import {AuthInfo} from "../../../contexts/auth-context"
import Typography from "@material-ui/core/Typography"

interface LogoutProps {
    onLogout: () => Promise<void>
    onUpdateAuth: (authInfo: AuthInfo) => void
}

const Logout: React.FC<LogoutProps> = ({onLogout, onUpdateAuth}) => {
    const history = useHistory()

    useEffect(() => {
        onLogout().then(() => {
            onUpdateAuth({isLoggedIn: false, userName: undefined})
            history.push("/")
        })
    }, [])

    return <Typography variant="h4">Logging you out</Typography>
}
Logout.propTypes = {
    onLogout: PropTypes.func.isRequired,
    onUpdateAuth: PropTypes.func.isRequired
}

export default Logout