package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.exception.*;
import com.epam.esm.controller.util.Message;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public abstract class BaseExceptionHandlingController {

    @ExceptionHandler({CertificateNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Message certificateNotFound(CertificateNotFoundException ex) {
        long id = ex.getCertificateId();
        return new Message(HttpStatus.NOT_FOUND, String.format("Certificate %d can not be found", id));
    }

    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Message tagNotFound(TagNotFoundException ex) {
        String tagName = ex.getTagName();
        return new Message(HttpStatus.NOT_FOUND, String.format("Tag '%s' can not be found", tagName));
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Message userNotFound(UserNotFoundException ex) {
        long id = ex.getUserId();
        return new Message(HttpStatus.NOT_FOUND, String.format("User %d can not be found", id));
    }

    @ExceptionHandler({PurchaseNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Message purchaseNotFound(PurchaseNotFoundException ex) {
        long id = ex.getPurchaseId();
        return new Message(HttpStatus.NOT_FOUND, String.format("Purchase %d can not be found", id));
    }

    @ExceptionHandler(BadRequestParametersException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Message badRequestFound(BadRequestParametersException ex) {
        return new Message(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


}
