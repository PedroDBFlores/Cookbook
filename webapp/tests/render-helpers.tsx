import React from "react"
import PropTypes from "prop-types"
import {Route, MemoryRouter, Switch} from "react-router-dom"
import {AxiosInstance} from "axios"
import {ApiHandlerContext} from "../src/services/api-handler"
import {AuthContext, AuthInfo} from "../src/services/credentials-service/credentials-service"
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
    authInfo?: AuthInfo
}

export const WrapWithCommonContexts: React.FC<WrapperWithCommonContexts> = ({
                                                                                children,
                                                                                apiHandler = jest.fn(),
                                                                                authInfo
                                                                            }) => (
    <ApiHandlerContext.Provider value={apiHandler()}>
        <AuthContext.Provider value={authInfo}>
            <SnackbarProvider maxSnack={10}>
                {children}
            </SnackbarProvider>
        </AuthContext.Provider>
    </ApiHandlerContext.Provider>
)

WrapWithCommonContexts.propTypes = {
    apiHandler: PropTypes.func,
    authInfo: PropTypes.any
}


