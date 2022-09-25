import React from "react"
import { render, screen, waitFor } from "@testing-library/react"
import RecipeSearchForm from "./search-form"
import userEvent from "@testing-library/user-event"

describe("Recipe search form", () => {
    it("renders the layout", () => {
        render(<RecipeSearchForm onSearch={jest.fn()} recipeTypes={[]}/>)

        expect(screen.getByLabelText(/^translated recipe-feature.search.recipe-name-parameter$/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/^translated name$/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/^translated recipe-feature.search.recipe-description-parameter$/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/^translated description/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/^translated recipe-feature.recipe-type-parameter$/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/^translated recipe-type-feature.singular/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/^translated common.search$/i)).toHaveAttribute("type", "submit")
    })

    it("allows a search to be performed with no parameters provided", async() => {
        const onSearchMock = jest.fn()

        render(<RecipeSearchForm onSearch={onSearchMock} recipeTypes={[]}/>)

        await userEvent.click(screen.getByLabelText(/^translated common.search$/i))

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
            recipeTypeId: ""
        }],
        ["description is filled", {
            name: "",
            description: "a description",
            recipeTypeId: ""
        }],
        ["recipe type id is changed", {
            name: "",
            description: "",
            recipeTypeId: "2"
        }]
    ])("searches when %s", async(_, { name, description, recipeTypeId }) => {
        const onSearchMock = jest.fn()

        render(<RecipeSearchForm onSearch={onSearchMock} recipeTypes={[
            { id: 1, name: "A lovely" },
            { id: 2, name: "new recipe type" }
        ]}/>)


        await userEvent.paste(screen.getByLabelText(/^translated name$/i), name)
        await userEvent.paste(screen.getByLabelText(/^translated description$/i), description)
        await userEvent.selectOptions(screen.getByLabelText(/^translated recipe-type-feature.singular$/i), recipeTypeId)

        await userEvent.click(screen.getByLabelText(/^translated common.search$/i))

        await waitFor(() => {
            expect(onSearchMock).toHaveBeenCalledWith({
                name,
                description,
                recipeTypeId
            })
        })
    })
})
