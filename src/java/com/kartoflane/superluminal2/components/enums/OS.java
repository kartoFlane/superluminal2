package com.kartoflane.superluminal2.components.enums;

/**
 * An enum representing operating systems supported in the application.
 * 
 * @author kartoFlane
 *
 */
public enum OS {
	UNKNOWN,
	WINDOWS32,
	WINDOWS64,
	MACOSX32,
	MACOSX64,
	LINUX32,
	LINUX64;

	public static OS[] WINDOWS() {
		return new OS[] { WINDOWS32, WINDOWS64 };
	}

	public static OS[] MACOSX() {
		return new OS[] { MACOSX32, MACOSX64 };
	}

	public static OS[] LINUX() {
		return new OS[] { LINUX32, LINUX64 };
	}

	public boolean isUnknown() {
		return this == UNKNOWN;
	}

	public boolean isWindows() {
		return this == WINDOWS32 || this == WINDOWS64;
	}

	public boolean isMac() {
		return this == MACOSX32 || this == MACOSX64;
	}

	public boolean isLinux() {
		return this == LINUX32 || this == LINUX64;
	}

	public boolean is32Bit() {
		return this == WINDOWS32 || this == MACOSX32 || this == LINUX32;
	}

	public boolean is64Bit() {
		return this == WINDOWS64 || this == MACOSX64 || this == LINUX64;
	}

	@Override
	public String toString() {
		String result = "";

		if (isUnknown())
			return "Unknown";

		if (isWindows())
			result = "Windows ";
		else if (isMac())
			result = "Mac ";
		else if (isLinux())
			result = "Linux ";

		result += is32Bit() ? "32-bit" : "64-bit";
		return result;
	}
}
