package com.kartoflane.ftl.layout;

import java.util.Arrays;


/**
 * The base class for a layout object, ie. a set of integer
 * values preceded by a label, found in a .txt FTL layout file.
 * 
 * Essentially, a thin wrapper around an int array, with a label.
 * 
 * Layout objects are unmodifiable after their creation, and don't
 * provide any sort of logic, only access to raw values.
 * 
 * @author kartoFlane
 *
 */
public abstract class LayoutObject {

	protected final LOType type;
	protected final int[] values;

	public LayoutObject(LOType type, int size) {
		this.type = type;
		this.values = new int[size];
	}

	public final LOType getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayoutObject other = (LayoutObject) obj;
		if (type != other.type)
			return false;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LayoutObject [type=");
		builder.append(type);
		builder.append(", values=");
		builder.append(Arrays.toString(values));
		builder.append("]");
		return builder.toString();
	}
}
