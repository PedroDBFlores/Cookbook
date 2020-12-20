import {Center, Progress, Text} from "@chakra-ui/react"
import React from "react"

const Loader: React.FC = () => <>
    <Progress mb={2} isIndeterminate/>
    <Center><Text fontSize="2xl">Loading...</Text></Center>
</>

export default Loader