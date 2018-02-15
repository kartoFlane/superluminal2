package com.kartoflane.superluminal2.ftl;

/**
 * An interface for elements with "id" attributes to look up.
 * 
 * @author Vhati
 */
public interface IDeferredText extends Comparable<IDeferredText>
{
	public static final IDeferredText EMPTY = new VerbatimText( "" );


	/**
	 * Creates a copy of this DeferredText with the specified id, but the same resolved text.
	 * 
	 * @param newTextId
	 *            the new text id
	 * @return the new DeferredText
	 */
	IDeferredText derive( String newTextId );

	/**
	 * Returns the "id" attribute value, or null.
	 */
	String getTextId();

	/**
	 * Sets the looked-up text.
	 */
	void setResolvedText( String s );

	/**
	 * Returns either the looked-up text or the element's own value.
	 *
	 * When a textId is available, FTL 1.6.1 will attempt to resolve it and use that resolved
	 * text. If the textId does not resolve (there's no entry in text_ files for the textId),
	 * the textId will be used verbatim, prefixed by an error message. If the id attribute is
	 * completely missing from the tag, FTL will use the tag's own text (ie. as it used to
	 * work before FTL 1.6.1)
	 */
	String getTextValue();
}
