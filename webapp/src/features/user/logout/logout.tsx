import React, {useEffect} from "react"
import PropTypes from "prop-types"
import {useHistory} from "react-router-dom"
import Typography from "@material-ui/core/Typography"
import {AuthInfo} from "../../../services/credentials-service/credentials-service"

interface LogoutProps {
    onLogout: () => Promise<void>
    onUpdateAuth: (authInfo: AuthInfo | undefined) => void
}

const Logout: React.FC<LogoutProps> = ({onLogout, onUpdateAuth}) => {
    const history = useHistory()

    useEffect(() => {
        onLogout().then(() => {
            onUpdateAuth(undefined)
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