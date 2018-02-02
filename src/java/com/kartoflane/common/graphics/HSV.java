package com.kartoflane.common.graphics;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;


public class HSV {

	public float h;
	public float s;
	public float v;


	public HSV( float h, float s, float v ) {
		this.h = h;
		this.s = s;
		this.v = v;
	}

	public HSV( HSV hsv ) {
		this.h = hsv.h;
		this.s = hsv.s;
		this.v = hsv.v;
	}

	public HSV( RGB rgb ) {
		float[] hsv = rgb.getHSB();
		h = hsv[0] / 360f;
		s = hsv[1];
		v = hsv[2];
	}

	public Color toColor( Device d ) {
		RGB rgb = toRGB();
		return new Color( d, rgb.red, rgb.green, rgb.blue );
	}

	public RGB toRGB() {
		try {
			int[] rgb = toRGB( h, s, v );
			return new RGB( rgb[0], rgb[1], rgb[2] );
		}
		catch ( IllegalArgumentException e ) {
			throw new IllegalArgumentException( toString() );
		}
	}

	public String toString() {
		return "HSV { " + h + ", " + s + ", " + v + " }";
	}

	private static int[] toRGB( float hue, float saturation, float value ) {
		if ( hue == 1.0f )
			hue = 0.9999999f;
		else if ( hue == 0.0f )
			hue = 0.0000001f;
		if ( saturation == 1.0f )
			saturation = 0.9999999f;
		else if ( saturation == 0.0f )
			saturation = 0.0000001f;
		if ( value == 1.0f )
			value = 0.9999999f;
		else if ( value == 0.0f )
			value = 0.0000001f;

		int h = (int)( hue * 6 );
		float f = hue * 6 - h;
		float p = value * ( 1 - saturation ) * 256;
		float q = value * ( 1 - f * saturation ) * 256;
		float t = value * ( 1 - ( 1 - f ) * saturation ) * 256;
		float v = value * 256;

		int[] result = new int[3];

		switch ( h ) {
			case 0:
				result[0] = (int)v;
				result[1] = (int)t;
				result[2] = (int)p;
				break;
			case 1:
				result[0] = (int)q;
				result[1] = (int)v;
				result[2] = (int)p;
				break;
			case 2:
				result[0] = (int)p;
				result[1] = (int)v;
				result[2] = (int)t;
				break;
			case 3:
				result[0] = (int)p;
				result[1] = (int)q;
				result[2] = (int)v;
				break;
			case 4:
				result[0] = (int)t;
				result[1] = (int)p;
				result[2] = (int)v;
				break;
			case 5:
				result[0] = (int)v;
				result[1] = (int)p;
				result[2] = (int)q;
				break;
			default:
				throw new RuntimeException( String.format( "Error converting from HSV to RGB: H=%s, S=%s, V=%s%n", hue, saturation, value ) );
		}

		return result;
	}
}
