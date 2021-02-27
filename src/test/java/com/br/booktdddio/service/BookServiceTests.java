package com.br.booktdddio.service;

import com.br.booktdddio.builder.BookDTOBuilder;
import com.br.booktdddio.dto.BookDTO;
import com.br.booktdddio.entity.Book;
import com.br.booktdddio.exception.BookAlreadyCreatedException;
import com.br.booktdddio.exception.BookNotFoundException;
import com.br.booktdddio.mapper.BookMapper;
import com.br.booktdddio.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

    @Test
    void whenListIsCalledThenReturnAList() {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book book = bookMapper.toModel(bookDTO);

        // when
        when(bookRepository.findAll()).thenReturn(asList(book));

        List<BookDTO> bookDTOList = bookService.listAll();

        // then
        assertThat(bookDTOList, is(not(empty())));
        assertEquals(bookDTO.getName(), bookDTOList.get(0).getName());
    }

    @Test
    void whenListIsCalledThenReturnAEmptyList() {
        // when
        when(bookRepository.findAll()).thenReturn(emptyList());

        List<BookDTO> bookDTOList = bookService.listAll();

        // then
        assertThat(bookDTOList, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenShouldBeDeleted() throws BookNotFoundException {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book book = bookMapper.toModel(bookDTO);

        // when
        when(bookRepository.findById(bookDTO.getId())).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        bookService.delete(bookDTO.getId());

        // then
        verify(bookRepository, times(1)).findById(bookDTO.getId());
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void whenExclusionIsCalledWithoutValidIdThenShouldBeThrown() {
        // when
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(BookNotFoundException.class, () -> bookService.delete(anyLong()));
    }

    @Test
    void whenValidBookNameIsGivenThenReturnABeer() throws BookNotFoundException {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        Book book = bookMapper.toModel(bookDTO);

        // when
        when(bookRepository.findByName(bookDTO.getName())).thenReturn(Optional.of(book));

        BookDTO foundBookDTO = bookService.findByName(bookDTO.getName());

        // then
        assertEquals(bookDTO.getName(), foundBookDTO.getName());
    }

    @Test
    void whenNotValidBookNameIsGivenThenShouldBeThrown() {
        // when
        when(bookRepository.findByName(anyString())).thenReturn(Optional.empty());

        // then
        assertThrows(BookNotFoundException.class, () -> bookService.findByName(anyString()));
    }

}
