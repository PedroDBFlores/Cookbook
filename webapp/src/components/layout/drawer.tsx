import React from "react"
import {Drawer as DrawerMUI, IconButton, List, ListItem, ListItemIcon, ListItemText} from "@material-ui/core"
import {ChevronLeft} from "@material-ui/icons"
import PropTypes from "prop-types"
import {useHistory} from "react-router-dom"
import clsx from "clsx"
import {makeStyles} from "@material-ui/core/styles"

interface DrawerProps {
    isOpen: boolean
    items: Array<DrawerItem>
    onClose: () => void
}

interface DrawerItem {
    icon: React.ReactNode
    name: string
    route: string
}

export const drawerWidth = 240

const useStyles = makeStyles((theme) => ({
    drawerPaper: {
        position: "relative",
        whiteSpace: "nowrap",
        width: drawerWidth,
        transition: theme.transitions.create("width", {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    },
    drawerPaperClose: {
        overflowX: "hidden",
        transition: theme.transitions.create("width", {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
        width: theme.spacing(7),
        [theme.breakpoints.up("sm")]: {
            width: theme.spacing(9),
        },
    }, toolbarIcon: {
        display: "flex",
        alignItems: "center",
        justifyContent: "flex-end",
        padding: "0 8px",
        ...theme.mixins.toolbar,
    }
}))

const Drawer: React.FC<DrawerProps> = ({isOpen, items, onClose}) => {
    const classes = useStyles()
    const history = useHistory()

    const onItemClick = (item: DrawerItem) => history.push(item.route)

    return <DrawerMUI open={isOpen} classes={{
        paper: clsx(classes.drawerPaper, !open && classes.drawerPaperClose),
    }}>
        <div className={classes.toolbarIcon}>
            <IconButton aria-label="Close the drawer" onClick={() => onClose()}>
                <ChevronLeft/>
            </IconButton>
        </div>
        <List>
            {
                items.map((item, idx) =>
                    <ListItem key={`drawer-item-${idx}`} aria-label={`${item.name} menu`}
                              onClick={() => onItemClick(item)}>
                        <ListItemIcon aria-label={`${item.name} menu icon`}>{item.icon} </ListItemIcon>
                        <ListItemText primary={item.name}/>
                    </ListItem>)
            }
        </List>
    </DrawerMUI>
}

Drawer.propTypes = {
    isOpen: PropTypes.bool.isRequired,
    items: PropTypes.array.isRequired,
    onClose: PropTypes.func.isRequired
}

export default Drawer