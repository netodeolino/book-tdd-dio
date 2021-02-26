package com.br.booktdddio.builder;

import com.br.booktdddio.dto.BookDTO;
import lombok.Builder;

@Builder
public class BookDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "TDD da DIO";

    @Builder.Default
    private String authorName = "Francisco";

    public BookDTO toBookDTO() {
        return new BookDTO(id, name, authorName);
    }

}
