import React from "react"
import PropTypes from "prop-types"
import {MdFirstPage, MdKeyboardArrowLeft, MdKeyboardArrowRight, MdLastPage} from "react-icons/md"
import {Box, ButtonGroup, Flex, IconButton, Select, Spacer, Text} from "@chakra-ui/react"

interface TablePaginationProps {
    count: number
    page: number
    rowsPerPage: number
    onChangeRowsPerPage: (rowsPerPage: number) => void
    onChangePage: (page: number) => void
}

const TablePagination: React.FC<TablePaginationProps> = ({
                                                             count,
                                                             page,
                                                             rowsPerPage,
                                                             onChangeRowsPerPage,
                                                             onChangePage
                                                         }) => {
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
            <Select aria-label={"Rows per page"}
                    onChange={ev => onChangeRowsPerPage(Number(ev.target.value))}>
                <option value={10}>10</option>
                <option value={20}>20</option>
                <option value={50}>50</option>
            </Select>
        </Box>
        <Spacer/>
        <Box>
            <Text>{count !== 0 ? `Page ${page} of ${pageNumber}` : "No pages"}</Text>
        </Box>
        <Spacer/>
        <Box>
            <ButtonGroup isAttached>
                <IconButton
                    onClick={handleFirstPageButtonClick}
                    disabled={page === 1}
                    aria-label="first page">
                    <MdFirstPage/>
                </IconButton>
                <IconButton
                    onClick={handlePreviousButtonClick}
                    disabled={page === 1}
                    aria-label="previous page">
                    <MdKeyboardArrowLeft/>
                </IconButton>
                <IconButton
                    onClick={handleNextButtonClick}
                    disabled={page >= pageNumber}
                    aria-label="next page">
                    <MdKeyboardArrowRight/>
                </IconButton>
                <IconButton
                    onClick={handleLastPageButtonClick}
                    disabled={page >= pageNumber}
                    aria-label="last page">
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
