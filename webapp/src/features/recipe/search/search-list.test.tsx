import React from "react"
import {render, screen, waitFor} from "@testing-library/react"
import RecipeSearchList from "./search-list"
import {generateRecipeDetails} from "../../../../tests/helpers/generators/dto-generators"
import {WrapperWithRoutes} from "../../../../tests/render-helpers"
import userEvent from "@testing-library/user-event"
import {RecipeDetails} from "services/recipe-service"

describe("Recipe search list component", () => {
    const expectedRecipes: Array<RecipeDetails> = [
        generateRecipeDetails(),
        generateRecipeDetails()
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
            }} onDelete={jest.fn()} onChangeRowsPerPage={jest.fn()} onPageChange={jest.fn()}/>)

            expect(screen.getByText(/No matching recipes/)).toBeInTheDocument()
        })

        it("renders a table with the required headers", () => {
            render(<RecipeSearchList searchResult={{
                count: 1,
                numberOfPages: 1,
                results: expectedRecipes
            }} onDelete={jest.fn()} onChangeRowsPerPage={jest.fn()} onPageChange={jest.fn()}/>)

            expect(screen.getByText(/^id$/i)).toBeInTheDocument()
            expect(screen.getByText(/^name$/i)).toBeInTheDocument()
            expect(screen.getByText(/^recipe type$/i)).toBeInTheDocument()
            expect(screen.getByText(/^actions$/i)).toBeInTheDocument()
        })

        it("shows the provided data", () => {
            render(<RecipeSearchList searchResult={{
                count: expectedRecipes.length,
                numberOfPages: 1,
                results: expectedRecipes
            }} onDelete={jest.fn()} onChangeRowsPerPage={jest.fn()} onPageChange={jest.fn()}/>)

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
            render(<RecipeSearchList
                searchResult={searchResult}
                onDelete={jest.fn()}
                onChangeRowsPerPage={jest.fn()}
                onPageChange={onPageChangeMock}/>)

            userEvent.click(screen.getByLabelText(/next page/i))
            expect(onPageChangeMock).toHaveBeenCalledWith(2)

            userEvent.click(screen.getByLabelText(/previous page/i))
            expect(onPageChangeMock).toHaveBeenCalledWith(1)
        })

        test.each([
            [10], [20], [50]
        ])("changes the number of rows to %s", async (numberOfItems) => {
            const onNumberOfRowsChange = jest.fn()
            render(<RecipeSearchList searchResult={searchResult} onDelete={jest.fn()}
                                     onChangeRowsPerPage={onNumberOfRowsChange}
                                     onPageChange={jest.fn()}/>)


            userEvent.selectOptions(screen.getByLabelText(/^rows per page$/i), numberOfItems.toString())

            expect(onNumberOfRowsChange).toHaveBeenCalledWith(numberOfItems)
        })
    })

    describe("Actions", () => {
        it("has a set of actions for the recipe type", async () => {
            render(<RecipeSearchList searchResult={searchResult} onDelete={jest.fn()} onChangeRowsPerPage={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            const detailButton = screen.getByLabelText(`Recipe details for id ${expectedRecipes[0].id}`, {
                selector: "button"
            })
            const editButton = screen.getByLabelText(`Edit Recipe with id ${expectedRecipes[0].id}`, {
                selector: "button"
            })
            const deleteButton = screen.getByLabelText(`Delete Recipe with id ${expectedRecipes[0].id}`, {
                selector: "button"
            })

            expect(detailButton).toBeInTheDocument()
            expect(editButton).toBeInTheDocument()
            expect(deleteButton).toBeInTheDocument()
        })


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

            userEvent.click(screen.getByLabelText(`Recipe details for id ${firstRecipe.id}`, {
                selector: "button"
            }))

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

            userEvent.click(screen.getByLabelText(`Edit Recipe with id ${firstRecipe.id}`, {
                selector: "button"
            }))

            expect(await screen.findByText(/i'm the recipe edit page/i)).toBeInTheDocument()
        })

        it("deletes a recipe", async () => {
            const onDeleteMock = jest.fn()
            const firstRecipe = expectedRecipes[0]
            render(<RecipeSearchList searchResult={searchResult} onDelete={onDeleteMock}
                                     onChangeRowsPerPage={jest.fn()}
                                     onPageChange={jest.fn()}/>)

            userEvent.click(screen.getByLabelText(`Delete Recipe with id ${firstRecipe.id}`, {
                selector: "button"
            }))

            await waitFor(() => {
                expect(onDeleteMock).toHaveBeenCalledWith(firstRecipe.id, firstRecipe.name)
            })
        })
    })

})