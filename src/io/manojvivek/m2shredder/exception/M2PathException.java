package io.manojvivek.m2shredder.exception;

import java.nio.file.InvalidPathException;

/**
 * @author manojvivek
 *
 */
public class M2PathException extends Exception {

    /**
	 * @param string
	 */
	public M2PathException(String string) {
		super(string);
	}

	/**
	 * @param e
	 */
	public M2PathException(InvalidPathException e) {
		super(e);
	}

	/**
	 * 
	 */
	public M2PathException() {
		// TODO Auto-generated constructor stub
	}

	/**
     * 
     */
    private static final long serialVersionUID = 2561156908293187964L;

}
