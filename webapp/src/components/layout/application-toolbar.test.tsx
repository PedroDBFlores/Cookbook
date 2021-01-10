import React from "react"
import ApplicationToolbar from "./application-toolbar"
import { WrapperWithRouter, WrapperWithRoutes } from "../../../tests/render-helpers"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

jest.mock("components/theme-mode-toggler/theme-mode-toggler", () => ({
    __esModule: true,
    default: () => <>I'm the theme mode toggler</>
}))


describe("Application Toolbar", () => {
    describe("Layout", () => {
        it("render the initial state", () => {
            render(<WrapperWithRouter>
                <ApplicationToolbar title="A title"/>
            </WrapperWithRouter>)

            expect(screen.getByText("A title")).toBeInTheDocument()
            expect(screen.getByText("Recipe types")).toBeInTheDocument()
            expect(screen.getByText("Recipes")).toBeInTheDocument()
            expect(screen.getByText(/I'm the theme mode toggler/i)).toBeInTheDocument()
        })
    })

    describe("Navigation", () => {
        test.each([
            ["Menu", {
                elementText: "A title",
                expectedRoute: "/",
                expectedContent: "Main page"
            }],
            ["Recipe types", {
                elementText: "Recipe types",
                expectedRoute: "/recipetype",
                expectedContent: "The best recipe types"
            }],
            ["Recipes", {
                elementText: "Recipes",
                expectedRoute: "/recipe",
                expectedContent: "The best recipes"
            }]
        ])("navigates to the %s", (_, { elementText, expectedRoute, expectedContent }) => {
            render(<WrapperWithRoutes initialPath={"/apptoolbar"}
                routeConfiguration={[
                    {
                        path: "/apptoolbar",
                        exact: true,
                        component: () => <ApplicationToolbar title="A title"/>
                    },
                    {
                        path: expectedRoute,
                        exact: true,
                        component: () => <>{expectedContent}</>
                    }
                ]}
            />)

            userEvent.click(screen.getByText(elementText))

            expect(screen.getByText(expectedContent)).toBeInTheDocument()
        })
    })
})
