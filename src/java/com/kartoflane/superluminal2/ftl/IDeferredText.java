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
