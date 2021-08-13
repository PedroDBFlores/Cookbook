import React from "react"

interface IfProps {
    condition: boolean | undefined
    children: React.ReactNode
    elseRender?: React.ReactNode
}

const If: React.FC<IfProps> = ({ condition, children, elseRender }) => <>{condition ? children : elseRender}</>

export default If
