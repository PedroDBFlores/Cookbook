/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-function-return-type */
import React, {ReactNode} from "react"
import {render} from "@testing-library/react"
import {Route, MemoryRouter, Switch} from "react-router-dom"
import {SnackbarProvider} from "notistack"
import {AxiosInstance} from "axios"
import {ApiHandlerContext} from "../src/services/api-handler"
import {AuthContext, AuthInfo} from "../src/services/credentials-service"

interface Props {
    children: ReactNode
}

interface CommonWrappingContextsProps extends Props {
    apiHandler: () => AxiosInstance
    authInfo?: AuthInfo
}

const CommonWrappingContexts = ({
                                    children,
                                    apiHandler = jest.fn(),
                                    authInfo = {userId: 666, name: "ALARMA", userName: "alarma"}
                                }: CommonWrappingContextsProps) =>
    <ApiHandlerContext.Provider value={apiHandler()}>
        <AuthContext.Provider value={authInfo}>
            <SnackbarProvider maxSnack={100}>
                {children}
            </SnackbarProvider>
        </AuthContext.Provider>
    </ApiHandlerContext.Provider>


const MemoryRouterWrapper = (initialPath: string) => ({children}: Props) =>
    <CommonWrappingContexts apiHandler={jest.fn()}>
        <MemoryRouter initialEntries={[{pathname: initialPath}]}>
            <Switch>
                {children}
            </Switch>
        </MemoryRouter>
    </CommonWrappingContexts>

export const renderWrapped = (Component: any, wrapper?: any) => render(Component, {wrapper})

export const renderWithRouter = (Component: any, options?: any) => {
    options = {exact: false, path: "/", initialPath: "/"}
    return renderWrapped(
        <Route exact={options.exact} path={options.path} render={() => Component}/>,
        MemoryRouterWrapper(options.initialPath)
    )
}

export const renderWithRoutes = (
    routes: { [route: string]: () => JSX.Element },
    initialPath = "/") =>
    renderWrapped(
        Object.keys(routes).map(key => <Route exact key={key} path={key} render={routes[key]}/>),
        MemoryRouterWrapper(initialPath)
    )

export const renderWrappedInCommonContexts = (component: JSX.Element,
                                              apiHandler: () => AxiosInstance = jest.fn()) =>
    render(<CommonWrappingContexts apiHandler={apiHandler}>
        {component}
    </CommonWrappingContexts>)



