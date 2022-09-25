import React from "react"
import {render, screen, waitFor} from "@testing-library/react"
import RecipeSearchList from "./search-list"
import {WrapperWithRoutes} from "../../../../tests/render-helpers"
import userEvent from "@testing-library/user-event"
import {RecipeDetails} from "services/recipe-service"
import TablePagination from "../../../components/table-pagination/table-pagination"

jest.mock("../../../components/table-pagination/table-pagination", () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(() => <>Mock table pagination</>)
    }
})
const TablePaginationMock = TablePagination as jest.MockedFunction<typeof TablePagination>

describe("Recipe search list component", () => {

    beforeEach(jest.clearAllMocks)

    const expectedRecipes: Array<RecipeDetails> = [
        {
            id: 123,
            recipeTypeId: 4,
            name: "Roasted sweet potato",
            description: "Roasted sweet potato",
            recipeTypeName: "Dessert",
            ingredients: "Potato",
            preparingSteps: "Take potato to oven"
        },
        {
            id: 144,
            recipeTypeId: 2,
            name: "Roasted codfish",
            description: "Roasted codfish",
            recipeTypeName: "Fish",
            ingredients: "Codfish, Potato, Olive Oil, Onion, Paprika, Garlic, Salt",
            preparingSteps: "All into baking tray, put in oven at 200ºC for 45-60 minutes until potatoes are golden"
        }
    ]

    const searchResult = {
        count: expectedRecipes.length,
        numberOfPages: 1,
        results: expectedRecipes
    }

    describe("Render", () => {
        it("shows 'No matching recipes' when there are no recipes", () => {
            render(<RecipeSearchList searchResult={{
                count: 0,
                numberOfPages: 1,
                results: []
            }}
                                     onDelete={jest.fn()}
                                     onChangeRowsPerPage={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            expect(screen.getByText(/^translated recipe-feature.search.no-matching-recipes$/)).toBeInTheDocument()
        })

        it("renders a table with the required headers", () => {
            render(<RecipeSearchList searchResult={{
                count: 1,
                numberOfPages: 1,
                results: expectedRecipes
            }}
                                     onDelete={jest.fn()}
                                     onChangeRowsPerPage={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            expect(screen.getByText(/^translated id$/i)).toBeInTheDocument()
            expect(screen.getByText(/^translated name$/i)).toBeInTheDocument()
            expect(screen.getByText(/^translated recipe-type-feature.singular$/i)).toBeInTheDocument()
            expect(screen.getByText(/^translated actions$/i)).toBeInTheDocument()
        })

        it("shows the provided data", () => {
            render(<RecipeSearchList searchResult={{
                count: expectedRecipes.length,
                numberOfPages: 1,
                results: expectedRecipes
            }}
                                     onDelete={jest.fn()}
                                     onChangeRowsPerPage={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            expectedRecipes.forEach(recipe => {
                expect(screen.getByText(recipe.id.toString())).toBeInTheDocument()
                expect(screen.getByText(recipe.name)).toBeInTheDocument()
                expect(screen.getByText(recipe.recipeTypeName)).toBeInTheDocument()
            })
        })
    })

    describe("Pagination", () => {
        it("calls the 'onPageChange' when the page changing buttons are pressed", () => {
            const onPageChangeMock = jest.fn()
            const searchResult = {
                count: 100,
                numberOfPages: 10,
                results: []
            }
            TablePaginationMock.mockImplementation(({onChangePage}) => {
                return <>
                    <button aria-label="next page"
                            onClick={() => onChangePage(2)}>next page
                    </button>
                    <button aria-label="previous page"
                            onClick={() => onChangePage(1)}>previous page
                    </button>
                </>
            })

            render(<RecipeSearchList
                searchResult={searchResult}
                onDelete={jest.fn()}
                onChangeRowsPerPage={jest.fn()}
                onPageChange={onPageChangeMock}/>)

            await userEvent.click(screen.getByLabelText(/next page/i))
            expect(onPageChangeMock).toHaveBeenCalledWith(2)

            await userEvent.click(screen.getByLabelText(/previous page/i))
            expect(onPageChangeMock).toHaveBeenCalledWith(1)
        })

        test.each([
            [10], [20], [50]
        ])("changes the number of rows to %s", async numberOfItems => {
            const onNumberOfRowsChange = jest.fn()

            TablePaginationMock.mockImplementation(({onChangeRowsPerPage}) => {
                return <>
                    <button aria-label="rows per page"
                            onClick={() => onChangeRowsPerPage(numberOfItems)}>next page
                    </button>
                </>
            })

            render(<RecipeSearchList searchResult={searchResult} onDelete={jest.fn()}
                                     onChangeRowsPerPage={onNumberOfRowsChange}
                                     onPageChange={jest.fn()}/>)


            await userEvent.click(screen.getByLabelText(/^rows per page$/i))

            expect(onNumberOfRowsChange).toHaveBeenCalledWith(numberOfItems)
        })
    })

    describe("Actions", () => {

        it("navigates to the recipe details", async () => {
            const firstRecipe = expectedRecipes[0]

            render(<WrapperWithRoutes initialPath="/recipe" routeConfiguration={[
                {
                    path: "/recipe",
                    exact: true,
                    component: () => <RecipeSearchList searchResult={searchResult} onDelete={jest.fn()}
                                                       onChangeRowsPerPage={jest.fn()} onPageChange={jest.fn()}/>
                },
                {
                    path: `/recipe/${firstRecipe.id}/details`,
                    exact: true,
                    component: () => <div>I'm the recipe details page</div>
                }
            ]}/>)

            await userEvent.click(screen.getAllByLabelText(/^translated recipe-feature.details-label$/i, {
                selector: "button"
            })[0])

            expect(await screen.findByText(/i'm the recipe details page/i)).toBeInTheDocument()
        })

        it("navigates to the recipe edit page", async () => {
            const firstRecipe = expectedRecipes[0]

            render(<WrapperWithRoutes initialPath="/recipe" routeConfiguration={[
                {
                    path: "/recipe",
                    exact: true,
                    component: () => <RecipeSearchList searchResult={searchResult} onDelete={jest.fn()}
                                                       onChangeRowsPerPage={jest.fn()} onPageChange={jest.fn()}/>
                },
                {
                    path: `/recipe/${firstRecipe.id}/edit`,
                    exact: true,
                    component: () => <div>I'm the recipe edit page</div>
                }
            ]}/>)

            await userEvent.click(screen.getAllByLabelText(/^translated recipe-feature.edit-label$/i, {
                selector: "button"
            })[0])

            expect(await screen.findByText(/i'm the recipe edit page/i)).toBeInTheDocument()
        })

        it("deletes a recipe", async () => {
            const onDeleteMock = jest.fn()
            const firstRecipe = expectedRecipes[0]

            render(<RecipeSearchList searchResult={searchResult} onDelete={onDeleteMock}
                                     onChangeRowsPerPage={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            await userEvent.click(screen.getAllByLabelText(/^translated recipe-feature.delete-label$/i, {
                selector: "button"
            })[0])

            await waitFor(() => {
                expect(onDeleteMock).toHaveBeenCalledWith(firstRecipe.id, firstRecipe.name)
            })
        })
    })

})
