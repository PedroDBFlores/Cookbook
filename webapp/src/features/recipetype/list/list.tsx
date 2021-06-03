import React from "react"
import PropTypes from "prop-types"
import {useHistory} from "react-router-dom"
import {MdDelete, MdEdit, MdVisibility} from "react-icons/md"
import {RecipeType} from "services/recipe-type-service"
import {Button, ButtonGroup, Table, Tbody, Td, Th, Thead, Tr, Text} from "@chakra-ui/react"
import {useTranslation} from "react-i18next"

interface RecipeTypeListProps {
    recipeTypes: Array<RecipeType>
    onDelete: (id: number, name: string) => void
}

const RecipeTypeList: React.VFC<RecipeTypeListProps> = ({recipeTypes, onDelete}) => {
    const {t} = useTranslation()
    const history = useHistory()

    const navigateToDetails = (id: number): void => history.push(`/recipetype/${id}/details`)
    const navigateToEdit = (id: number): void => history.push(`/recipetype/${id}/edit`)

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
                {recipeTypes.map(({id, name}) =>
                    <Tr key={`recipeType-${id}`}>
                        <Td>{id}</Td>
                        <Td>{name}</Td>
                        <Td align="center">
                            <ButtonGroup>
                                <Button aria-label={t("recipe-type-feature.list.details-for-label", {id})}
                                        onClick={() => navigateToDetails(id)}>
                                    <MdVisibility/>
                                </Button>
                                <Button aria-label={t("recipe-type-feature.list.edit-for-label", {id})}
                                        onClick={() => navigateToEdit(id)}>
                                    <MdEdit/>
                                </Button>
                                <Button aria-label={t("recipe-type-feature.list.delete-for-label", {id})}
                                        onClick={() => onDelete(id, name)}>
                                    <MdDelete/>
                                </Button>
                            </ButtonGroup>
                        </Td>
                    </Tr>)}
            </Tbody>
        </Table>
}

RecipeTypeList.propTypes = {
    recipeTypes: PropTypes.array.isRequired,
    onDelete: PropTypes.func.isRequired
}

export default RecipeTypeList
