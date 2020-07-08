import React from "react"
import { render, screen } from "@testing-library/react"
import RecipeTypeList from "../../../src/features/recipetype/list/list"
import { generateRecipeType } from "../../helpers/generators/dto-generators"

describe("Recipe type list", () => {
    describe("Render", () => {
        it("shows 'No recipes types.' if there are none", async () => {
            render(<RecipeTypeList recipeTypes={[]} />)

            expect(screen.getByText(/No recipe types/i)).toBeInTheDocument()
        })

        it("shows a table with the required headers", async () => {
            render(<RecipeTypeList recipeTypes={[generateRecipeType()]} />)

            expect(screen.getByText(/id/i)).toBeInTheDocument()
            expect(screen.getByText(/name/i)).toBeInTheDocument()
            expect(screen.getByText(/actions/i)).toBeInTheDocument()
        })

        it("shows the provided data", async () => {
            const recipeTypes = [
                generateRecipeType(),
                generateRecipeType()
            ]
            render(<RecipeTypeList recipeTypes={recipeTypes} />)

            recipeTypes.forEach(element => {
                expect(screen.getByText(element.id.toString())).toBeInTheDocument()
                expect(screen.getByText(element.name)).toBeInTheDocument()
            })
        })
    })

    describe("Actions", () => {
        it("has a set of actions for the recipe type", async () => {
            const recipeTypes = [
                generateRecipeType(),
                generateRecipeType()
            ]

            render(<RecipeTypeList recipeTypes={recipeTypes} />)
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
    })
})