import React from "react"
import {IconButton, useColorMode} from "@chakra-ui/react"
import {MdBrightness2, MdWbSunny} from "react-icons/md"

const ThemeModeToggler: React.FC = () => {
    const {colorMode, toggleColorMode} = useColorMode()
    const ariaLabel = `Change to ${colorMode === "light" ? "dark" : "light"} theme`
    const Icon = colorMode === "light" ? <MdBrightness2/> : <MdWbSunny/>

    return <IconButton aria-label={ariaLabel} icon={Icon} onClick={toggleColorMode}/>
}

export default ThemeModeToggler