import { render, screen } from '@solidjs/testing-library'
import Text from '.'

describe('Text atom', () => {
    it('renders a text', () => {
        render(() => <Text value="A text" />)

        expect(screen.getByText('A text')).toBeInTheDocument()
    })
})