import React from "react"
import PropTypes from "prop-types"
import {useHistory} from "react-router-dom"
import {MdDelete, MdEdit, MdVisibility} from "react-icons/md"
import {RecipeType} from "../../../services/recipe-type-service"
import {Button, ButtonGroup, Table, Tbody, Td, Th, Thead, Tr} from "@chakra-ui/react"

interface RecipeTypeListProps {
    recipeTypes: Array<RecipeType>
    onDelete: (id: number) => void
}

const RecipeTypeList: React.FC<RecipeTypeListProps> = ({recipeTypes, onDelete}) => {
    const history = useHistory()

    const navigateToDetails = (id: number): void => history.push(`/recipetype/${id}/details`)
    const navigateToEdit = (id: number): void => history.push(`/recipetype/${id}/edit`)

    return !recipeTypes?.length ?
        <>
            No recipe types.
        </>
        :
        <Table>
            <Thead>
                <Tr>
                    <Th>Id</Th>
                    <Th>Name</Th>
                    <Th align="center">Actions</Th>
                </Tr>
            </Thead>
            <Tbody>
                {recipeTypes.map(({id, name}) =>
                    <Tr key={`recipeType-${id}`}>
                        <Td>{id}</Td>
                        <Td>{name}</Td>
                        <Td align="center">
                            <ButtonGroup>
                                <Button aria-label={`Recipe type details for id ${id}`}
                                        onClick={() => navigateToDetails(id)}>
                                    <MdVisibility/>
                                </Button>
                                <Button aria-label={`Edit Recipe type with id ${id}`}
                                        onClick={() => navigateToEdit(id)}>
                                    <MdEdit/>
                                </Button>
                                <Button aria-label={`Delete Recipe type with id ${id}`}
                                        onClick={() => onDelete(id)}>
                                    <MdDelete/>
                                </Button>
                            </ButtonGroup>
                        </Td>
                    </Tr>
                )}
            </Tbody>
        </Table>
}
RecipeTypeList.propTypes = {
    recipeTypes: PropTypes.array.isRequired,
    onDelete: PropTypes.func.isRequired
}

export default RecipeTypeList