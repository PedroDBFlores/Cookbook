import React from "react"
import PropTypes from "prop-types"
import { MemoryRouter, Route, Routes } from "react-router-dom"
import { WithModal } from "../src/components/modal/modal-context"
import { ChakraProvider } from "@chakra-ui/react"

const MemoryRouterWrapper: React.FC<{ initialPath: string; children?: React.ReactNode }> =
    ({ children, initialPath }) =>
        <MemoryRouter initialEntries={[{ pathname: initialPath }]}>
            <Routes>
                {children}
            </Routes>
        </MemoryRouter>

MemoryRouterWrapper.propTypes = {
    initialPath: PropTypes.string.isRequired
}

interface WrapperWithRouterProps {
    initialPath?: string
    routeConfiguration: Array<{
        path: string
        element: JSX.Element
    }>
}

export const WrapperWithRoutes: React.FC<WrapperWithRouterProps> = ({
    initialPath = "/",
    routeConfiguration
}) =>
    <MemoryRouterWrapper initialPath={initialPath}>
        {
            routeConfiguration.map(routeConfiguration => <Route
                key={routeConfiguration.path} {...routeConfiguration} />)
        }
    </MemoryRouterWrapper>
WrapperWithRoutes.propTypes = {
    initialPath: PropTypes.string,
    routeConfiguration: PropTypes.array.isRequired
}

export const WrapperWithRouter: React.FC<{ children: JSX.Element }> = ({ children }) => <WrapperWithRoutes
    routeConfiguration={[
        {
            path: "/",
            element: children
        }]
    }
/>


export const WrapWithCommonContexts: React.FC<{ children?: React.ReactNode }> = ({
    children,
}) => (
    <ChakraProvider>
        <WithModal>
            {children}
        </WithModal>
    </ChakraProvider>
)

WrapWithCommonContexts.propTypes = {
    children: PropTypes.node
}
