import React from "react"
import { render, screen, waitFor } from "@testing-library/react"
import RecipeTypeDetails from "../../../../src/features/recipetype/details/details"
import { findRecipeType } from "../../../../src/services/recipe-type-service"
import { generateRecipeType } from "../../../helpers/generators/dto-generators"


jest.mock("../../../../src/services/recipe-type-service")
const findRecipeTypeMock = findRecipeType as jest.MockedFunction<typeof findRecipeType>

describe("Recipe type details", () => {
    it("renders the recipe type details", async () => {
        const expectedRecipeType = generateRecipeType()
        findRecipeTypeMock.mockResolvedValueOnce(expectedRecipeType)
        render(<RecipeTypeDetails id={99} />)

        await waitFor(() => {
            expect(screen.getByText(/loading.../i)).toBeInTheDocument()
            expect(findRecipeTypeMock).toHaveBeenCalled()
        })

        expect(screen.getByText("Id:")).toBeInTheDocument()
        expect(screen.getByText("Name:")).toBeInTheDocument()
        expect(screen.getByText(expectedRecipeType.id.toString())).toBeInTheDocument()
        expect(screen.getByText(expectedRecipeType.name)).toBeInTheDocument()
    })

    it("renders an error if the recipe type cannot be obtained", async () => {
        findRecipeTypeMock.mockRejectedValueOnce({ message: "Failure" })
        render(<RecipeTypeDetails id={99} />)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await waitFor(() => expect(findRecipeTypeMock).toHaveBeenCalled())

        expect(screen.getByText(/failure/i)).toBeInTheDocument()
    })
})