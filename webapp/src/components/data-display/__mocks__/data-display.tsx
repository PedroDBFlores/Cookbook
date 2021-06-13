import React from "react"
import PropTypes from "prop-types"
import DataDisplay from "../data-display"

const DataDisplayMock: typeof DataDisplay = ({title, content}) => <>Mock Data Display #{title},{content}#</>

DataDisplayMock.propTypes = {
    title: PropTypes.string.isRequired,
    content: PropTypes.string.isRequired
}

export default DataDisplayMock
