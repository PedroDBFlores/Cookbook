import React from "react"
import PropTypes from "prop-types"
import {makeStyles, createStyles, Theme, useTheme} from "@material-ui/core/styles"
import {IconButton} from "@material-ui/core"
import {KeyboardArrowLeft, KeyboardArrowRight, FirstPage, LastPage} from "@material-ui/icons"

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        root: {
            flexShrink: 0,
            marginLeft: theme.spacing(2.5),
        },
    }),
)

interface TablePaginationActionsProps {
    count: number
    page: number
    rowsPerPage: number
    onChangePage: (event: React.MouseEvent<HTMLButtonElement> | null, page: number) => void
}

const TablePaginationActions: React.FC<TablePaginationActionsProps> = ({count, page, rowsPerPage, onChangePage}) => {
    const classes = useStyles()
    const theme = useTheme()
    const isRtl = theme.direction === "rtl"
    const numberOfPages = Math.ceil(count / rowsPerPage) - 1

    const handleFirstPageButtonClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        onChangePage(event, 0)
    }

    const handlePreviousButtonClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        onChangePage(event, page - 1)
    }

    const handleNextButtonClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        onChangePage(event, page + 1)
    }

    const handleLastPageButtonClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        onChangePage(event, Math.max(0, numberOfPages))
    }


    return <div className={classes.root}>
        <IconButton
            onClick={handleFirstPageButtonClick}
            disabled={page === 0}
            aria-label="first page">
            {isRtl ? <LastPage/> : <FirstPage/>}
        </IconButton>
        <IconButton
            onClick={handlePreviousButtonClick}
            disabled={page === 0}
            aria-label="previous page">
            {isRtl ? <KeyboardArrowRight/> : <KeyboardArrowLeft/>}
        </IconButton>
        <IconButton
            onClick={handleNextButtonClick}
            disabled={page >= numberOfPages}
            aria-label="next page">
            {isRtl ? <KeyboardArrowLeft/> : <KeyboardArrowRight/>}
        </IconButton>
        <IconButton
            onClick={handleLastPageButtonClick}
            disabled={page >= numberOfPages}
            aria-label="last page">
            {isRtl ? <FirstPage/> : <LastPage/>}
        </IconButton>
    </div>
}
TablePaginationActions.propTypes = {
    count: PropTypes.number.isRequired,
    page: PropTypes.number.isRequired,
    rowsPerPage: PropTypes.number.isRequired,
    onChangePage: PropTypes.func.isRequired
}

export default TablePaginationActions