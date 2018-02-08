package com.kartoflane.superluminal2.ftl;

/**
 * Wrapper around a String instance to make it compliant with IDeferredText.
 */
public class VerbatimText implements IDeferredText
{
	private String text;


	public VerbatimText()
	{
		this( "" );
	}

	public VerbatimText( String text )
	{
		this.text = text;
	}

	@Override
	public String getTextId()
	{
		// Shouldn't ever be called.
		throw new UnsupportedOperationException();
	}

	@Override
	public void setResolvedText( String s )
	{
		// Shouldn't ever be called.
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTextValue()
	{
		return text;
	}

	@Override
	public String toString()
	{
		return getTextValue();
	}

	@Override
	public int compareTo( IDeferredText o )
	{
		return text.compareTo( o.getTextValue() );
	}
}
