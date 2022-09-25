import React, { useRef } from "react"
import { IfFulfilled, IfPending, IfRejected, useAsync } from "react-async"
import { MdDelete, MdEdit, MdError } from "react-icons/md"
import createRecipeTypeService, { RecipeType } from "services/recipe-type-service"
import { Button, ButtonGroup, Grid, GridItem, Text, useToast } from "@chakra-ui/react"
import useModalContext from "components/modal/modal-context"
import Loader from "components/loader/loader"
import DataDisplay from "../../../components/data-display/data-display"
import Section from "components/section/section"
import { useTranslation } from "react-i18next"
import { useNavigate, useParams } from "react-router-dom"

const RecipeTypeDetails = () => {
    const { t } = useTranslation()
    const { openModal, closeModal } = useModalContext()
    const navigate = useNavigate()
    const { id } = useParams()
    const toast = useToast()

    const { find, delete: deleteRecipeType } = createRecipeTypeService()
    const findPromiseRef = useRef(() => find(Number(id)))
    const state = useAsync<RecipeType>({
        promiseFn: findPromiseRef.current,
        onReject: ({ message }) => toast({
            title: t("recipe-type-feature.errors.occurred-fetching"),
            description: <>{message}</>,
            status: "error",
            duration: 5000
        })
    })

    const showModal = (data: RecipeType) => {
        openModal({
            title: t("common.question"),
            content: t("recipe-type-feature.delete.question"),
            actionText: t("common.delete"),
            onAction: () => { handleDelete(data.id, data.name) },
            onClose: closeModal
        })
    }

    const handleDelete = async (id: number, name: string) => {
        try {
            await deleteRecipeType(id)
            toast({
                title: t("recipe-type-feature.delete.success", { name }), status: "success"
            })
            navigate("/recipetype")
        } catch ({ message }) {
            toast({
                title: t("recipe-type-feature.delete.failure", { name }),
                description: <>{message}</>,
                status: "error",
                duration: 5000
            })
        }
    }

    const onEdit = (id: number) => navigate(`/recipetype/${id}/edit`)

    return <Section title={t("recipe-type-feature.details.title")}>
        <IfPending state={state}>
            <Loader />
        </IfPending>
        <IfRejected state={state}>
            <MdError />
            <Text>{t("recipe-type-feature.errors.cannot-load")}</Text>
        </IfRejected>
        <IfFulfilled state={state}>
            {data => <Grid templateColumns="repeat(12, 1fr)" gap={6}>
                <GridItem colSpan={12}>
                    <DataDisplay title={t("recipe-type-feature.details.id")} content={data.id.toString()} />
                    <DataDisplay title={t("recipe-type-feature.details.name")} content={data.name} />
                </GridItem>
                <GridItem colSpan={12}>
                    <ButtonGroup>
                        <Button aria-label={t("recipe-type-feature.edit-label")}
                            onClick={() => onEdit(data.id)}>
                            <MdEdit />
                        </Button>
                        <Button aria-label={t("recipe-type-feature.delete-label")}
                            onClick={() => showModal(data)}>
                            <MdDelete />
                        </Button>
                    </ButtonGroup>
                </GridItem>
            </Grid>
            }
        </IfFulfilled>
    </Section>
}

export default RecipeTypeDetails
