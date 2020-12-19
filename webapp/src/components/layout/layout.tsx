import React from "react"
import PropTypes from "prop-types"
import ApplicationToolbar from "./application-toolbar"
import { Container, Grid } from "@chakra-ui/react"

const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    return (
        <>
            <ApplicationToolbar title={"Cookbook"} />
            <main>
                <div style={{ minHeight: "48px" }} />
                <Container maxW="6xl">
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

