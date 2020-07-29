import React from "react"
import PropTypes from "prop-types"
import {AppBar, Toolbar, IconButton, Typography} from "@material-ui/core"
import MenuIcon from "@material-ui/icons/Menu"

export interface ApplicationToolbarProps {
    title: string
    onMenuClick: () => void
}

const ApplicationToolbar: React.FC<ApplicationToolbarProps> = ({title, onMenuClick}) => {
    return (
        <AppBar position="static">
            <Toolbar>
                <IconButton edge="start" color="inherit" aria-label="menu" onClick={onMenuClick}>
                    <MenuIcon/>
                </IconButton>
                <Typography variant="h6">
                    {title}
                </Typography>
            </Toolbar>
        </AppBar>)
}

ApplicationToolbar.propTypes = {
    title: PropTypes.string.isRequired,
    onMenuClick: PropTypes.func.isRequired
}

export default ApplicationToolbar