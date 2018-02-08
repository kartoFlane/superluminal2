package com.kartoflane.superluminal2.ftl;

/**
 * Copied from Vhati's FTL Profile Editor, with minor modifications to work with Superluminal.
 * 
 * @author Vhati
 */
public class DefaultDeferredText implements IDeferredText
{
	private String textId = null;
	private String resolvedText = null;


	public DefaultDeferredText()
	{
		this( "" );
	}

	public DefaultDeferredText( String textId )
	{
		this.textId = textId;
	}

	/**
	 * Returns the "id" attribute value, or null.
	 */
	@Override
	public String getTextId()
	{
		return textId;
	}

	/**
	 * Sets the looked-up text.
	 */
	@Override
	public void setResolvedText( String s )
	{
		resolvedText = s;
	}

	/**
	 * Returns either the looked-up text or the element's own value.
	 */
	@Override
	public String getTextValue()
	{
		return resolvedText;
	}

	@Override
	public String toString()
	{
		return getTextValue();
	}

	@Override
	public int compareTo( IDeferredText o )
	{
		return getTextValue().compareTo( o.getTextValue() );
	}
}
