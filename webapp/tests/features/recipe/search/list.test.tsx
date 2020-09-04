import React from "react"
import {render, screen} from "@testing-library/react"
import RecipeList from "../../../../src/features/recipe/search/list"

describe("Recipe list component", () => {
    describe("Render", () => {
        it("shows 'No matching recipes' when there are no recipes", () => {
            render(<RecipeList recipes={[]}/>)

            expect(screen.getByText(/No matching recipes/)).toBeInTheDocument()
        })
    })
})