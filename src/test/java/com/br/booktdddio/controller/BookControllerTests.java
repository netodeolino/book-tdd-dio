package com.br.booktdddio.controller;

import com.br.booktdddio.builder.BookDTOBuilder;
import com.br.booktdddio.dto.BookDTO;
import com.br.booktdddio.exception.BookNotFoundException;
import com.br.booktdddio.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Optional;

import static com.br.booktdddio.utils.JsonUtil.asJsonString;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookControllerTests {

    private static final String BOOK_ENDPOINT_URI = "/api/v1/books";

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenABookIsCreated() throws Exception {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        // when
        when(bookService.create(bookDTO)).thenReturn(bookDTO);

        // then
        mockMvc.perform(post(BOOK_ENDPOINT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(bookDTO.getName())))
                .andExpect(jsonPath("$.authorName", is(bookDTO.getAuthorName())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();
        bookDTO.setAuthorName(null);

        // then
        mockMvc.perform(post(BOOK_ENDPOINT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETListIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        // when
        when(bookService.listAll()).thenReturn(asList(bookDTO));

        // then
        mockMvc.perform(get(BOOK_ENDPOINT_URI)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenGETListWithoutBookIsCalledThenOkStatusIsReturned() throws Exception {
        // when
        when(bookService.listAll()).thenReturn(emptyList());

        // then
        mockMvc.perform(get(BOOK_ENDPOINT_URI)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentIsReturned() throws Exception {
        // when
        doNothing().when(bookService).delete(anyLong());

        // then
        mockMvc.perform(delete(BOOK_ENDPOINT_URI + "/" + anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithoutValidIdThenNotFoundIsReturned() throws Exception {
        // when
        doThrow(BookNotFoundException.class).when(bookService).delete(anyLong());

        // then
        mockMvc.perform(delete(BOOK_ENDPOINT_URI + "/" + anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        // when
        when(bookService.findByName(bookDTO.getName())).thenReturn(bookDTO);

        // then
        mockMvc.perform(get(BOOK_ENDPOINT_URI + "/" + bookDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(bookDTO.getName())));
    }

    @Test
    void whenGETIsCalledWithoutValidNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        BookDTO bookDTO = BookDTOBuilder.builder().build().toBookDTO();

        // when
        when(bookService.findByName(bookDTO.getName())).thenThrow(BookNotFoundException.class);

        // then
        mockMvc.perform(get(BOOK_ENDPOINT_URI + "/" + bookDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
