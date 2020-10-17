import React from "react"
import {render, screen, waitFor} from "@testing-library/react"
import RecipeSearchList from "./search-list"
import {generateRecipeDetails} from "../../../../tests/helpers/generators/dto-generators"
import {renderWithRoutes} from "../../../../tests/render"
import userEvent from "@testing-library/user-event"

describe("Recipe search list component", () => {
    beforeEach(() => {
        jest.clearAllMocks()
    })

    describe("Render", () => {
        it("shows 'No matching recipes' when there are no recipes", () => {
            render(<RecipeSearchList searchResult={{
                count: 0,
                numberOfPages: 1,
                results: []
            }} onDelete={jest.fn()} onNumberOfRowsChange={jest.fn()} onPageChange={jest.fn()}/>)

            expect(screen.getByText(/No matching recipes/)).toBeInTheDocument()
        })

        it("renders a table with the required headers", () => {
            render(<RecipeSearchList searchResult={{
                count: 1,
                numberOfPages: 1,
                results: [generateRecipeDetails()]
            }} onDelete={jest.fn()} onNumberOfRowsChange={jest.fn()} onPageChange={jest.fn()}/>)

            expect(screen.getByText(/^id$/i)).toBeInTheDocument()
            expect(screen.getByText(/^name$/i)).toBeInTheDocument()
            expect(screen.getByText(/^recipe type$/i)).toBeInTheDocument()
            expect(screen.getByText(/^user$/i)).toBeInTheDocument()
            expect(screen.getByText(/^actions$/i)).toBeInTheDocument()
        })

        it("shows the provided data", () => {
            const expectedRecipes = [
                generateRecipeDetails(),
                generateRecipeDetails()
            ]

            render(<RecipeSearchList searchResult={{
                count: expectedRecipes.length,
                numberOfPages: 1,
                results: expectedRecipes
            }} onDelete={jest.fn()} onNumberOfRowsChange={jest.fn()} onPageChange={jest.fn()}/>)

            expectedRecipes.forEach(recipe => {
                expect(screen.getByText(recipe.id.toString())).toBeInTheDocument()
                expect(screen.getByText(recipe.name)).toBeInTheDocument()
                expect(screen.getByText(recipe.recipeTypeName)).toBeInTheDocument()
                expect(screen.getByText(recipe.userName)).toBeInTheDocument()
            })
        })
    })

    describe("Pagination", () => {
        const expectedRecipes = [
            generateRecipeDetails(),
            generateRecipeDetails()
        ]

        const basicSearchResult = {
            count: expectedRecipes.length,
            numberOfPages: 1,
            results: expectedRecipes
        }

        test.each([
            ["has results", basicSearchResult],
            ["doesn't have results", {...basicSearchResult, count: 0, results: []}]
        ])("it has the pagination element when it %s", (_, searchResult) => {
            render(<RecipeSearchList searchResult={searchResult} onNumberOfRowsChange={jest.fn()} onDelete={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            expect(screen.getByLabelText(/rows per page/i)).toBeInTheDocument()
            expect(screen.getByLabelText(/first page/i)).toBeInTheDocument()
        })

        test.each([
            ["1-2 of 2", basicSearchResult],
            ["1-10 of 500", {...basicSearchResult, count: 500, numberOfPages: 50}]
        ])("has the correct number of elements (%s)", (expectedElements, searchResult) => {
            render(<RecipeSearchList searchResult={searchResult} onDelete={jest.fn()} onNumberOfRowsChange={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            expect(screen.getByText(expectedElements)).toBeInTheDocument()
        })

        describe("on page change", () => {
            const onPageChangeMock = jest.fn()
            const searchResult = {
                count: 100,
                numberOfPages: 10,
                results: []
            }

            beforeEach(() => {
                onPageChangeMock.mockClear()
            })

            test.each([
                ["next page", ["next page"], 1],
                ["first page", ["next page", "first page"], 0],
                ["previous page", ["next page", "previous page"], 0],
                ["last page", ["last page"], 9]
            ])("calls the 'onPageChange' when the '%s' button is pressed", (_,
                                                                            buttonLabels, expectedPageNumber) => {
                render(<RecipeSearchList
                    searchResult={searchResult}
                    onDelete={jest.fn()}
                    onNumberOfRowsChange={jest.fn()}
                    onPageChange={onPageChangeMock}/>)

                buttonLabels.forEach(label => userEvent.click(screen.getByLabelText(label)))

                expect(onPageChangeMock).toHaveBeenLastCalledWith(expectedPageNumber)
            })
        })
    })

    test.each([
        [10], [20], [50]
    ])("changes the number of rows to %s", async (numberOfItems) => {
        const recipes = [generateRecipeDetails(), generateRecipeDetails()]
        const searchResult = {
            count: recipes.length,
            numberOfPages: 1,
            results: recipes
        }
        const onNumberOfRowsChange = jest.fn()
        render(<RecipeSearchList searchResult={searchResult} onDelete={jest.fn()} onNumberOfRowsChange={onNumberOfRowsChange}
                                 onPageChange={jest.fn()}/>)


        userEvent.selectOptions(screen.getByLabelText(/^rows per page$/i), numberOfItems.toString())

        expect(onNumberOfRowsChange).toHaveBeenCalledWith(numberOfItems)
    })

    describe("Actions", () => {
        const recipes = [
            generateRecipeDetails(),
            generateRecipeDetails()
        ]

        const searchResult = {
            count: recipes.length,
            numberOfPages: 1,
            results: recipes
        }

        it("has a set of actions for the recipe type", async () => {
            render(<RecipeSearchList searchResult={searchResult} onDelete={jest.fn()} onNumberOfRowsChange={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            const detailButton = screen.getByLabelText(`Recipe details for id ${recipes[0].id}`, {
                selector: "button"
            })
            const editButton = screen.getByLabelText(`Edit Recipe with id ${recipes[0].id}`, {
                selector: "button"
            })
            const deleteButton = screen.getByLabelText(`Delete Recipe with id ${recipes[0].id}`, {
                selector: "button"
            })

            expect(detailButton).toBeInTheDocument()
            expect(editButton).toBeInTheDocument()
            expect(deleteButton).toBeInTheDocument()
        })


        it("navigates to the recipe details", async () => {
            const firstRecipe = recipes[0]
            renderWithRoutes({
                "/recipe": () => <RecipeSearchList searchResult={searchResult} onDelete={jest.fn()}
                                                   onNumberOfRowsChange={jest.fn()} onPageChange={jest.fn()}/>,
                [`/recipe/${firstRecipe.id}/details`]: () => <div>I'm the recipe details page</div>
            }, "/recipe")

            userEvent.click(screen.getByLabelText(`Recipe details for id ${firstRecipe.id}`, {
                selector: "button"
            }))

            expect(await screen.findByText(/i'm the recipe details page/i)).toBeInTheDocument()
        })

        it("navigates to the recipe edit page", async () => {
            const firstRecipe = recipes[0]
            renderWithRoutes({
                "/recipe": () => <RecipeSearchList searchResult={searchResult} onDelete={jest.fn()}
                                                   onNumberOfRowsChange={jest.fn()} onPageChange={jest.fn()}/>,
                [`/recipe/${firstRecipe.id}/edit`]: () => <div>I'm the recipe edit page</div>
            }, "/recipe")

            userEvent.click(screen.getByLabelText(`Edit Recipe with id ${firstRecipe.id}`, {
                selector: "button"
            }))

            expect(await screen.findByText(/i'm the recipe edit page/i)).toBeInTheDocument()
        })

        it("deletes a recipe", async () => {
            const onDeleteMock = jest.fn()
            const firstRecipe = recipes[0]
            render(<RecipeSearchList searchResult={searchResult} onDelete={onDeleteMock} onNumberOfRowsChange={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            userEvent.click(screen.getByLabelText(`Delete Recipe with id ${firstRecipe.id}`, {
                selector: "button"
            }))

            await waitFor(() => {
                expect(onDeleteMock).toHaveBeenCalledWith(firstRecipe.id)
            })
        })
    })

})