import React from "react"
import PropTypes from "prop-types"
import {AppBar, Toolbar, IconButton, Typography, Theme} from "@material-ui/core"
import MenuIcon from "@material-ui/icons/Menu"
import {makeStyles} from "@material-ui/core/styles"
import clsx from "clsx"

const useStyles = makeStyles<Theme, { drawerWidth: number }>((theme) =>
    ({
        appBar: {
            zIndex: theme.zIndex.drawer + 1,
            transition: theme.transitions.create(["width", "margin"], {
                easing: theme.transitions.easing.sharp,
                duration: theme.transitions.duration.leavingScreen,
            }),
        },
        appBarShift: {
            marginLeft: props => props.drawerWidth,
            width: props => `calc(100% - ${props.drawerWidth}px)`,
            transition: theme.transitions.create(["width", "margin"], {
                easing: theme.transitions.easing.sharp,
                duration: theme.transitions.duration.enteringScreen,
            }),
        },
        toolbar: {
            paddingRight: 24,
        },
        toolbarIcon: {
            display: "flex",
            alignItems: "center",
            justifyContent: "flex-end",
            padding: "0 8px",
            ...theme.mixins.toolbar,
        },
        menuButton: {
            marginRight: 36,
        },
        menuButtonHidden: {
            display: "none",
        }, title: {
            flexGrow: 1,
        },
    }))

export interface ApplicationToolbarProps {
    title: string
    onMenuClick: () => void
    drawerWidth: number
    isDrawerOpen: boolean
}

const ApplicationToolbar: React.FC<ApplicationToolbarProps> = ({
                                                                   title,
                                                                   onMenuClick,
                                                                   drawerWidth,
                                                                   isDrawerOpen
                                                               }) => {
    const classes = useStyles({drawerWidth})

    return (
        <AppBar position="absolute" className={clsx(classes.appBar, isDrawerOpen && classes.appBarShift)}>
            <Toolbar className={classes.toolbar}>
                <IconButton edge="start" color="inherit" aria-label="menu"
                            onClick={onMenuClick}
                            className={clsx(classes.menuButton, isDrawerOpen && classes.menuButtonHidden)}>
                    <MenuIcon/>
                </IconButton>
                <Typography component="h1" variant="h6" color="inherit" noWrap className={classes.title}>
                    {title}
                </Typography>
            </Toolbar>
        </AppBar>)
}

ApplicationToolbar.propTypes = {
    title: PropTypes.string.isRequired,
    onMenuClick: PropTypes.func.isRequired,
    drawerWidth: PropTypes.number.isRequired,
    isDrawerOpen: PropTypes.bool.isRequired
}

export default ApplicationToolbar
