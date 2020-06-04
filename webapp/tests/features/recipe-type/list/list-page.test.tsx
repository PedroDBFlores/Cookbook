import React from "react"
import { render, screen, waitFor } from "@testing-library/react"
import RecipeTypeListPage from "../../../../src/features/recipe-type/list/list-page"
import { createRecipeTypeService, RecipeTypeService } from "../../../../src/services/recipe-type-service"

jest.mock("../../../../src/services/recipe-type-service")
const serviceMock = createRecipeTypeService as jest.Mock<RecipeTypeService>
const getAllRecipeTypesMock = jest.fn()
serviceMock.mockImplementation(() => ({
    getAllRecipeTypes: getAllRecipeTypesMock
}))

describe("Recipe type list page", () => {
    describe("Render", () => {
        it("has the required content and gets the recipe types", async () => {
            getAllRecipeTypesMock.mockResolvedValueOnce([])
            render(<RecipeTypeListPage />)
            expect(screen.getByText(/Recipe type list/i)).toBeInTheDocument()

            await screen.findByText("No recipe types found")
            expect(getAllRecipeTypesMock).toHaveBeenCalled()
        })
    })
})