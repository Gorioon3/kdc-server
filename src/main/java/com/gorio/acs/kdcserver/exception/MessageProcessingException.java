package com.gorio.acs.kdcserver.exception;

import lombok.NoArgsConstructor;

/**
 * @author Gorio
 */
@NoArgsConstructor
public class MessageProcessingException extends RuntimeException {
    public MessageProcessingException(String s) {
        super(s);
    }

    public MessageProcessingException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MessageProcessingException(Throwable throwable) {
        super(throwable);
    }

    protected MessageProcessingException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
