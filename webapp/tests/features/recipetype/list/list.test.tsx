import React from "react"
import { render, screen, waitFor, fireEvent } from "@testing-library/react"
import RecipeTypeList from "../../../../src/features/recipetype/list/list"
import { generateRecipeType } from "../../../helpers/generators/dto-generators"
import userEvent from "@testing-library/user-event"
import { renderWithRoutes } from "../../../render"

describe("Recipe type list component", () => {
    describe("Render", () => {
        it("shows 'No recipes types.' if there are none", async () => {
            render(<RecipeTypeList recipeTypes={[]} onDelete={jest.fn()} />)

            expect(screen.getByText(/No recipe types/i)).toBeInTheDocument()
        })

        it("shows a table with the required headers", async () => {
            render(<RecipeTypeList recipeTypes={[generateRecipeType()]} onDelete={jest.fn()} />)

            expect(screen.getByText(/id/i)).toBeInTheDocument()
            expect(screen.getByText(/name/i)).toBeInTheDocument()
            expect(screen.getByText(/actions/i)).toBeInTheDocument()
        })

        it("shows the provided data", async () => {
            const recipeTypes = [
                generateRecipeType(),
                generateRecipeType()
            ]

            render(<RecipeTypeList recipeTypes={recipeTypes} onDelete={jest.fn()} />)

            recipeTypes.forEach(element => {
                expect(screen.getByText(element.id.toString())).toBeInTheDocument()
                expect(screen.getByText(element.name)).toBeInTheDocument()
            })
        })
    })

    describe("Actions", () => {
        const recipeTypes = [
            generateRecipeType(),
            generateRecipeType()
        ]

        it("has a set of actions for the recipe type", async () => {
            render(<RecipeTypeList recipeTypes={recipeTypes} onDelete={jest.fn()} />)

            const detailButton = screen.getByLabelText(`Recipe type details for id ${recipeTypes[0].id}`, {
                selector: "button"
            })
            const editButton = screen.getByLabelText(`Edit Recipe type with id ${recipeTypes[0].id}`, {
                selector: "button"
            })
            const deleteButton = screen.getByLabelText(`Delete Recipe type with id ${recipeTypes[0].id}`, {
                selector: "button"
            })

            expect(detailButton).toBeInTheDocument()
            expect(editButton).toBeInTheDocument()
            expect(deleteButton).toBeInTheDocument()
        })


        it("navigates to the recipe type details", async () => {
            const firstRecipeType = recipeTypes[0]
            renderWithRoutes({
                "/recipetype": () => <RecipeTypeList recipeTypes={recipeTypes} onDelete={jest.fn()} />,
                [`/recipetype/${firstRecipeType.id}`]: () => <div>I'm the recipe type details page</div>
            }, "/recipetype")
            const detailsButton = screen.getByLabelText(`Recipe type details for id ${firstRecipeType.id}`, {
                selector: "button"
            })

            fireEvent.click(detailsButton)

            expect(await screen.findByText(/i'm the recipe type details page/i)).toBeInTheDocument()
        })

        it("navigates to the recipe type edit page", async () => {
            const firstRecipeType = recipeTypes[0]
            renderWithRoutes({
                "/recipetype": () => <RecipeTypeList recipeTypes={recipeTypes} onDelete={jest.fn()} />,
                [`/recipetype/${firstRecipeType.id}/edit`]: () => <div>I'm the recipe type edit page</div>
            }, "/recipetype")
            const editButton = screen.getByLabelText(`Edit Recipe type with id ${firstRecipeType.id}`, {
                selector: "button"
            })

            fireEvent.click(editButton)

            expect(await screen.findByText(/i'm the recipe type edit page/i)).toBeInTheDocument()
        })

        it("deletes a recipe type", async () => {
            const onDeleteMock = jest.fn()
            const firstRecipeType = recipeTypes[0]
            render(<RecipeTypeList recipeTypes={recipeTypes} onDelete={onDeleteMock} />)
            const deleteButton = screen.getByLabelText(`Delete Recipe type with id ${firstRecipeType.id}`, {
                selector: "button"
            })

            userEvent.click(deleteButton)

            await waitFor(() => {
                expect(onDeleteMock).toHaveBeenCalledWith(firstRecipeType.id)
            })
        })
    })
})
