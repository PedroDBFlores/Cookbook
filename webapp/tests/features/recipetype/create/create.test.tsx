import React from "react"
import {render, screen, fireEvent, waitFor} from "@testing-library/react"
import CreateRecipeType from "../../../../src/features/recipetype/create/create"
import {RecipeTypeService} from "../../../../src/services/recipe-type-service"
import {renderWithRoutes} from "../../../render"

const createRecipeTypeMock = jest.fn()
const recipeTypeServiceMock = {
    getAll: jest.fn(),
    delete:  jest.fn(),
    update: jest.fn(),
    create :createRecipeTypeMock,
    find:  jest.fn()
} as RecipeTypeService

describe("Create recipe type", () => {
    it("renders the initial form", () => {
        render(<CreateRecipeType recipeTypeService={recipeTypeServiceMock}/>)

        expect(screen.getByText(/create a new recipe type/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
        const submitButton = screen.getByLabelText(/create recipe type/i)
        expect(submitButton).toHaveAttribute("type", "submit")
    })

    it("displays an error when the name is empty on submitting", async () => {
        render(<CreateRecipeType recipeTypeService={recipeTypeServiceMock}/>)

        const submitButton = screen.getByLabelText(/create recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() =>
            expect(screen.getByText(/name is required/i)).toBeInTheDocument()
        )
    })

    it("create the recipe type in the Cookbook API and navigates to the details", async () => {
        createRecipeTypeMock.mockResolvedValueOnce({id: 1})

        renderWithRoutes({
            "/recipetype/new": () => <CreateRecipeType recipeTypeService={recipeTypeServiceMock}/>,
            "/recipetype/1": () => <div>I'm the recipe type details page for id 1</div>
        }, "/recipetype/new")

        const nameInput = screen.getByLabelText(/name/i)
        fireEvent.change(nameInput, {target: {value: "Fish"}})
        const submitButton = screen.getByLabelText(/create recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() => {
            expect(createRecipeTypeMock).toHaveBeenCalledWith({name: "Fish"})
            expect(screen.getByText(/i'm the recipe type details page for id 1/i)).toBeInTheDocument()
        })
    })
})
