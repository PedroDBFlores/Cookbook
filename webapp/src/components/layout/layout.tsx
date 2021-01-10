import React from "react"
import ApplicationToolbar from "./application-toolbar"
import { Flex } from "@chakra-ui/react"

const Layout: React.FC = ({ children }) => {
    return (
        <Flex
            direction="column"
            align="center"
            maxW={{
                base: "auto",
                xl: "1200px"
            }}
            m="0 auto">
            <ApplicationToolbar title={"Cookbook"} />
            {children}
        </Flex>
    )
}

export default Layout

