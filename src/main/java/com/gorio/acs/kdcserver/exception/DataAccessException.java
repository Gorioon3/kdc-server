package com.gorio.acs.kdcserver.exception;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Class Name DataAccessException
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/25
 */
public class DataAccessException extends Exception {
    public DataAccessException() {
        super();
    }

    public DataAccessException(String s) {
        super(s);
    }

    public DataAccessException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DataAccessException(Throwable throwable) {
        super(throwable);
    }

    protected DataAccessException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
