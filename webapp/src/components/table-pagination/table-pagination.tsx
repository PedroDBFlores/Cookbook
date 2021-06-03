import React from "react"
import PropTypes from "prop-types"
import {MdFirstPage, MdKeyboardArrowLeft, MdKeyboardArrowRight, MdLastPage} from "react-icons/md"
import {Box, ButtonGroup, Flex, IconButton, Select, Spacer, Text} from "@chakra-ui/react"
import {useTranslation} from "react-i18next"

interface TablePaginationProps {
    count: number
    page: number
    rowsPerPage: number
    onChangeRowsPerPage: (rowsPerPage: number) => void
    onChangePage: (page: number) => void
}

const TablePagination: React.VFC<TablePaginationProps> = ({
                                                             count,
                                                             page,
                                                             rowsPerPage,
                                                             onChangeRowsPerPage,
                                                             onChangePage
                                                         }) => {
    const {t} = useTranslation()

    const pageNumber = Math.ceil(count / rowsPerPage)

    const handleFirstPageButtonClick = () => {
        onChangePage(1)
    }

    const handlePreviousButtonClick = () => {
        onChangePage(page - 1)
    }

    const handleNextButtonClick = () => {
        onChangePage(page + 1)
    }

    const handleLastPageButtonClick = () => {
        onChangePage(Math.max(1, pageNumber))
    }

    return <Flex>
        <Box>
            <Select aria-label={t("pagination.rows-per-page")}
                    onChange={ev => onChangeRowsPerPage(Number(ev.target.value))}>
                <option value={10}>10</option>
                <option value={20}>20</option>
                <option value={50}>50</option>
            </Select>
        </Box>
        <Spacer/>
        <Box>
            <Text>{count !== 0 ?  t("pagination.page-x-of-y", {page, totalPages :pageNumber}) : t("pagination.no-pages")}</Text>
        </Box>
        <Spacer/>
        <Box>
            <ButtonGroup isAttached>
                <IconButton
                    onClick={handleFirstPageButtonClick}
                    disabled={page === 1}
                    aria-label={t("pagination.first-page-aria-label")}>
                    <MdFirstPage/>
                </IconButton>
                <IconButton
                    onClick={handlePreviousButtonClick}
                    disabled={page === 1}
                    aria-label={t("pagination.previous-page-aria-label")}>
                    <MdKeyboardArrowLeft/>
                </IconButton>
                <IconButton
                    onClick={handleNextButtonClick}
                    disabled={page >= pageNumber}
                    aria-label={t("pagination.next-page-aria-label")}>
                    <MdKeyboardArrowRight/>
                </IconButton>
                <IconButton
                    onClick={handleLastPageButtonClick}
                    disabled={page >= pageNumber}
                    aria-label={t("pagination.last-page-aria-label")}>
                    <MdLastPage/>
                </IconButton>
            </ButtonGroup>
        </Box>
    </Flex>
}

TablePagination.propTypes = {
    count: PropTypes.number.isRequired,
    page: PropTypes.number.isRequired,
    rowsPerPage: PropTypes.number.isRequired,
    onChangeRowsPerPage: PropTypes.func.isRequired,
    onChangePage: PropTypes.func.isRequired
}

export default TablePagination
