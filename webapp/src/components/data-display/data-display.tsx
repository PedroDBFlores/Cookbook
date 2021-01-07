import React from "react"
import PropTypes from "prop-types"
import { Heading, Text } from "@chakra-ui/react"

interface DataDisplayProps {
    title: string
    content: string
}

const DataDisplay: React.FC<DataDisplayProps> = ({title, content}) => <>
    <Heading as="h4" size="md">{title}</Heading>
    <Text>{content}</Text>
</>


DataDisplay.propTypes = {
    title: PropTypes.string.isRequired,
    content: PropTypes.string.isRequired
}

export default DataDisplay