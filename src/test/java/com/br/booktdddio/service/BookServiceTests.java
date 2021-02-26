package com.br.booktdddio.service;

import com.br.booktdddio.builder.BookDTOBuilder;
import com.br.booktdddio.dto.BookDTO;
import com.br.booktdddio.entity.Book;
import com.br.booktdddio.exception.BookAlreadyCreatedException;
import com.br.booktdddio.mapper.BookMapper;
import com.br.booktdddio.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {

    private final BookMapper bookMapper = BookMapper.INSTANCE;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void whenNewBookInformedThenShouldBeCreated() throws BookAlreadyCreatedException {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book expectedBookSaved = bookMapper.toModel(bookDTO);

        // when
        when(bookRepository.findByName(bookDTO.getName())).thenReturn(Optional.empty());
        when(bookRepository.save(expectedBookSaved)).thenReturn(expectedBookSaved);

        // then
        BookDTO bookDTOSaved = bookService.create(bookDTO);

        assertEquals(bookDTO.getName(), bookDTOSaved.getName());
        assertEquals(bookDTO.getAuthorName(), bookDTOSaved.getAuthorName());
    }

    @Test
    void whenAlreadyBookCreatedThenAnExceptionShouldBeThrown() {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book duplicatedBookSaved = bookMapper.toModel(bookDTO);

        // when
        when(bookRepository.findByName(bookDTO.getName())).thenReturn(Optional.of(duplicatedBookSaved));

        // then
        assertThrows(BookAlreadyCreatedException.class, () -> bookService.create(bookDTO));
    }

}
