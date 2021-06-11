import React from "react"
import PropTypes from "prop-types"

interface DataDisplayProps {
    title: string
    content: string
}

const DataDisplay: React.VFC<DataDisplayProps> = ({title,content}) => <>Mock Data Display #{title},{content}#</>

DataDisplay.propTypes = {
    title: PropTypes.string.isRequired,
    content: PropTypes.string.isRequired
}

export default DataDisplay
