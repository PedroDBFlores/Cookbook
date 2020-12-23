import React from "react"
import {render, screen, waitFor} from "@testing-library/react"
import RecipeSearchForm from "./search-form"
import userEvent from "@testing-library/user-event"

describe("Recipe search form", () => {
    it("renders the layout", () => {
        render(<RecipeSearchForm onSearch={jest.fn()} recipeTypes={[]}/>)

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
    ])("searches when %s", async (_, {name, description, recipeTypeId}) => {
        const onSearchMock = jest.fn()
        render(<RecipeSearchForm onSearch={onSearchMock} recipeTypes={[
            {id: 1, name: "A lovely"},
            {id: 2, name: "new recipe type"}
        ]}/>)

        userEvent.paste(screen.getByLabelText(/^name$/i), name)
        userEvent.paste(screen.getByLabelText(/^description$/i), description)
        userEvent.selectOptions(screen.getByLabelText(/^recipe type$/i), recipeTypeId)

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