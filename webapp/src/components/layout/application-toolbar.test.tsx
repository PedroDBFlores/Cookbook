import React from "react"
import ApplicationToolbar from "./application-toolbar"
import {WrapperWithRouter, WrapperWithRoutes} from "../../../tests/render-helpers"
import {fireEvent, render, screen} from "@testing-library/react"

describe("Application Toolbar", () => {
    describe("Layout", () => {
        it("has the application title", () => {
            render(<WrapperWithRouter>
                <ApplicationToolbar title="A title"/>
            </WrapperWithRouter>)

            expect(screen.getByText("A title")).toBeInTheDocument()
        })

        it("has the expected navigation items", () => {
            render(<WrapperWithRouter>
                <ApplicationToolbar title="ABC"/>
            </WrapperWithRouter>)

            expect(screen.getByText("Recipe types")).toBeInTheDocument()
            expect(screen.getByText("Recipes")).toBeInTheDocument()
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
        ])("navigates to the %s", (_, {elementText, expectedRoute, expectedContent}) => {
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

            fireEvent.click(screen.getByText(elementText))

            expect(screen.getByText(expectedContent)).toBeInTheDocument()
        })
    })
})
