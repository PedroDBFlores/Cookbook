import React, {Children, ReactNode, ReactNodeArray} from "react"
import PropTypes from "prop-types"

interface ChooseProps {
    children: ReactNode | ReactNodeArray
}

export const Choose: React.FC<ChooseProps> = (
    {children}
) => {
    const chooseChildren = Children.toArray(children)

    /* eslint-disable-next-line */
    const componentTypePredicate = (child: any, componentName: string) =>
        child?.type?.name == componentName

    const whenChildren = chooseChildren.filter(child => componentTypePredicate(child, "When"))
    const otherwiseChildren = chooseChildren.filter(child => componentTypePredicate(child, "Otherwise"))
    // @ts-ignore
    const singleWhen = whenChildren.filter(c => c.props.condition)[0]

    return <>{singleWhen || otherwiseChildren[0]}</>
}
Choose.propTypes = {
    children: PropTypes.oneOfType([
        PropTypes.arrayOf(PropTypes.node),
        PropTypes.node
    ]).isRequired
}

interface WhenProps {
    condition: boolean
    children: ReactNode
}

export const When: React.FC<WhenProps> = (
    {condition, children}
) => <>{condition && children}</>

When.propTypes = {
    condition: PropTypes.bool.isRequired,
    children: PropTypes.element.isRequired
}
export const Otherwise: React.FC = (
    {children}
) => <>{children}</>

Otherwise.propTypes = {
    children: PropTypes.element.isRequired
}
