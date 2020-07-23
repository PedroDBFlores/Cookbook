import React, {useEffect} from "react"
import {render, screen, waitFor} from "@testing-library/react"
import RecipeTypeListPage from "../../../../src/features/recipetype/list/list-page"
import {deleteRecipeType, getAllRecipeTypes} from "../../../../src/services/recipe-type-service"
import RecipeTypeList from "../../../../src/features/recipetype/list/list"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"

jest.mock("../../../../src/services/recipe-type-service")
const getAllRecipeTypesMock = getAllRecipeTypes as jest.MockedFunction<typeof getAllRecipeTypes>
const deleteRecipeTypeMock = deleteRecipeType as jest.MockedFunction<typeof deleteRecipeType>

jest.mock("../../../../src/features/recipetype/list/list", () => {
    return {
        __esModule: true,
        default: jest.fn(() => <>Mock Recipe Type list</>)
    }
})
const recipeTypeListMock = RecipeTypeList as jest.MockedFunction<typeof RecipeTypeList>

describe("Recipe type list page", () => {
    it("has the required content and gets the recipe types", async () => {
        getAllRecipeTypesMock.mockResolvedValueOnce([])
        render(<RecipeTypeListPage/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        await waitFor(() => {
            screen.getByText(/^mock recipe type list$/i)
        })
    })

    it("shows the error", async () => {
        getAllRecipeTypesMock.mockRejectedValueOnce({code: "YELLOW", message: "Database error"})
        render(<RecipeTypeListPage/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await screen.findByText(/error: database error/i)
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
    })

    it("passed the onDelete function to the 'RecipeTypeList' component ", async () => {
        const expectedRecipeType = generateRecipeType()
        getAllRecipeTypesMock.mockResolvedValueOnce([expectedRecipeType])
        deleteRecipeTypeMock.mockResolvedValueOnce(void (0))
        recipeTypeListMock.mockImplementationOnce(({recipeTypes, onDelete}) => {
            useEffect(() => {
                onDelete(recipeTypes[0].id)
            }, [])
            return <></>
        })
        render(<RecipeTypeListPage/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        await waitFor(() => {
            expect(deleteRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
        })
    })
})
