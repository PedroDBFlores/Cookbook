import React from "react"
import PropTypes from "prop-types"
import {MemoryRouter, Route, Switch} from "react-router-dom"
import {AxiosInstance} from "axios"
import {ApiHandlerContext} from "../src/services/api-handler"
import {SnackbarProvider} from "notistack"

const MemoryRouterWrapper: React.FC<{ initialPath: string }> =
    ({children, initialPath}) =>
        <MemoryRouter initialEntries={[{pathname: initialPath}]}>
            <Switch>
                {children}
            </Switch>
        </MemoryRouter>

MemoryRouterWrapper.propTypes = {
    initialPath: PropTypes.string.isRequired
}

interface WrapperWithRouterProps {
    initialPath?: string
    routeConfiguration: Array<{
        exact: boolean
        path: string
        component: () => JSX.Element
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

export const WrapperWithRouter: React.FC = ({children}) => <WrapperWithRoutes
    routeConfiguration={[
        {
            path: "/",
            exact: true,
            component: () => <>{children}</>
        }]
    }
/>

interface WrapperWithCommonContexts {
    apiHandler?: () => AxiosInstance
}

export const WrapWithCommonContexts: React.FC<WrapperWithCommonContexts> = ({
                                                                                children,
                                                                                apiHandler = jest.fn()
                                                                            }) => (
    <ApiHandlerContext.Provider value={apiHandler()}>
        <SnackbarProvider maxSnack={10}>
            {children}
        </SnackbarProvider>
    </ApiHandlerContext.Provider>
)

WrapWithCommonContexts.propTypes = {
    apiHandler: PropTypes.func
}


