import React, {useEffect, useState} from "react"
import PropTypes from "prop-types"
import FormControl from "@material-ui/core/FormControl"
import InputLabel from "@material-ui/core/InputLabel"
import Select from "@material-ui/core/Select"
import MenuItem from "@material-ui/core/MenuItem"

interface SelectorProps {
    label: string
    ariaLabel?: string
    className?: string
    onLoad: () => Promise<Array<{ id: number; name: string }>>
    onChange?: () => number
}

const Selector: React.FC<SelectorProps> = ({
                                               label,
                                               ariaLabel,
                                               className,
                                               onLoad
                                           }) => {
    const [options, setOptions] = useState<Array<{ id: number; name: string }>>([])

    useEffect(() => {
        onLoad().then(setOptions)
    }, [])

    const labelWithNoSpaces = label.replace(" ", "")
    return <FormControl variant="outlined" className={className ?? ""}>
        <InputLabel id={`selector-${labelWithNoSpaces}-label`} aria-label={ariaLabel ?? ""}>{label}</InputLabel>
        <Select
            labelId={`selector-${labelWithNoSpaces}-label`}
            id={`selector-select-${labelWithNoSpaces}`}
            displayEmpty
            value=""
            label={label}>
            {
                options.map(({id, name}) => <MenuItem
                    key={`selector-option-${labelWithNoSpaces}-${id}`}
                    value={id}>
                    {name}
                </MenuItem>)
            }
        </Select>
    </FormControl>
}

Selector.propTypes = {
    label: PropTypes.string.isRequired,
    ariaLabel: PropTypes.string,
    className: PropTypes.string,
    onLoad: PropTypes.func.isRequired,
    onChange: PropTypes.func
}

export default Selector