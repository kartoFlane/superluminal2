package com.kartoflane.superluminal2.ftl;


/**
 * One of the "text" tags in lookup files.
 * 
 * @author Vhati
 */
public class NamedText
{
	private String id;
	private String text;


	public void setId( String id )
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public void setText( String s )
	{
		text = s;
	}

	public String getText()
	{
		return text;
	}
}
