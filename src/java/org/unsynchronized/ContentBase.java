package org.unsynchronized;

/**
 * Provides a skeleton content implementation.
 */
public class ContentBase implements Content
{
	public int handle;
	public boolean isExceptionObject;
	protected ContentType type;


	public ContentBase( ContentType type )
	{
		this.type = type;
	}

	public boolean isExceptionObject()
	{
		return isExceptionObject;
	}

	public void setIsExceptionObject( boolean value )
	{
		isExceptionObject = value;
	}

	public ContentType getType()
	{
		return type;
	}

	public int getHandle()
	{
		return this.handle;
	}

	public void validate() throws ValidityException
	{
	}
}

