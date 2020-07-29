import React, {useState} from "react"
import PropTypes from "prop-types"
import "../../styles/layout.css"
import ApplicationToolbar from "./application-toolbar"
import "./layout.scss"
import {Drawer} from "@material-ui/core"

const Layout: React.FC<{ children?: React.ReactNode }> = ({children}) => {
    const [isDrawerOpen, setDrawerOpen] = useState(false)

    const onMenuClick = () => {
        setDrawerOpen(!isDrawerOpen)
    }

    return (
        <main>
            <ApplicationToolbar title={"Cookbook"} onMenuClick={onMenuClick}/>
            <Drawer open={isDrawerOpen}>

            </Drawer>
            <div className="cookbook-content">
                {children || null}
            </div>
        </main>
    )
}

Layout.propTypes = {
    children: PropTypes.node
}

export default Layout

