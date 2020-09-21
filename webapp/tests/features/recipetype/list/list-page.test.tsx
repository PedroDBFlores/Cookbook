import React, {useEffect} from "react"
import {render, screen, waitFor, fireEvent} from "@testing-library/react"
import RecipeTypeListPage from "../../../../src/features/recipetype/list/list-page"
import RecipeTypeList from "../../../../src/features/recipetype/list/list"
import {generateRecipeType} from "../../../helpers/generators/dto-generators"
import {renderWithRoutes} from "../../../render"

const getAllRecipeTypesMock = jest.fn()
const deleteRecipeTypeMock = jest.fn()

jest.mock("../../../../src/features/recipetype/list/list", () => {
    return {
        __esModule: true,
        default: jest.fn(() => <>Mock Recipe Type list</>)
    }
})
const recipeTypeListMock = RecipeTypeList as jest.MockedFunction<typeof RecipeTypeList>

describe("Recipe type list page", () => {
    beforeEach(() => {
        jest.clearAllMocks()
    })

    it("has the required content and gets the recipe types", async () => {
        getAllRecipeTypesMock.mockResolvedValueOnce([])
        render(<RecipeTypeListPage getAllRecipeTypes={getAllRecipeTypesMock}
         onDelete={deleteRecipeTypeMock}/>)

        expect(screen.getByText(/recipe types/i)).toBeInTheDocument()
        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        await waitFor(() => {
            expect(screen.getByLabelText("Create new recipe type")).toBeInTheDocument()
            expect(screen.getByText(/^mock recipe type list$/i)).toBeInTheDocument()
        })
    })

    it("shows the error", async () => {
        getAllRecipeTypesMock.mockRejectedValueOnce({code: "YELLOW", message: "Database error"})
        render(<RecipeTypeListPage getAllRecipeTypes={getAllRecipeTypesMock}
                                   onDelete={deleteRecipeTypeMock}/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        await screen.findByText(/error: database error/i)
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
    })

    it("passed the onDelete function to the 'RecipeTypeList' component", async () => {
        const expectedRecipeType = generateRecipeType()
        getAllRecipeTypesMock.mockResolvedValueOnce([expectedRecipeType])
        deleteRecipeTypeMock.mockResolvedValueOnce(void (0))
        recipeTypeListMock.mockImplementationOnce(({recipeTypes, onDelete}) => {
            useEffect(() => {
                onDelete(recipeTypes[0].id)
            }, [])
            return <></>
        })
        render(<RecipeTypeListPage getAllRecipeTypes={getAllRecipeTypesMock}
                                   onDelete={deleteRecipeTypeMock}/>)

        expect(screen.getByText(/loading.../i)).toBeInTheDocument()
        expect(getAllRecipeTypesMock).toHaveBeenCalled()
        await waitFor(() => {
            expect(deleteRecipeTypeMock).toHaveBeenCalledWith(expectedRecipeType.id)
        })
    })

    it("navigates to the recipe type create page on click", async () => {
        getAllRecipeTypesMock.mockResolvedValueOnce([])
        renderWithRoutes({
            "/recipetype": () => <RecipeTypeListPage getAllRecipeTypes={getAllRecipeTypesMock}
                                                     onDelete={deleteRecipeTypeMock}/>,
            "/recipetype/new": () => <>I'm the recipe type create page</>
        }, "/recipetype")
        await waitFor(() => expect(screen.getByLabelText(/create new recipe type/i)).toBeInTheDocument())

        const createButton = screen.getByLabelText(/create new recipe type/i)
        fireEvent.click(createButton)

        await waitFor(() => expect(screen.getByText(/I'm the recipe type create page/i)).toBeInTheDocument())
    })
})
