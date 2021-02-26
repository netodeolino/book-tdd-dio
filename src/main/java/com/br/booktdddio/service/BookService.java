package com.br.booktdddio.service;

import com.br.booktdddio.dto.BookDTO;
import com.br.booktdddio.entity.Book;
import com.br.booktdddio.exception.BookAlreadyCreatedException;
import com.br.booktdddio.mapper.BookMapper;
import com.br.booktdddio.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookMapper bookMapper = BookMapper.INSTANCE;

    @Autowired
    private BookRepository bookRepository;

    public BookDTO create(BookDTO bookDTO) throws BookAlreadyCreatedException {
        verifyIfIsAlreadyCreated(bookDTO.getName());
        Book book = bookMapper.toModel(bookDTO);
        Book bookSaved = bookRepository.save(book);
        return bookMapper.toDTO(bookSaved);
    }

    public List<BookDTO> listAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
    }

    private void verifyIfIsAlreadyCreated(String name) throws BookAlreadyCreatedException {
        Optional<Book> optBookSaved = bookRepository.findByName(name);
        if (optBookSaved.isPresent()) {
            throw new BookAlreadyCreatedException(name);
        }
    }

}
