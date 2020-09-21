import React from "react"
import {render, screen, fireEvent, waitFor, within} from "@testing-library/react"
import RecipeSearchForm from "../../../../src/features/recipe/search/search-form"

describe("Recipe search form", () => {
    it("renders the layout", () => {
        render(<RecipeSearchForm onSearch={jest.fn()} recipeTypes={[]}/>)

        expect(screen.getByText(/parameters/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe name parameter/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe description parameter/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/recipe type parameter/i)).toBeInTheDocument()
        const submitButton = screen.getByLabelText(/search recipe with parameters/i)
        expect(submitButton).toHaveAttribute("type", "submit")
    })

    it("allows a search to be performed with no parameters provided", async () => {
        const onSearchMock = jest.fn()
        render(<RecipeSearchForm onSearch={onSearchMock} recipeTypes={[]}/>)

        fireEvent.submit(screen.getByLabelText(/search recipe with parameters/i))

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

        fireEvent.change(screen.getByLabelText("Name"),
            {target: {value: name}})
        fireEvent.change(screen.getByLabelText("Description"),
            {target: {value: description}})

        if(recipeTypeText) {
            const selectElement = screen.getByText("Recipe type")
                .closest("div")

            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-ignore
            fireEvent.mouseDown(within(selectElement).getByRole("button"))
            fireEvent.click(screen.getByText(recipeTypeText))
        }

        fireEvent.submit(screen.getByLabelText(/search recipe with parameters/i))

        await waitFor(() => {
            expect(onSearchMock).toHaveBeenCalledWith({
                name,
                description,
                recipeTypeId
            })
        })
    })
})