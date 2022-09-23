package com.eddsteel.literalquotes

import com.charleskorn.kaml.Yaml
import com.eddsteel.literalquotes.model.Book
import java.io.File
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json

class Config {
    val booksPath = FileSystems.getDefault().getPath(System.getenv("BOOKS_PATH"))
    val token = System.getenv("LITERAL_TOKEN")
    val handle = System.getenv("LITERAL_HANDLE")
}

class LiteralClient {
    val decoder = Json
    val httpClient = HttpClient.newHttpClient()

    fun postRequest(token: String, json: String): HttpRequest = HttpRequest.newBuilder()
        .uri(URI.create("https://literal.club/graphql"))
        .header("Authorization", "Bearer $token")
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString(json))
        .build()

    fun getBookId(token: String, isbn: String): String {
        val response = httpClient
            .send(postRequest(token, getBookIdByIsbn(isbn)), BodyHandlers.ofString())
        return if (response.statusCode() == 200) {
            val responseData =
                decoder.decodeFromString(GetBookIdByIsbnResponse.serializer(), response.body())
            responseData.bookID
        } else {
            error("${response.statusCode()} - ${response.body()}")
        }
    }

    fun getQuotes(token: String, bookId: String, handle: String): List<String> {
        val response = httpClient
            .send(postRequest(token, getHighlights(bookId, handle)), BodyHandlers.ofString())
        return if (response.statusCode() == 200) {
            val responseData =
                decoder.decodeFromString(GetHighlightsResponse.serializer(), response.body())
            responseData.quotes
        } else {
            error("${response.statusCode()} - ${response.body()}")
        }
    }

    fun addQuote(token: String, bookId: String, quote: String): Unit {
        val response = httpClient
            .send(postRequest(token, createHighlight(bookId, quote)), BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            error("${response.statusCode()} - ${response.body()}")
        }
    }
}

class Scanner(val booksPath: Path) {
    val decoder = Yaml.default

    fun <T> forEach(f: (Book) -> T): Unit = Files.newDirectoryStream(booksPath).forEach { path ->
        try {
          f(decoder.decodeFromString(Book.serializer(), String(Files.readAllBytes(path))))
        } catch (e: Exception) {
            error("$path: ${e.message}")
        }
    }
}

fun main() {
    val config = Config()
    val client = LiteralClient()

    Scanner(config.booksPath).forEach { book ->
      println("${book.title} (${book.quotes.size})")
      val id = client.getBookId(config.token, book.isbn)
      val quotes = client.getQuotes(config.token, id, config.handle)
      if (quotes.isNotEmpty()) {
          println("${book.title} already has ${quotes.size} quotes, skipping.")
      } else {
          book.quotes.forEach { quote ->
              client.addQuote(config.token, id, quote)
          }
      }
    }
}
