package org.unsynchronized;

import java.io.IOException;


/**
 * Exception used to signal that an exception object was successfully read from the
 * stream. This object holds a reference to the serialized exception object.
 */
public class ReadException extends IOException
{
	public static final long serialVersionUID = 2277356908919221L;
	public Content exceptionobj;


	/**
	 * Constructor.
	 * 
	 * @param c
	 *            the serialized exception object that was read
	 */
	public ReadException( Content c )
	{
		super( "serialized exception read during stream" );
		this.exceptionobj = c;
	}

	/**
	 * Gets the Exception object that was thrown.
	 * 
	 * @return the content representing the serialized exception object
	 */
	public Content getExceptionObject()
	{
		return exceptionobj;
	}
}
