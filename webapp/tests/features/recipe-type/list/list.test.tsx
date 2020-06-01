import React from "react"
import { render, screen } from "@testing-library/react"
import RecipeTypeList from "../../../../src/features/recipe-type/list/list"
import { generateRecipeType } from "../../../helpers/generators/dto-generators"

describe("Recipe type list", () => {
    describe("Render", () => {
        it("shows a table with the required headers", async () => {
            render(<RecipeTypeList recipeTypes={[]} />)

            expect(screen.getByText(/id/i)).toBeInTheDocument()
            expect(screen.getByText(/name/i)).toBeInTheDocument()
        })

        it("shows the provided data", async() => {
            const recipeTypes = [
                generateRecipeType(),
                generateRecipeType()
            ]
            render(<RecipeTypeList recipeTypes={recipeTypes} />)

            recipeTypes.forEach(element => {
                expect(screen.getByText(element.id.toString())).toBeInTheDocument()
                expect(screen.getByText(element.name)).toBeInTheDocument()
            });
        })
    })
})