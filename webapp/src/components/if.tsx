import React from "react"
import PropTypes from "prop-types"

interface IfProps {
    condition: boolean;
    renderContent: React.ReactNode;
    elseRenderContent?: React.ReactNode | undefined;
}

const If: React.FC<IfProps> = ({ condition, renderContent, elseRenderContent }) => <>{condition ? renderContent : (elseRenderContent || null)}</>

If.propTypes = {
    condition: PropTypes.bool.isRequired,
    renderContent: PropTypes.node.isRequired,
    elseRenderContent: PropTypes.node
}

export default If