import React from "react"
import { render, screen } from "@testing-library/react"
import RecipeTypeListPage from "../../../src/features/recipetype/list/list-page"
import createRecipeTypeService from "../../../src/services/recipe-type-service"
import { generateRecipeType } from "../../helpers/generators/dto-generators"

jest.mock("../../../src/services/recipe-type-service")
const serviceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

const getAllRecipeTypesMock = jest.fn()
serviceMock.mockImplementation(() => ({
    getAll: getAllRecipeTypesMock,
    create: jest.fn(),
    delete: jest.fn(),
    find: jest.fn(),
    update: jest.fn()
}))

describe("Recipe type list page", () => {
    it("has the required content and gets the recipe types", async () => {
        getAllRecipeTypesMock.mockResolvedValueOnce([])
        render(<RecipeTypeListPage />)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await screen.findByText(/no recipe types./i)

        expect(getAllRecipeTypesMock).toHaveBeenCalled()
    })

    it("shows the error", async () => {
        getAllRecipeTypesMock.mockRejectedValueOnce({ message: "Database error" })
        render(<RecipeTypeListPage />)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await screen.findByText(/error: database error/i)
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
    })
})