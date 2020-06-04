import React from "react"
import PropTypes from "prop-types"
import "../../styles/layout.css"
import ApplicationToolbar from "./application-toolbar"
import "./layout.scss"

const Layout: React.FC<{ children?: React.ReactNode }> = ({ children }) => {

    return (
        <main>
            <ApplicationToolbar title={"Cookbook"} />
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

