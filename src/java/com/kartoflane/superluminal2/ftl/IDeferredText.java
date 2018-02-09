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
	 * TODO: Test to find out which one FTL prioritizes.
	 */
	String getTextValue();
}
