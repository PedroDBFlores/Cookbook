import React from "react"
import {render, screen, waitFor, within} from "@testing-library/react"
import RecipeSearchForm from "../../../../src/features/recipe/search/search-form"
import userEvent from "@testing-library/user-event"

describe("Recipe search form", () => {
    it("renders the layout", () => {
        render(<RecipeSearchForm onSearch={jest.fn()} recipeTypes={[]}/>)

        expect(screen.getByText(/parameters/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe name parameter/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe description parameter/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe type parameter/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/search recipe with parameters/i)).toHaveAttribute("type", "submit")
    })

    it("allows a search to be performed with no parameters provided", async () => {
        const onSearchMock = jest.fn()
        render(<RecipeSearchForm onSearch={onSearchMock} recipeTypes={[]}/>)

        userEvent.click(screen.getByLabelText(/search recipe with parameters/i))

        await waitFor(() => {
            expect(onSearchMock).toHaveBeenCalledWith({
                name: "",
                description: "",
                recipeTypeId: 0
            })
        })
    })

    it.each([
        ["name is filled", {
            name: "a name",
            description: "",
            recipeTypeId: 0
        }, undefined],
        ["description is filled", {
            name: "",
            description: "a description",
            recipeTypeId: 0
        }, undefined],
        ["recipe type id is changed", {
            name: "",
            description: "",
            recipeTypeId: 2
        }, "new recipe type"]
    ])("searches when %s", async (_, {name, description, recipeTypeId}, recipeTypeText) => {
        const onSearchMock = jest.fn()
        render(<RecipeSearchForm onSearch={onSearchMock} recipeTypes={[
            {id: 1, name: "A lovely"},
            {id: 2, name: "new recipe type"}
        ]}/>)

        await userEvent.type(screen.getByLabelText(/^name$/i), name)
        await userEvent.type(screen.getByLabelText(/^description$/i), description)

        if (recipeTypeText) {
            // @ts-ignore
            userEvent.click(within(screen.getByText("Recipe type").closest("div")).getByRole("button"))
            userEvent.click(screen.getByText(recipeTypeText))
        }

        userEvent.click(screen.getByLabelText(/search recipe with parameters/i))

        await waitFor(() => {
            expect(onSearchMock).toHaveBeenCalledWith({
                name,
                description,
                recipeTypeId
            })
        })
    })
})