import React from "react"
import PropTypes from "prop-types"
import {Box, Flex, Heading, Text} from "@chakra-ui/react"
import {useHistory} from "react-router-dom"

interface MenuItemProps {
    onClick?: () => void
}

const MenuItem: React.FC<MenuItemProps> =
    ({children, onClick}) => {
        return <Text mt={{base: 4, md: 0}} mr={6} display="block" onClick={onClick}>
            {children}
        </Text>
    }

MenuItem.propTypes = {
    onClick: PropTypes.func
}

interface ApplicationToolbarProps {
    title: string
}

const ApplicationToolbar: React.FC<ApplicationToolbarProps> = ({
                                                                   title,
                                                               }) => {
    const history = useHistory()
    const [show, setShow] = React.useState(false)
    const handleToggle = () => setShow(!show)

    return (
        <Flex
            as="nav"
            align="center"
            justify="space-between"
            wrap="wrap"
            padding="1.5rem"
            bg="teal.500"
            color="white"
        >
            <Flex align="center" mr={5}>
                <Heading as="h1" size="lg" letterSpacing={"-.1rem"} onClick={() => history.push("/")}>
                    {title}
                </Heading>
            </Flex>

            <Box display={{base: "block", md: "none"}} onClick={handleToggle}>
                <svg
                    fill="white"
                    width="12px"
                    viewBox="0 0 20 20"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <title>Menu</title>
                    <path d="M0 3h20v2H0V3zm0 6h20v2H0V9zm0 6h20v2H0v-2z"/>
                </svg>
            </Box>

            <Box
                display={{sm: show ? "block" : "none", md: "flex"}}
                width={{sm: "full", md: "auto"}}
                alignItems="center"
                flexGrow={1}>
                <MenuItem onClick={() => history.push("/recipetype")}>Recipe types</MenuItem>
                <MenuItem onClick={() => history.push("/recipe")}>Recipes</MenuItem>
            </Box>
        </Flex>
    )
}
ApplicationToolbar.propTypes = {
    title: PropTypes.string.isRequired
}

export default ApplicationToolbar
