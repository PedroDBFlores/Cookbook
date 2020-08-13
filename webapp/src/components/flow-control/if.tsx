import React from "react"
import PropTypes from "prop-types"

interface IfProps {
    condition: boolean | undefined
    children: React.ReactNode
    elseRender?: React.ReactNode
}

const If: React.FC<IfProps> = ({condition, children, elseRender}) => {
    return <>{condition ? children : elseRender}</>
}

If.propTypes = {
    condition: PropTypes.bool.isRequired,
    children: PropTypes.node.isRequired,
    elseRender: PropTypes.node
}

export default If