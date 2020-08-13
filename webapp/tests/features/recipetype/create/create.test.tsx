import React from "React"
import {render, screen, fireEvent, waitFor} from "@testing-library/react"
import CreateRecipeType from "../../../../src/features/recipetype/create/create"
import {createRecipeType} from "../../../../src/services/recipe-type-service"
import {renderWithRoutes} from "../../../render"

jest.mock("../../../../src/services/recipe-type-service")
const createRecipeTypeMock = createRecipeType as jest.MockedFunction<typeof createRecipeType>

describe("Create recipe type", () => {
    it("renders the initial form", () => {
        render(<CreateRecipeType/>)

        expect(screen.getByText(/create a new recipe type/i)).toBeInTheDocument()
        expect(screen.getByText(/name/i)).toBeInTheDocument()
        expect(screen.getByLabelText(/name/i)).toBeInTheDocument()
        const submitButton = screen.getByLabelText(/create recipe type/i)
        expect(submitButton).toHaveAttribute("type", "submit")
    })

    it("displays an error when the name is empty on submitting", async () => {
        render(<CreateRecipeType/>)

        const submitButton = screen.getByLabelText(/create recipe type/i)
        fireEvent.submit(submitButton)

        await waitFor(() =>
            expect(screen.getByText(/name is required/i)).toBeInTheDocument()
        )
    })

    it("calls the create recipe type endpoint and navigates to it's details", async () => {
        createRecipeTypeMock.mockResolvedValueOnce({id: 1})

        renderWithRoutes({
            "/recipetype/new": () => <CreateRecipeType/>,
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
