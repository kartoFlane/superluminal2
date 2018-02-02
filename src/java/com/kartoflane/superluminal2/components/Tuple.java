package com.kartoflane.superluminal2.components;

public class Tuple<K, V>
{
	private K k = null;
	private V v = null;


	public Tuple()
	{
	}

	public Tuple( K key, V value )
	{
		k = key;
		v = value;
	}

	public void setKey( K key )
	{
		k = key;
	}

	public K getKey()
	{
		return k;
	}

	public void setValue( V value )
	{
		v = value;
	}

	public V getValue()
	{
		return v;
	}

	public int hashCode()
	{
		int hashK = k != null ? k.hashCode() : 0;
		int hashV = v != null ? v.hashCode() : 0;

		return ( hashK + hashV ) * hashV + hashK;
	}

	public boolean equals( Object o )
	{
		if ( o instanceof Tuple ) {
			Tuple<?, ?> ot = (Tuple<?, ?>)o;
			return ( ( this.k == ot.k || ( this.k != null && ot.k != null && this.k.equals( ot.k ) ) ) &&
				( this.v == ot.v || ( this.v != null && ot.v != null && this.v.equals( ot.v ) ) ) );
		}

		return false;
	}

	public String toString()
	{
		return "(" + k + ", " + v + ")";
	}
}
