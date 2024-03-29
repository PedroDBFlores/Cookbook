import React, { useEffect } from "react"
import { render, screen, waitFor } from "@testing-library/react"
import RecipeSearchPage from "./search-page"
import RecipeSearchForm from "./search-form"
import { SearchResult } from "model"
import createRecipeService, { RecipeDetails } from "services/recipe-service"
import createRecipeTypeService, { RecipeType } from "services/recipe-type-service"
import { WrapperWithRouter, WrapperWithRoutes, WrapWithCommonContexts } from "../../../../tests/render-helpers"
import userEvent from "@testing-library/user-event"
import { Button } from "@chakra-ui/react"
import Modal from "components/modal/modal"

jest.mock("services/recipe-type-service")
jest.mock("services/recipe-service")
const createRecipeTypeServiceMock = createRecipeTypeService as jest.MockedFunction<typeof createRecipeTypeService>
const createRecipeServiceMock = createRecipeService as jest.MockedFunction<typeof createRecipeService>

jest.mock("features/recipe/search/search-form", () => ({
	__esModule: true,
	default: jest.fn().mockImplementation(({ recipeTypes }: { recipeTypes: Array<RecipeType> }) =>
		<>
			Search Recipe Form
			{
				recipeTypes.map(({ id, name }) => <span key={id}>{name}</span>)
			}
		</>)
}))
const recipeSearchFormMock = RecipeSearchForm as jest.MockedFunction<typeof RecipeSearchForm>

jest.mock("features/recipe/search/search-list", () => ({
	__esModule: true,
	default: jest.fn().mockImplementation(({ searchResult, onDelete, onChangeRowsPerPage, onPageChange }: {
		searchResult: SearchResult<RecipeDetails>
		onDelete: (id: number, name: string) => void
		onChangeRowsPerPage: (rowsPerPage: number) => void
		onPageChange: (page: number) => void
	}) =>
		<>
			Search Recipe List
			{
				searchResult.count
					? searchResult.results.map(({ id, name }) => <div key={id}>
						<span>{name}</span>)
						<Button
							onClick={() => onDelete(id, name)}>Delete recipe named '{name}'</Button>
					</div>) : <span>No matching recipes</span>
			}
			<Button onClick={() => onChangeRowsPerPage(20)}> Change Items per page</Button>
			<Button onClick={() => onPageChange(2)}>Change page</Button>
		</>)
}))

jest.mock("components/modal/modal", () => ({
	__esModule: true,
	default: jest.fn().mockImplementation(() => <div>Delete Recipe Modal</div>)
}))
const basicModalDialogMock = Modal as jest.MockedFunction<typeof Modal>

