package com.br.booktdddio.controller;

import com.br.booktdddio.dto.BookDTO;
import com.br.booktdddio.exception.BookAlreadyCreatedException;
import com.br.booktdddio.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO) throws BookAlreadyCreatedException {
        return bookService.create(bookDTO);
    }

    @GetMapping
    public List<BookDTO> listAll() {
        return bookService.listAll();
    }

}
