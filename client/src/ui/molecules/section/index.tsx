import { JSXElement } from 'solid-js'
import Flex from '../../atoms/flex'

type SectionProps = {
    title: string
    children: JSXElement
    actions?: JSXElement
}

const Section = (props: SectionProps) => (
    <Flex flexDirection="flex-col" width='w-full'>
        <Flex alignItems='items-center' justifyContent='justify-between'>
            <p>{props.title}</p>
            {props.children}
        </Flex>
        <div>
            {props.actions}
        </div>
    </Flex>
)

export default Section