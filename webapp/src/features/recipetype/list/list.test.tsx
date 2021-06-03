import React from "react"
import { render, screen } from "@testing-library/react"
import RecipeTypeList from "./list"
import userEvent from "@testing-library/user-event"
import { WrapperWithRoutes } from "../../../../tests/render-helpers"
import { RecipeType } from "../../../services/recipe-type-service"

describe("Recipe type list component", () => {
    const recipeTypes: Array<RecipeType> = [
        { id: 1, name: "Vegetarian" },
        { id: 2, name: "Fish" }
    ]

    describe("Render", () => {
        it("shows 'No recipes types.' if there are none", () => {
            render(<RecipeTypeList recipeTypes={[]} onDelete={jest.fn()}/>)

            expect(screen.getByText(/^translated recipe-type-feature.list.no-results$/i)).toBeInTheDocument()
        })

        it("shows a table with the required headers and data", () => {
            render(<RecipeTypeList recipeTypes={recipeTypes} onDelete={jest.fn()}/>)

            expect(screen.getByText(/^translated id$/i)).toBeInTheDocument()
            expect(screen.getByText(/^translated name$/i)).toBeInTheDocument()
            expect(screen.getByText(/^translated actions$/i)).toBeInTheDocument()
            recipeTypes.forEach(element => {
                expect(screen.getByText(element.id.toString())).toBeInTheDocument()
                expect(screen.getByText(element.name)).toBeInTheDocument()
            })
        })
    })

    describe("Actions", () => {
        it("navigates to the recipe type details", async() => {
            const firstRecipeType = recipeTypes[0]

            render(<WrapperWithRoutes initialPath={"/recipetype"} routeConfiguration={[
                {
                    path: "/recipetype",
                    exact: true,
                    component: () => <RecipeTypeList recipeTypes={recipeTypes} onDelete={jest.fn()}/>
                },
                {
                    path: `/recipetype/${firstRecipeType.id}/details`,
                    exact: true,
                    component: () => <>I'm the recipe type details page</>
                }
            ]}/>)

            userEvent.click(screen.getByLabelText(`translated recipe-type-feature.list.details-for-label #${firstRecipeType.id}#`, {
                selector: "button"
            }))

            expect(await screen.findByText(/i'm the recipe type details page/i)).toBeInTheDocument()
        })

        it("navigates to the recipe type edit page", async() => {
            const firstRecipeType = recipeTypes[0]

            render(<WrapperWithRoutes initialPath={"/recipetype"} routeConfiguration={[
                {
                    path: "/recipetype",
                    exact: true,
                    component: () => <RecipeTypeList recipeTypes={recipeTypes} onDelete={jest.fn()}/>
                },
                {
                    path: `/recipetype/${firstRecipeType.id}/edit`,
                    exact: true,
                    component: () => <>I'm the recipe type edit page</>
                }
            ]}/>)

            userEvent.click(screen.getByLabelText(`translated recipe-type-feature.list.edit-for-label #${firstRecipeType.id}#`, {
                selector: "button"
            }))

            expect(await screen.findByText(/i'm the recipe type edit page/i)).toBeInTheDocument()
        })

        it("deletes a recipe type", () => {
            const onDeleteMock = jest.fn()
            const firstRecipeType = recipeTypes[0]

            render(<RecipeTypeList recipeTypes={recipeTypes} onDelete={onDeleteMock}/>)

            userEvent.click(screen.getByLabelText(`translated recipe-type-feature.list.delete-for-label #${firstRecipeType.id}#`, {
                selector: "button"
            }))

            expect(onDeleteMock).toHaveBeenCalledWith(firstRecipeType.id, firstRecipeType.name)
        })
    })
})
