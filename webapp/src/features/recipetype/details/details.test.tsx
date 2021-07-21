import React, {useEffect} from "react"
import {render, screen, waitFor} from "@testing-library/react"
import RecipeTypeDetails from "./details"
import Modal from "components/modal/modal"
import createRecipeTypeService, {RecipeType} from "services/recipe-type-service"
import userEvent from "@testing-library/user-event"
import {WrapperWithRoutes, WrapWithCommonContexts} from "../../../../tests/render-helpers"

jest.mock("services/recipe-type-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>

jest.mock("components/modal/modal", () => ({
	__esModule: true,
	default: jest.fn().mockImplementation(() => <div>Delete RecipeType Modal</div>)
}))
const basicModalDialogMock = Modal as jest.MockedFunction<typeof Modal>

describe("Recipe type details", () => {
	const baseRecipeType: RecipeType = {id: 99, name: "A recipe type"}

	const findRecipeTypeMock = jest.fn()
	const deleteRecipeTypeMock = jest.fn()

	createRecipeTypeServiceMock.mockImplementation(() => ({
		getAll: jest.fn(),
		update: jest.fn(),
		find: findRecipeTypeMock,
		delete: deleteRecipeTypeMock,
		create: jest.fn()
	}))

	beforeEach(jest.clearAllMocks)

	it("renders the recipe type details", async () => {
		findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)

		render(<WrapWithCommonContexts>
			<RecipeTypeDetails id={99}/>
		</WrapWithCommonContexts>)

		expect(screen.getByText(/^translated recipe-type-feature.details.title$/i)).toBeInTheDocument()
		expect(screen.getByText(/^translated common.loading$/i)).toBeInTheDocument()
		expect(findRecipeTypeMock).toHaveBeenCalledWith(99)
		expect(await screen.findByText(/^translated recipe-type-feature.details.id$/i)).toBeInTheDocument()
		expect(await screen.findByText(/^translated recipe-type-feature.details.name$/i)).toBeInTheDocument()
		expect(await screen.findByText(baseRecipeType.id.toString())).toBeInTheDocument()
		expect(await screen.findByText(baseRecipeType.name)).toBeInTheDocument()
	})

	it("renders an error if the recipe type cannot be obtained", async () => {
		findRecipeTypeMock.mockRejectedValueOnce(new Error("Failure"))

		render(<WrapWithCommonContexts>
			<RecipeTypeDetails id={99}/>
		</WrapWithCommonContexts>)

		expect(await screen.findByText(/^translated recipe-type-feature.errors.occurred-fetching$/i)).toBeInTheDocument()
		expect(await screen.findByText(/^failure$/i)).toBeInTheDocument()
		expect(await screen.findByText(/^translated recipe-type-feature.errors.cannot-load$/i)).toBeInTheDocument()
		expect(findRecipeTypeMock).toHaveBeenCalled()
	})

	describe("Actions", () => {

		it("takes the user to the edit recipe type page", async () => {
			findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)
			render(<WrapWithCommonContexts>
				<WrapperWithRoutes initialPath={`/recipetype/${baseRecipeType.id}/details`} routeConfiguration={[
					{
						path: `/recipetype/${baseRecipeType.id}/details`,
						exact: true,
						component: () => <RecipeTypeDetails id={baseRecipeType.id}/>
					},
					{
						path: `/recipetype/${baseRecipeType.id}/edit`,
						exact: true,
						component: () => <div>I'm the recipe type edit page</div>
					}
				]}/>
			</WrapWithCommonContexts>)

			userEvent.click(await screen.findByLabelText(/^translated recipe-type-feature.edit-label$/i))

			expect(screen.getByText(/I'm the recipe type edit page/i)).toBeInTheDocument()
		})

		it("deletes the recipe type", async () => {
			findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)
			deleteRecipeTypeMock.mockResolvedValueOnce({})
			basicModalDialogMock.mockImplementationOnce(({content, onAction}) => {
				useEffect(() => onAction(), [])
				return <div>{content}</div>
			})

			render(<WrapWithCommonContexts>
				<WrapperWithRoutes initialPath={`/recipetype/${baseRecipeType.id}/details`} routeConfiguration={[
					{
						path: `/recipetype/${baseRecipeType.id}/details`,
						exact: true,
						component: () => <RecipeTypeDetails id={baseRecipeType.id}/>
					},
					{
						path: "/recipetype",
						exact: true,
						component: () => <div>I'm the recipe type list page</div>
					}
				]}/>
			</WrapWithCommonContexts>)

			userEvent.click(await screen.findByLabelText(/^translated recipe-type-feature.delete-label$/i))
			expect(screen.getByText(/^translated recipe-type-feature.delete.question$/i)).toBeInTheDocument()

			await waitFor(() => expect(deleteRecipeTypeMock).toHaveBeenCalledWith(baseRecipeType.id))
			expect(await screen.findByText(`translated recipe-type-feature.delete.success #${baseRecipeType.name}#`)).toBeInTheDocument()
			expect(await screen.findByText(/I'm the recipe type list page/i)).toBeInTheDocument()
		})

		it("shows an error if deleting the recipe type fails", async () => {
			findRecipeTypeMock.mockResolvedValueOnce(baseRecipeType)
			deleteRecipeTypeMock.mockRejectedValueOnce({message: "In use"})
			basicModalDialogMock.mockImplementationOnce(({content, onAction}) => {
				useEffect(() => onAction(), [])
				return <div>{content}</div>
			})
			render(<WrapWithCommonContexts>
				<RecipeTypeDetails id={99}/>
			</WrapWithCommonContexts>)

			userEvent.click(await screen.findByLabelText(/^translated recipe-type-feature.delete-label$/i))
			expect(screen.getByText(/^translated recipe-type-feature.delete.question$/i)).toBeInTheDocument()

			expect(await screen.findByText(`translated recipe-type-feature.delete.failure #${baseRecipeType.name}#`)).toBeInTheDocument()
			expect(await screen.findByText(/^in use$/i)).toBeInTheDocument()
		})
	})
})
