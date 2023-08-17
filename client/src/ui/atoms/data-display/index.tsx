import Flex from '../flex'
import Text from '../text'

type DataDisplayProps = {
    title: string
    content: string
}

const DataDisplay = (props: DataDisplayProps) => (
    <Flex flexDirection="flex-col">
        <h4 class="m-0 text-2xl">{props.title}</h4>
        <Text fontSize='text-base' value={props.content} />
    </Flex>
)

export default DataDisplay