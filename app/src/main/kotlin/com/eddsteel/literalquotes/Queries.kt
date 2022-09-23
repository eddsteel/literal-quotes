package com.eddsteel.literalquotes

import kotlinx.serialization.Serializable

fun getBookIdByIsbn(isbn: String): String = """
{
    "query": "{ book(where: { isbn13: \"$isbn\" }) { id }}"
}"""

fun createHighlight(bookId: String, quote: String) = """
{
    "query": "mutation { createMoment(bookId: \"$bookId\", quote: \"$quote\") { quote }}"
}"""

fun getHighlights(bookId: String, handle: String) = """
{
    "query": "{ momentsByHandleAndBookId(bookId: \"$bookId\", handle: \"$handle\") { quote } }"
}"""

@Serializable
data class GetBookIdByIsbnResponse(val data: BookIdResponseData) {
    val bookID: String
      get() = data.book.id
}
@Serializable
data class GetHighlightsResponse(val data: GetHighlightsResponseData) {
    val quotes: List<String>
      get() = data.momentsByHandleAndBookId.map { it.quote }
}

@Serializable
data class BookIdResponseData(val book: BookIdContainer)
@Serializable
data class GetHighlightsResponseData(val momentsByHandleAndBookId: List<QuoteContainer>)
@Serializable
data class BookIdContainer(val id: String)
@Serializable
data class QuoteContainer(val quote: String)
