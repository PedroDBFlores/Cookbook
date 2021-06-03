import React, { useState } from "react"
import PropTypes from "prop-types"
import { SearchResult } from "model"
import { MdDelete, MdEdit, MdVisibility } from "react-icons/md"
import { useHistory } from "react-router-dom"
import { RecipeDetails } from "services/recipe-service"
import { Table, Thead, Th, Tr, Tbody, Td, Tfoot, ButtonGroup, Button } from "@chakra-ui/react"
import TablePagination from "components/table-pagination/table-pagination"
import {useTranslation} from "react-i18next"

interface RecipeSearchListProps {
    searchResult: SearchResult<RecipeDetails>
    onDelete: (id: number, name: string) => void
    onChangeRowsPerPage: (rowsPerPage: number) => void
    onPageChange: (page: number) => void
}

const RecipeSearchList: React.VFC<RecipeSearchListProps> = ({
    searchResult,
    onDelete,
    onChangeRowsPerPage,
    onPageChange
}) => {
    const {t} = useTranslation()
    const [rowsPerPage, setRowsPerPage] = useState<number>(10)
    const [page, setPage] = useState<number>(1)
    const history = useHistory()

    const navigateToDetails = (id: number): void => history.push(`/recipe/${id}/details`)
    const navigateToEdit = (id: number): void => history.push(`/recipe/${id}/edit`)

    const handleOnChangePage = (page: number) => {
        setPage(page)
        onPageChange(page)
    }

    const handleOnChangeRowsPerPage = (rowsPerPage: number) => {
        setPage(1)
        setRowsPerPage(rowsPerPage)
        onChangeRowsPerPage(rowsPerPage)
    }

    return <Table>
        <Thead>
            <Tr>
                <Th>{t("id")}</Th>
                <Th>{t("name")}</Th>
                <Th>{t("recipe-type-feature.singular")}</Th>
                <Th align="center">{t("actions")}</Th>
            </Tr>
        </Thead>
        <Tbody>
            {
                !searchResult?.count
                    ? <Tr>
                        <Td colSpan={5}>
                            {t("recipe-feature.search.no-matching-recipes")}
                        </Td>
                    </Tr>
                    : searchResult.results.map(({ id, name, recipeTypeName }) =>
                        <Tr key={`recipe-${id}`}>
                            <Td>{id}</Td>
                            <Td>{name}</Td>
                            <Td>{recipeTypeName}</Td>
                            <Td align="center">
                                <ButtonGroup>
                                    <Button aria-label={t("recipe-feature.details-label")}
                                        onClick={() => navigateToDetails(id)}>
                                        <MdVisibility/>
                                    </Button>
                                    <Button aria-label={t("recipe-feature.edit-label")}
                                        onClick={() => navigateToEdit(id)}>
                                        <MdEdit/>
                                    </Button>
                                    <Button aria-label={t("recipe-feature.delete-label")}
                                        onClick={() => onDelete(id, name)}>
                                        <MdDelete/>
                                    </Button>
                                </ButtonGroup>
                            </Td>
                        </Tr>)
            }
        </Tbody>
        <Tfoot>
            <Tr>
                <Td colSpan={5}>
                    <TablePagination
                        count={searchResult.count}
                        page={page}
                        rowsPerPage={rowsPerPage}
                        onChangeRowsPerPage={handleOnChangeRowsPerPage}
                        onChangePage={handleOnChangePage}
                    />
                </Td>
            </Tr>
        </Tfoot>
    </Table>
}

RecipeSearchList.propTypes = {
    searchResult: PropTypes.shape({
        count: PropTypes.number.isRequired,
        numberOfPages: PropTypes.number.isRequired,
        results: PropTypes.array.isRequired
    }).isRequired,
    onChangeRowsPerPage: PropTypes.func.isRequired,
    onPageChange: PropTypes.func.isRequired,
    onDelete: PropTypes.func.isRequired
}

export default RecipeSearchList
