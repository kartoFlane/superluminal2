package com.kartoflane.superluminal2.ftl;

import java.util.ArrayList;

import com.kartoflane.superluminal2.components.interfaces.Identifiable;


public abstract class BlueprintList<T> extends ArrayList<T> implements Identifiable, Comparable<BlueprintList<?>>
{
	private static final long serialVersionUID = 4618623391139370151L;

	protected final String blueprintName;


	public BlueprintList( String blueprint )
	{
		super();
		blueprintName = blueprint;
	}

	public String getBlueprintName()
	{
		return blueprintName;
	}

	@Override
	public String getIdentifier()
	{
		return blueprintName;
	}

	@Override
	public int compareTo( BlueprintList<?> o )
	{
		return blueprintName.compareTo( o.blueprintName );
	}

	@Override
	public boolean equals( Object o )
	{
		if ( o instanceof BlueprintList ) {
			BlueprintList<?> other = (BlueprintList<?>)o;
			return blueprintName.equals( other.blueprintName );
		}
		else
			return false;
	}
}
