import React from "react"
import {Flex, Heading, Text} from "@chakra-ui/react"
import {Link} from "react-router-dom"
import ThemeModeToggle from "../theme-mode-toggle/theme-mode-toggle"
import {useTranslation} from "react-i18next"

interface MenuItemProps {
    to?: string
    isLast?: boolean
    children?: React.ReactNode
}

const MenuItem: React.FC<MenuItemProps> = ({children, isLast, to = "/"}) => (
    <Text
        mb={{base: isLast ? 0 : 8, sm: 0}}
        mr={{base: 0, sm: isLast ? 0 : 8}}
        display="block">
        <Link to={to}>{children}</Link>
    </Text>
)

interface ApplicationToolbarProps {
    title: string
}

const ApplicationToolbar: React.VFC<ApplicationToolbarProps> = ({title}) => {
    const {t} = useTranslation()

    return (
        <Flex
            as="nav"
            align="center"
            justify="space-between"
            wrap="wrap"
            w="100%"
            mb={8}
            p={4}
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
                <MenuItem to="/recipetype">{t("recipe-type-feature.plural")}</MenuItem>
                <MenuItem to="/recipe">{t("recipe-feature.plural")}</MenuItem>
                <ThemeModeToggle/>
            </Flex>
        </Flex>
    )
}

export default ApplicationToolbar
