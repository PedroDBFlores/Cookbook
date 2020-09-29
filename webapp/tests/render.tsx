/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-function-return-type */
import React, { ReactNode, } from "react"
import { render } from "@testing-library/react"
import { Route, MemoryRouter, Switch } from "react-router-dom"

interface Props {
    children: ReactNode
}

const MemoryRouterWrapper = (initialPath: string) => ({children}: Props) =>
    <MemoryRouter initialEntries={[{pathname: initialPath}]}>
        <Switch>
            {children}
        </Switch>
    </MemoryRouter>

export const renderWrapped = (Component: any, wrapper?: any) => render(Component, {wrapper})
export const renderWithRouter = (Component: any, options?: any) => {
    options = {exact: false, path: "/", initialPath: "/"}
    return renderWrapped(
        <Route exact={options.exact} path={options.path} render={() => Component}/>,
        MemoryRouterWrapper(options.initialPath)
    )
}
export const renderWithRoutes = (routes: { [route: string]: () => JSX.Element }, initialPath = "/") =>
    renderWrapped(
        Object.keys(routes).map(key => <Route exact key={key} path={key} render={routes[key]}/>),
        MemoryRouterWrapper(initialPath)
    )



