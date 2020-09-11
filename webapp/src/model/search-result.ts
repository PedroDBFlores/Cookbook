export default interface SearchResult<T> {
    count: number
    numberOfPages: number
    results: Array<T>
}