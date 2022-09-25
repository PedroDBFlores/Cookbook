import React from "react"
import ApplicationToolbar from "./application-toolbar"
import { WrapperWithRouter, WrapperWithRoutes } from "../../../tests/render-helpers"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"

jest.mock("components/theme-mode-toggle/theme-mode-toggle", () => ({
    __esModule: true,
    default: () => <>I'm the theme mode toggler</>
}))


describe("Application Toolbar", () => {
    describe("Layout", () => {
        it("render the initial state", () => {
            render(<WrapperWithRouter>
                <ApplicationToolbar title="A title" />
            </WrapperWithRouter>)

            expect(screen.getByText("A title")).toBeInTheDocument()
            expect(screen.getByText(/translated recipe-type-feature.plural/i)).toBeInTheDocument()
            expect(screen.getByText(/translated recipe-feature.plural/i)).toBeInTheDocument()
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
                elementText: "translated recipe-type-feature.plural",
                expectedRoute: "/recipetype",
                expectedContent: "The best recipe types"
            }],
            ["Recipes", {
                elementText: "translated recipe-feature.plural",
                expectedRoute: "/recipe",
                expectedContent: "The best recipes"
            }]
        ])("navigates to the %s", async (_, { elementText, expectedRoute, expectedContent }) => {
            render(<WrapperWithRoutes initialPath={"/apptoolbar"}
                routeConfiguration={[
                    {
                        path: "/apptoolbar",
                        element: <ApplicationToolbar title="A title" />
                    },
                    {
                        path: expectedRoute,
                        element: <>{expectedContent}</>
                    }
                ]}
            />)

            await userEvent.click(screen.getByText(elementText))

            expect(screen.getByText(expectedContent)).toBeInTheDocument()
        })
    })
})
