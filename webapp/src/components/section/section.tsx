import React from "react"
import PropTypes from "prop-types"
import { Box, Flex, Heading } from "@chakra-ui/react"

interface SectionProps {
    title: React.ReactElement | string
    actions?: React.ReactElement
}

const Section: React.FC<SectionProps> = ({ title, actions, children }) => <Flex
    direction="column"
    w="100%">
    <Flex align="center"
        justify="space-between"
        wrap="wrap"
        w="100%"
        mb="8">
        <Flex align="center">
            {
                React.isValidElement(title) ? title
                    : <Heading as="h4">{title}</Heading>
            }
        </Flex>
        <Flex align="center">
            {actions}
        </Flex>
    </Flex>
    <Box>
        {children}
    </Box>
</Flex>

Section.propTypes = {
    title: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.element
    ]).isRequired,
    actions: PropTypes.element
}

export default Section
