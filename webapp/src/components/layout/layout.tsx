import React, {useState} from "react"
import PropTypes from "prop-types"
import "../../styles/layout.css"
import ApplicationToolbar from "./application-toolbar"
import "./layout.css"
import {Container, CssBaseline, Grid} from "@material-ui/core"
import {makeStyles} from "@material-ui/core/styles"
import Drawer from "./drawer"
import {List, Home} from "@material-ui/icons"

const useStyles = makeStyles((theme) => ({
    root: {
        display: "flex",
    },
    title: {
        flexGrow: 1,
    },
    appBarSpacer: theme.mixins.toolbar,
    content: {
        flexGrow: 1,
        height: "100vh",
        overflow: "auto",
    },
    container: {
        paddingTop: theme.spacing(4),
        paddingBottom: theme.spacing(4),
    },
    paper: {
        padding: theme.spacing(2),
        display: "flex",
        overflow: "auto",
        flexDirection: "column",
    },
    fixedHeight: {
        height: 240,
    },
    toolbar: {
        paddingRight: 24,
    }
}))

const Layout: React.FC<{ children?: React.ReactNode }> = ({children}) => {
    const classes = useStyles()
    const [isDrawerOpen, setDrawerOpen] = useState(false)

    const onMenuClick = () => {
        setDrawerOpen(!isDrawerOpen)
    }

    const drawerItems = [
        {name: "Home", icon: <Home/>, route: "/"},
        {name: "Recipe Types", icon: <List/>, route: "/recipetype"}
    ]

    return (
        <div className={classes.root}>
            <CssBaseline/>
            <ApplicationToolbar title={"Cookbook"} onMenuClick={onMenuClick} drawerWidth={240}
                                isDrawerOpen={isDrawerOpen}/>
            <Drawer isOpen={isDrawerOpen} items={drawerItems} onClose={() => setDrawerOpen(false)}/>
            <main className={classes.content}>
                <div className={classes.appBarSpacer} style={{minHeight: "64px"}}/>
                <Container maxWidth="lg" className={classes.container}>
                    <Grid container spacing={3}>
                        {children || null}
                    </Grid>
                </Container>
            </main>
        </div>
    )
}

Layout.propTypes = {
    children: PropTypes.node
}

export default Layout

