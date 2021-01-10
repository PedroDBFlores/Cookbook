import React from "react"
import PropTypes from "prop-types"
import { Flex, Heading, Text } from "@chakra-ui/react"
import { Link } from "react-router-dom"
import ThemeModeToggler from "../theme-mode-toggler/theme-mode-toggler"

interface MenuItemProps {
    to?: string
    isLast?: boolean
}

const MenuItem: React.FC<MenuItemProps> = ({ children, isLast, to = "/" }) => {
    return (
        <Text
            mb={{ base: isLast ? 0 : 8, sm: 0 }}
            mr={{ base: 0, sm: isLast ? 0 : 8 }}
            display="block"
        >
            <Link to={to}>{children}</Link>
        </Text>
    )
}

MenuItem.propTypes = {
    to: PropTypes.string,
    isLast: PropTypes.bool
}

interface ApplicationToolbarProps {
    title: string
}

const ApplicationToolbar: React.FC<ApplicationToolbarProps> = ({ title }) => {
    return (
        <Flex
            as="nav"
            align="center"
            justify="space-between"
            wrap="wrap"
            w="100%"
            mb={8}
            p={8}
            bg="blue.500"
            color="white">
            <Flex align="center">
                <Heading as="h1" size="lg" letterSpacing={"-.1rem"}>
                    <Link to="/">{title}</Link>
                </Heading>
            </Flex>

            <Flex
                align={["center", "center", "center", "center"]}
                justify={["center", "space-between", "flex-end", "flex-end"]}
                direction={["column", "row", "row", "row"]}
                pt={[4, 4, 0, 0]}>
                <MenuItem to="/recipetype">Recipe types</MenuItem>
                <MenuItem to="/recipe">Recipes</MenuItem>
                <ThemeModeToggler />
            </Flex>
        </Flex >
    )
}
ApplicationToolbar.propTypes = {
    title: PropTypes.string.isRequired
}

export default ApplicationToolbar
