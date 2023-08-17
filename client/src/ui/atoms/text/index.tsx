import { createMemo } from 'solid-js'
import { Tailwindest } from 'tailwindest'
import { tw } from '../flex'

type TextProps = {
    fontSize?: Tailwindest['fontSize']
    value: string
}

const Text = (props: TextProps) => {
    const classes = createMemo(() => tw.style({
        fontSize: props.fontSize ?? 'text-base'
    }))

    return <p class={classes().class}>{props.value}</p>
}

export default Text