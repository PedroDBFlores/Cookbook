import React from "react"
import {render, screen} from "@testing-library/react"
import RecipeSearchPage from "../../../../src/features/recipe/search/search-page"

const searchMock = jest.fn()

describe("Search recipe page component", () => {
    it("renders the initial component", () => {
        render(<RecipeSearchPage  />)

        expect(screen.getByText(/search recipes/i)).toBeInTheDocument()
        expect(screen.getByText(/parameters/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe name parameter/)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe description parameter/)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe type parameter/)).toBeInTheDocument()

        expect(screen.getByText(/no matching recipes/i)).toBeInTheDocument()
    })
})