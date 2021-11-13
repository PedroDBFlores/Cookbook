import React, {useRef} from "react"
import {IfFulfilled, IfPending, IfRejected, useAsync} from "react-async"
import {MdDelete, MdEdit} from "react-icons/md"
import {useHistory} from "react-router-dom"
import createRecipeService, {RecipeDetails as RecipeDetail} from "services/recipe-service"
import {Button, ButtonGroup, Grid, GridItem, Text, useToast} from "@chakra-ui/react"
import useModalContext from "components/modal/modal-context"
import Loader from "components/loader/loader"
import DataDisplay from "../../../components/data-display/data-display"
import Section from "components/section/section"
import {useTranslation} from "react-i18next"

const RecipeDetails: React.VFC<{ id: number }> = ({id}) => {
	const {t} = useTranslation()
	const toast = useToast()

	const {find, delete: deleteRecipe} = createRecipeService()
	const findPromiseRef = useRef(() => find(id))
	const state = useAsync<RecipeDetail>({
		promiseFn: findPromiseRef.current,
		onReject: ({message}) => toast({
			title: t("recipe-feature.errors.occurred-fetching"),
			description: <>{message}</>,
			status: "error",
			duration: 5000
		})
	})

	return <Section title={t("recipe-feature.details-title")}>
		<IfPending state={state}>
			<Loader/>
		</IfPending>
		<IfRejected state={state}>
			<Text>{t("recipe-feature.errors.cannot-load")}</Text>
		</IfRejected>
		<IfFulfilled state={state}>
			{data => <Grid templateColumns="repeat(12, 1fr)" gap={6}>
				<GridItem colSpan={12}>
					<DataDisplay title={t("recipe-feature.details.id")} content={data.id.toString()}/>
					<DataDisplay title={t("recipe-feature.details.name")} content={data.name}/>
					<DataDisplay title={t("recipe-feature.details.description")} content={data.description}/>
					<DataDisplay title={t("recipe-feature.details.ingredients")} content={data.ingredients}/>
					<DataDisplay title={t("recipe-feature.details.preparing-steps")} content={data.preparingSteps}/>
				</GridItem>
				<GridItem colSpan={12}>
					<RecipeDetailsActions recipe={data} deleteRecipeFn={deleteRecipe}/>
				</GridItem>
			</Grid>
			}
		</IfFulfilled>
	</Section>
}

interface RecipeDetailsActionsProps {
	recipe: RecipeDetail
	deleteRecipeFn: (id: number) => void
}

const RecipeDetailsActions: React.VFC<RecipeDetailsActionsProps> = ({recipe, deleteRecipeFn}) => {
	const {t} = useTranslation()
	const history = useHistory()
	const toast = useToast()
	const {openModal, closeModal} = useModalContext()

	const showModal = (id: number, name: string) =>
		openModal({
			title: t("common.question"),
			content: t("recipe-feature.delete.question"),
			actionText: t("common.delete"),
			onAction: () => handleDelete(id, name),
			onClose: closeModal
		})

	const handleDelete = async (id: number, name: string) => {
		try {
			await deleteRecipeFn(id)
			toast({title: t("recipe-feature.delete.success", {name}), status: "success"})
			history.push("/recipe")
		} catch ({message}) {
			toast({
				title: t("recipe-feature.delete.failure", {name}),
				description: <>{message}</>,
				status: "error",
				duration: 5000
			})
		}
	}

	const onEdit = (id: number) => history.push(`/recipe/${id}/edit`)

	return <ButtonGroup>
		<Button aria-label={t("recipe-feature.edit-label")}
				onClick={() => onEdit(recipe.id)}>
			<MdEdit/>
		</Button>
		<Button aria-label={t("recipe-feature.delete-label")}
				onClick={() => showModal(recipe.id, recipe.name)}>
			<MdDelete/>
		</Button>
	</ButtonGroup>
}

export default RecipeDetails
