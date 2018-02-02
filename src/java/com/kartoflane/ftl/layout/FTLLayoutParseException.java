package com.kartoflane.ftl.layout;

/**
 * Class for exceptions thrown by the FTLLayoutParser.
 * Can be used to retrieve the partial layout that the parser has
 * successfully built before the error was encountered.
 * 
 * @author kartoFlane
 *
 */
public class FTLLayoutParseException extends Exception
{
	private static final long serialVersionUID = 600L;

	private ShipLayout partialLayout = null;
	private int line = -1;


	public FTLLayoutParseException()
	{
		super();
	}

	public FTLLayoutParseException( String message )
	{
		super( message );
	}

	public FTLLayoutParseException( String message, int line )
	{
		this( message, null, null );
		this.line = line;
	}

	public FTLLayoutParseException( String message, ShipLayout partialLayout )
	{
		this( message, null, partialLayout );
	}

	public FTLLayoutParseException( String message, ShipLayout partialLayout, int line )
	{
		this( message, partialLayout );
		this.line = line;
	}

	public FTLLayoutParseException( String message, Throwable cause )
	{
		this( message, cause, null );
	}

	public FTLLayoutParseException( String message, Throwable cause, int line )
	{
		this( message, cause, null );
		this.line = line;
	}

	public FTLLayoutParseException( String message, Throwable cause, ShipLayout partialLayout )
	{
		super( message, cause );
		this.partialLayout = partialLayout;
	}

	public FTLLayoutParseException( String message, Throwable cause, ShipLayout partialLayout, int line )
	{
		this( message, cause, partialLayout );
		this.line = line;
	}

	/**
	 * @return the partial layout that was successfully built before the error occured.
	 */
	public ShipLayout getPartialLayout()
	{
		return partialLayout;
	}

	public int getLine()
	{
		return line;
	}
}
