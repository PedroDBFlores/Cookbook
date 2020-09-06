import React from "react"
import {fireEvent, render, screen, waitFor} from "@testing-library/react"
import RecipeSearchList from "../../../../src/features/recipe/search/search-list"
import {generateRecipeDetails} from "../../../helpers/generators/dto-generators"
import {renderWithRoutes} from "../../../render"
import userEvent from "@testing-library/user-event"

describe("Recipe search list component", () => {
    describe("Render", () => {
        it("shows 'No matching recipes' when there are no recipes", () => {
            render(<RecipeSearchList recipes={[]} onDelete={jest.fn()}/>)

            expect(screen.getByText(/No matching recipes/)).toBeInTheDocument()
        })

        it("renders a table with the required headers", () => {
            render(<RecipeSearchList recipes={[generateRecipeDetails()]} onDelete={jest.fn()}/>)

            expect(screen.getByText(/id/i)).toBeInTheDocument()
            expect(screen.getByText(/name/i)).toBeInTheDocument()
            expect(screen.getByText(/recipe type/i)).toBeInTheDocument()
            expect(screen.getByText(/user/i)).toBeInTheDocument()
            expect(screen.getByText(/actions/i)).toBeInTheDocument()
        })

        it("shows the provided data", () => {
            const expectedRecipes = [
                generateRecipeDetails(),
                generateRecipeDetails()
            ]

            render(<RecipeSearchList recipes={expectedRecipes} onDelete={jest.fn()}/>)

            expectedRecipes.forEach(recipe => {
                expect(screen.getByText(recipe.id.toString())).toBeInTheDocument()
                expect(screen.getByText(recipe.name)).toBeInTheDocument()
                expect(screen.getByText(recipe.recipeTypeName)).toBeInTheDocument()
                expect(screen.getByText(recipe.userName)).toBeInTheDocument()
            })
        })
    })


    describe("Actions", () => {
        const recipes = [
            generateRecipeDetails(),
            generateRecipeDetails()
        ]


        it("has a set of actions for the recipe type", async () => {
            render(<RecipeSearchList recipes={recipes} onDelete={jest.fn()}/>)

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
                "/recipe": () => <RecipeSearchList recipes={recipes} onDelete={jest.fn()}/>,
                [`/recipe/${firstRecipe.id}`]: () => <div>I'm the recipe details page</div>
            }, "/recipe")
            const detailsButton = screen.getByLabelText(`Recipe details for id ${firstRecipe.id}`, {
                selector: "button"
            })

            fireEvent.click(detailsButton)

            expect(await screen.findByText(/i'm the recipe details page/i)).toBeInTheDocument()
        })

        it("navigates to the recipe edit page", async () => {
            const firstRecipe = recipes[0]
            renderWithRoutes({
                "/recipe": () => <RecipeSearchList recipes={recipes} onDelete={jest.fn()}/>,
                [`/recipe/${firstRecipe.id}/edit`]: () => <div>I'm the recipe edit page</div>
            }, "/recipe")
            const editButton = screen.getByLabelText(`Edit Recipe with id ${firstRecipe.id}`, {
                selector: "button"
            })

            fireEvent.click(editButton)

            expect(await screen.findByText(/i'm the recipe edit page/i)).toBeInTheDocument()
        })

        it("deletes a recipe", async () => {
            const onDeleteMock = jest.fn()
            const firstRecipe = recipes[0]
            render(<RecipeSearchList recipes={recipes} onDelete={onDeleteMock}/>)
            const deleteButton = screen.getByLabelText(`Delete Recipe with id ${firstRecipe.id}`, {
                selector: "button"
            })

            userEvent.click(deleteButton)

            await waitFor(() => {
                expect(onDeleteMock).toHaveBeenCalledWith(firstRecipe.id)
            })
        })
    })

})