describe("Search recipe page component", () => {
	const getAllRecipeTypesMock = jest.fn().mockImplementation(() => Promise.resolve([
		{ id: 1, name: "Meat" },
		{ id: 2, name: "Fish" }
	]))
	const basicRecipes: RecipeDetails[] = [
		{
			id: 1,
			recipeTypeId: 1,
			name: "Arroz de Pato",
			description: "Arroz de Pato",
			ingredients: "Arroz;Pato",
			preparingSteps: "Junte arroz ao pato",
			recipeTypeName: "Carne"
		},
		{
			id: 2,
			recipeTypeId: 2,
			name: "Sopa de Peixe",
			description: "Sopa de Peixe",
			ingredients: "Batata, Cenoura, Ervilha, Cebola, Peixe, Azeite",
			preparingSteps: "Tudo lá para dentro...",
			recipeTypeName: "Peixe"
		}
	]
	const searchRecipesMock = jest.fn().mockImplementation(() =>
		Promise.resolve({
			count: 2,
			numberOfPages: 1,
			results: basicRecipes
		} as SearchResult<RecipeDetails>))
	const deleteRecipeMock = jest.fn()

	createRecipeTypeServiceMock.mockImplementation(() => ({
		getAll: getAllRecipeTypesMock,
		update: jest.fn(),
		find: jest.fn(),
		delete: jest.fn(),
		create: jest.fn()
	}))
	createRecipeServiceMock.mockImplementation(() => ({
		create: jest.fn(),
		search: searchRecipesMock,
		find: jest.fn(),
		delete: deleteRecipeMock,
		update: jest.fn(),
		getAll: jest.fn()
	}))

	beforeEach(() => {
		jest.clearAllMocks()
	})

	it("renders the initial component", async () => {
		searchRecipesMock.mockResolvedValueOnce({
			count: 0,
			numberOfPages: 1,
			results: []
		} as SearchResult<RecipeDetails>)

		render(<WrapWithCommonContexts>
			<WrapperWithRouter>
				<RecipeSearchPage />
			</WrapperWithRouter>
		</WrapWithCommonContexts>)

		expect(screen.getByText(/translated common.loading/i)).toBeInTheDocument()
		expect(screen.getByText(/^translated recipe-feature.search.title$/i)).toBeInTheDocument()
		expect(await screen.findByText(/no matching recipes/i)).toBeInTheDocument()
		expect(screen.getByText("Search Recipe Form")).toBeInTheDocument()
		expect(screen.getByText("Meat")).toBeInTheDocument()
		expect(screen.getByText("Search Recipe List")).toBeInTheDocument()
		expect(getAllRecipeTypesMock).toHaveBeenCalled()
	})

	it("renders an error if fetching the recipe types fails", async () => {
		getAllRecipeTypesMock.mockRejectedValueOnce(new Error("Failed to fetch recipe types"))

		render(<WrapWithCommonContexts>
			<WrapperWithRouter>
				<RecipeSearchPage />
			</WrapperWithRouter>
		</WrapWithCommonContexts>)

		expect(await screen.findByText(/^translated recipe-feature.errors.occurred-fetching-recipe-types$/i)).toBeInTheDocument()
		expect(await screen.findByText(/^failed to fetch recipe types$/i)).toBeInTheDocument()
		expect(await screen.findByText(/^translated recipe-feature.errors.cannot-load-recipe-types$/i)).toBeInTheDocument()
	})

	test.each([
		["with a valid recipeTypeId", 1, 1],
		["with an unset recipeTypeId", 0, undefined]
	])("calls the search function with the parameters from the form %s",
		async (_, recipeTypeId, expectedOnSearchRecipeTypeId) => {
			recipeSearchFormMock.mockImplementationOnce(({ recipeTypes, onSearch }) => {
				useEffect(() => {
					if (recipeTypes) {
						onSearch({ name: "One", description: "Two", recipeTypeId })
					}
				}, [])

				return <></>
			})

			render(<WrapWithCommonContexts>
				<WrapperWithRouter>
					<RecipeSearchPage />
				</WrapperWithRouter>
			</WrapWithCommonContexts>)

			await waitFor(() => {
				expect(searchRecipesMock).toHaveBeenCalledWith({
					name: "One",
					description: "Two",
					recipeTypeId: expectedOnSearchRecipeTypeId,
					pageNumber: 1,
					itemsPerPage: 10
				})

				basicRecipes.forEach(recipeDetail => {
					expect(screen.getByText(recipeDetail.name)).toBeInTheDocument()
				})
			})
		})

	it("calls the search function and persists the form info even if the numberOfPages or pageNumber changes", async () => {
		recipeSearchFormMock.mockImplementationOnce(({ recipeTypes, onSearch }) => {
			useEffect(() => {
				if (recipeTypes) {
					onSearch({ name: "One", description: "Two", recipeTypeId: 1 })
				}
			}, [])

			return <></>
		})

		render(<WrapWithCommonContexts>
			<WrapperWithRouter>
				<RecipeSearchPage />
			</WrapperWithRouter>
		</WrapWithCommonContexts>)

		await waitFor(() => {
			expect(searchRecipesMock).toHaveBeenNthCalledWith(2, {
				name: "One",
				description: "Two",
				recipeTypeId: 1,
				pageNumber: 1,
				itemsPerPage: 10
			})
		})
		await userEvent.click(screen.getByText(/change page/i))

		await waitFor(() => {
			expect(searchRecipesMock).toHaveBeenNthCalledWith(3, {
				name: "One",
				description: "Two",
				recipeTypeId: 1,
				pageNumber: 2,
				itemsPerPage: 10
			})
		})

		await userEvent.click(screen.getByText("Change Items per page"))

		await waitFor(() => {
			expect(searchRecipesMock).toHaveBeenNthCalledWith(4, {
				name: "One",
				description: "Two",
				recipeTypeId: 1,
				pageNumber: 1,
				itemsPerPage: 20
			})
		})
	})

	describe("Actions", () => {
		beforeEach(() => {
			getAllRecipeTypesMock.mockResolvedValueOnce([])
			basicModalDialogMock.mockImplementationOnce(({ title, content, actionText, onAction }) => {
				useEffect(() => { onAction() }, [])
				return <>
					<p>{title}</p>
					<p>{content}</p>
					<p>{actionText}</p>
				</>
			})
		})

		it("navigates to the recipe create page on click", async () => {
			render(<WrapWithCommonContexts>
				<WrapperWithRoutes initialPath="/recipe" routeConfiguration={[
					{ path: "/recipe", element: <RecipeSearchPage /> },
					{ path: "/recipe/new", element: <>I'm the recipe create page</> }
				]} />
			</WrapWithCommonContexts>)

			await userEvent.click(await screen.findByLabelText(/^translated recipe-feature.create-label$/i))

			expect(await screen.findByText(/I'm the recipe create page/i)).toBeInTheDocument()
		})

		it("shows a success message when the recipe is deleted successfully", async () => {
			deleteRecipeMock.mockResolvedValueOnce(void (0))

			render(<WrapWithCommonContexts>
				<WrapperWithRouter>
					<RecipeSearchPage />
				</WrapperWithRouter>
			</WrapWithCommonContexts>)

			await userEvent.click(await screen.findByText(/delete recipe named 'Arroz de Pato'/i))

			expect(screen.getByText(/^translated common.question$/i)).toBeInTheDocument()
			expect(screen.getByText(/^translated recipe-feature.delete.question #Arroz de Pato#$/i)).toBeInTheDocument()
			expect(screen.getByText(/^translated common.delete$/i)).toBeInTheDocument()

			await waitFor(() => expect(deleteRecipeMock).toHaveBeenCalledWith(1))
			expect(await screen.findByText(/^translated recipe-feature.delete.success #Arroz de Pato#$/i)).toBeInTheDocument()
			expect(screen.queryByText(/delete recipe named 'Arroz de Pato'/i)).not.toBeInTheDocument()
		})

		it("shows a failure message if it fails to delete the recipe", async () => {
			deleteRecipeMock.mockRejectedValueOnce({ message: "Failure" })

			render(<WrapWithCommonContexts>
				<WrapperWithRouter>
					<RecipeSearchPage />
				</WrapperWithRouter>
			</WrapWithCommonContexts>)

			await userEvent.click(await screen.findByText(/delete recipe named 'Arroz de Pato'/i))

			expect(screen.getByText(/^translated common.question$/i)).toBeInTheDocument()
			expect(screen.getByText(/^translated recipe-feature.delete.question #Arroz de Pato#$/i)).toBeInTheDocument()
			expect(screen.getByText(/^translated common.delete$/i)).toBeInTheDocument()

			expect(await screen.findByText(/^translated recipe-feature.delete.failure #Arroz de Pato#$/i)).toBeInTheDocument()
			expect(await screen.findByText(/^Failure$/i)).toBeInTheDocument()
		})
	})
})
