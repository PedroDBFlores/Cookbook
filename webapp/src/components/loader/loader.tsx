import {Center, Progress, Text} from "@chakra-ui/react"
import React from "react"
import {useTranslation} from "react-i18next"

const Loader: React.VFC = () => {
    const {t} = useTranslation()

    return (
        <>
            <Progress mb={2} isIndeterminate />
            <Center><Text fontSize="2xl">{t("common.loading")}</Text></Center>
        </>
    )
}

export default Loader
