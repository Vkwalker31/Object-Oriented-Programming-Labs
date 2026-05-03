package com.example.bookexchange.api;

import com.example.bookexchange.api.dto.BookCreateRequest;
import com.example.bookexchange.api.dto.BookResponse;
import com.example.bookexchange.controllers.BookUseCase;
import com.example.bookexchange.models.Book;
import com.example.bookexchange.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Catalog")
public class BookApiController {
    private final BookUseCase bookUseCase;

    @PostMapping
    @Operation(summary = "Add book to catalog")
    public ResponseEntity<ApiResponse<BookResponse>> create(@Valid @RequestBody BookCreateRequest request) {
        Book saved = bookUseCase.addBook(request.title(), request.author(), request.isbn());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(toResponse(saved)));
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "Get book by id")
    public ResponseEntity<ApiResponse<BookResponse>> get(@PathVariable UUID bookId) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(bookUseCase.getBook(bookId))));
    }

    @GetMapping
    @Operation(summary = "Search books by filters")
    public ResponseEntity<ApiResponse<List<BookResponse>>> search(@RequestParam(required = false) String title,
                                                                  @RequestParam(required = false) String author,
                                                                  @RequestParam(required = false) String isbn) {
        List<BookResponse> response = bookUseCase.search(title, author, isbn).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getRating());
    }
}
