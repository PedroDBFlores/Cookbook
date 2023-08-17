import { JSXElement, createMemo } from 'solid-js'
import { Tailwindest, createTools } from 'tailwindest'

export const tw = createTools<Tailwindest>()

type FlexProps = {
    children: JSXElement
    flexDirection?: Tailwindest['flexDirection']
    justifyContent?: Tailwindest['justifyContent']
    alignItems?: Tailwindest['alignItems']
    gap?: Tailwindest['gap']
    width?: Tailwindest['width']
}

const Flex = (props: FlexProps) => {
    const classes = createMemo(() => tw.style({
        display: 'flex',
        flexDirection: props.flexDirection,
        alignItems: props.alignItems,
        justifyContent: props.justifyContent,
        gap: props.gap,
    }))
    
    return (
        <div class={classes().class}>
            {props.children}
        </div>
    )
}

export default Flex