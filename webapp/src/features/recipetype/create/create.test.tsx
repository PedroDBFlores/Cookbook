import React from "react"
import { render, screen } from "@testing-library/react"
import CreateRecipeType from "./create"
import { WrapperWithRouter, WrapperWithRoutes, WrapWithCommonContexts } from "../../../../tests/render-helpers"
import createRecipeTypeService from "services/recipe-type-service"
import userEvent from "@testing-library/user-event"

jest.mock("services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

jest.mock("components/recipe-type-form/recipe-type-form", () => ({
    __esModule: true,
    default: jest.fn().mockImplementation(({ onSubmit }) => <>
        <p>Create recipe type form</p>
        <button aria-label="Create recipe type"
            onClick={() => onSubmit({ id: 0, name: "Fish" })}>
            Create
        </button>
    </>)
}))

describe("Create recipe type", () => {
    const createRecipeTypeMock = jest.fn()

    createRecipeTypeServiceMock.mockImplementation(() => ({
        getAll: jest.fn(),
        update: jest.fn(),
        find: jest.fn(),
        delete: jest.fn(),
        create: createRecipeTypeMock
    }))

    beforeEach(jest.clearAllMocks)

    it("renders the initial form", () => {
        render(
            <WrapperWithRouter>
                <CreateRecipeType />
            </WrapperWithRouter>
        )

        expect(screen.getByText(/translated recipe-type-feature.create-label/i)).toBeInTheDocument()
        expect(screen.getByText(/Create recipe type form/i)).toBeInTheDocument()
    })

    it("creates the recipe type in the Cookbook API and navigates to the details", async () => {
        createRecipeTypeMock.mockResolvedValueOnce({ id: 1 })
        render(
            <WrapWithCommonContexts>
                <WrapperWithRoutes initialPath="/recipetype/new" routeConfiguration={[
                    { path: "/recipetype/new", element: <CreateRecipeType /> },
                    {
                        path: "/recipetype/:id/details",
                        element: <div>I'm the recipe type details page for id 1</div>
                    }
                ]} />
            </WrapWithCommonContexts>
        )

        await userEvent.click(screen.getByLabelText(/create recipe type/i))

        expect(await screen.findByText(/^translated recipe-type-feature.create.success #Fish#$/i)).toBeInTheDocument()
        expect(await screen.findByText(/i'm the recipe type details page for id 1/i)).toBeInTheDocument()
        expect(createRecipeTypeMock).toHaveBeenCalledWith({ name: "Fish" })
    })

    it("shows an error message if the create API call fails", async () => {
        createRecipeTypeMock.mockRejectedValueOnce({ message: "Duplicate recipe type" })
        render(
            <WrapWithCommonContexts>
                <WrapperWithRouter>
                    <CreateRecipeType />
                </WrapperWithRouter>
            </WrapWithCommonContexts>
        )

        await userEvent.click(screen.getByLabelText(/create recipe type/i))

        expect(await screen.findByText(/^translated recipe-type-feature.create.failure$/i)).toBeInTheDocument()
        expect(await screen.findByText(/^duplicate recipe type$/i)).toBeInTheDocument()
        expect(createRecipeTypeMock).toHaveBeenCalledWith({ name: "Fish" })
    })
})
