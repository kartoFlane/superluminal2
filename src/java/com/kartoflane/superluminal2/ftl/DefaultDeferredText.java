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

	public DefaultDeferredText( String textId, String resolvedText )
	{
		this.textId = textId;
		this.resolvedText = resolvedText;
	}

	public IDeferredText derive( String newTextId )
	{
		return new DefaultDeferredText( newTextId, resolvedText );
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
	 * 
	 * @see {@link IDeferredText#getTextValue()}
	 */
	@Override
	public String getTextValue()
	{
		return resolvedText == null ? textId : resolvedText;
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
