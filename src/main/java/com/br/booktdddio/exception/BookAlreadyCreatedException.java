package com.br.booktdddio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookAlreadyCreatedException extends Exception {

    public BookAlreadyCreatedException(String name) {
        super(String.format("Book with name %s already created in the system.", name));
    }

}
