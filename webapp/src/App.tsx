import React, { useEffect, useState } from "react"
import { BrowserRouter, Route, Routes } from "react-router-dom"
import Layout from "components/layout/layout"
import RecipeTypeListPage from "features/recipetype/list/list-page"
import CreateRecipeType from "features/recipetype/create/create"
import RecipeTypeDetails from "features/recipetype/details/details"
import EditRecipeType from "features/recipetype/edit/edit"
import RecipeSearchPage from "features/recipe/search/search-page"
import CreateRecipe from "features/recipe/create/create"
import RecipeDetails from "features/recipe/details/details"
import EditRecipe from "features/recipe/edit/edit"
import { ChakraProvider } from "@chakra-ui/react"
import { WithModal } from "components/modal/modal-context"
import i18n from "i18next"
import { initReactI18next } from "react-i18next"
import Backend from "i18next-http-backend"
import LanguageDetector from "i18next-browser-languagedetector"

const App: React.FC = () => {
	const [ready, setReady] = useState<boolean>()

	useEffect(() => {
		prepareLanguage().then(() => setReady(true))
	}, [])

	return ready ? <AppComponent /> : null
}

const prepareLanguage = () => i18n
	.use(Backend)
	.use(LanguageDetector)
	.use(initReactI18next)
	.init({
		fallbackLng: "en",
		debug: true,

		interpolation: {
			escapeValue: false, // not needed for react as it escapes by default
		}
	})

const AppComponent = () =>
	<ChakraProvider>
		<WithModal>
			<BrowserRouter>
				<Layout>
					<Routes>
						<Route path="/recipetype" element={<RecipeTypeListPage />} />
						<Route path="/recipetype/new" element={<CreateRecipeType />} />
						<Route path="/recipetype/:id/details"
							element={x => <RecipeTypeDetails id={Number(x.match.params.id)} />} />
						<Route path="/recipetype/:id/edit"
							element={x => <EditRecipeType id={Number(x.match.params.id)} />} />

						<Route path="/recipe" element={<RecipeSearchPage />} />
						<Route path="/recipe/new" element={<CreateRecipe />} />
						<Route path="/recipe/:id/details"
							element={x => <RecipeDetails id={Number(x.match.params.id)} />} />
						<Route path="/recipe/:id/edit"
							element={x => <EditRecipe id={Number(x.match.params.id)} />} />
					</Routes>
				</Layout>
			</BrowserRouter>
		</WithModal>
	</ChakraProvider>

export default App
