import React from "react"
import { useNavigate } from "react-router-dom"
import { MdDelete, MdEdit, MdVisibility } from "react-icons/md"
import { RecipeType } from "services/recipe-type-service"
import { Button, ButtonGroup, Table, Tbody, Td, Th, Thead, Tr, Text } from "@chakra-ui/react"
import { useTranslation } from "react-i18next"

interface RecipeTypeListProps {
    recipeTypes: Array<RecipeType>
    onDelete: (id: number, name: string) => void
}

const RecipeTypeList: React.FC<RecipeTypeListProps> = ({ recipeTypes, onDelete }) => {
    const { t } = useTranslation()
    const navigate = useNavigate()

    const navigateToDetails = (id: number): void => navigate(`/recipetype/${id}/details`)
    const navigateToEdit = (id: number): void => navigate(`/recipetype/${id}/edit`)

    return !recipeTypes?.length
        ? <Text>{t("recipe-type-feature.list.no-results")}</Text>
        : <Table>
            <Thead>
                <Tr>
                    <Th>{t("id")}</Th>
                    <Th>{t("name")}</Th>
                    <Th align="center">{t("actions")}</Th>
                </Tr>
            </Thead>
            <Tbody>
                {recipeTypes.map(({ id, name }) =>
                    <Tr key={`recipeType-${id}`}>
                        <Td>{id}</Td>
                        <Td>{name}</Td>
                        <Td align="center">
                            <ButtonGroup>
                                <Button aria-label={t("recipe-type-feature.list.details-for-label", { id })}
                                    onClick={() => navigateToDetails(id)}>
                                    <MdVisibility />
                                </Button>
                                <Button aria-label={t("recipe-type-feature.list.edit-for-label", { id })}
                                    onClick={() => navigateToEdit(id)}>
                                    <MdEdit />
                                </Button>
                                <Button aria-label={t("recipe-type-feature.list.delete-for-label", { id })}
                                    onClick={() => onDelete(id, name)}>
                                    <MdDelete />
                                </Button>
                            </ButtonGroup>
                        </Td>
                    </Tr>)}
            </Tbody>
        </Table>
}

export default RecipeTypeList
