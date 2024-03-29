import React from "react"
import {Box, Heading, Text} from "@chakra-ui/react"

interface DataDisplayProps {
    title: string
    content: string
}

const DataDisplay: React.FC<DataDisplayProps> = ({ title, content }) => <Box>
    <Heading as="h4" size="md">{title}</Heading>
    {
        content.split("\n").map((line, idx) => <Text key={`${title}_line_${idx + 1}`} as="p">{line}</Text>)
    }
</Box>

export default DataDisplay
