import React from "react"
import ApplicationToolbar from "./application-toolbar"
import {Flex} from "@chakra-ui/react"
import {useTranslation} from "react-i18next"

const Layout: React.FC = ({children}) => {
    const {t} = useTranslation()

    return (
        <Flex
            direction="column"
            align="center"
            maxW={{
                base: "auto",
                xl: "1200px"
            }}
            m="0 auto">
            <ApplicationToolbar title={t("app-name")}/>
            {children}
        </Flex>
    )
}

export default Layout
