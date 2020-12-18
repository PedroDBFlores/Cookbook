import React from "react"
import PropTypes from "prop-types"
import ApplicationToolbar from "./application-toolbar"
import { Container, Grid } from "@chakra-ui/react"

const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    return (
        <>
            <ApplicationToolbar title={"Cookbook"} />
            <main>
                <div style={{ minHeight: "64px" }} />
                <Container centerContent>
                    <Grid>
                        {children}
                    </Grid>
                </Container>
            </main>
        </>
    )
}
Layout.propTypes = {
    children: PropTypes.node.isRequired
}

export default Layout

