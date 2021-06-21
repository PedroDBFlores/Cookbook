import React from "react"
import {IconButton, useColorMode} from "@chakra-ui/react"
import {MdBrightness2, MdWbSunny} from "react-icons/md"
import {useTranslation} from "react-i18next"

const ThemeModeToggle: React.VFC = () => {
    const {t} = useTranslation()
    const {colorMode, toggleColorMode} = useColorMode()
    const oppositeTheme = colorMode === "light" ? "dark" : "light"
    const Icon = colorMode === "light" ? <MdBrightness2/> : <MdWbSunny/>

    return <IconButton aria-label={t("change-to-theme", {themeName: oppositeTheme})}
                       variant="outline"
                       colorScheme="white"
                       icon={Icon}
                       onClick={toggleColorMode}/>
}

export default ThemeModeToggle
 
