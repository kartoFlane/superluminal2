package com.kartoflane.superluminal2.components;

import com.kartoflane.superluminal2.components.interfaces.Predicate;

public class DefaultSearchResult implements Predicate<Object> {
	public boolean accept(Object object) {
		return false;
	}
}