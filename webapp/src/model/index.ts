/* Responses */
export interface SearchResult<T> {
    count: number
    numberOfPages: number
    results: Array<T>
}

export interface CreateResult {
    id: number
}

export interface ResponseError {
    code: string
    message: string
}