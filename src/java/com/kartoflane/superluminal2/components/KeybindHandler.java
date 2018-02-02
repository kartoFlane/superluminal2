package com.kartoflane.superluminal2.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.widgets.Shell;


public class KeybindHandler
{
	private HashMap<Shell, ArrayList<Hotkey>> keyMap = null;


	public KeybindHandler()
	{
	}

	@SuppressWarnings("unchecked")
	public List<Hotkey> getHotkeys( Shell shell )
	{
		if ( keyMap == null )
			return new ArrayList<Hotkey>();

		ArrayList<Hotkey> result = keyMap.get( shell );
		if ( result == null )
			return new ArrayList<Hotkey>();

		return (List<Hotkey>)result.clone();
	}

	public void hook( Shell shell, Hotkey hotkey )
	{
		if ( shell == null || hotkey == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		if ( keyMap == null )
			keyMap = new HashMap<Shell, ArrayList<Hotkey>>();
		if ( keyMap.get( shell ) == null )
			keyMap.put( shell, new ArrayList<Hotkey>() );

		keyMap.get( shell ).add( hotkey );
	}

	public boolean hooks( Shell shell )
	{
		if ( keyMap == null )
			return false;

		return keyMap.containsKey( shell );
	}

	/**
	 * Notifies the handler that the specified key combination has been pressed, thus triggering the
	 * hotkey associated with the combination.
	 * 
	 * @return true if a hotkey was triggered due to the event call, false otherwise
	 */
	public boolean notifyPressed( Shell shell, boolean shift, boolean ctrl, boolean alt, boolean cmd, int key )
	{
		if ( shell == null )
			throw new IllegalArgumentException( "Shell must not be null." );
		if ( keyMap == null )
			return false;
		if ( !hooks( shell ) )
			return false;

		for ( Hotkey h : keyMap.get( shell ) ) {
			if ( h.isEnabled() && h.passes( shift, ctrl, alt, cmd, key ) ) {
				h.executePress();
				return true;
			}
		}

		return false;
	}

	/**
	 * Notifies the handler that the specified key combination has been pressed, thus triggering the
	 * hotkey associated with the combination.
	 * 
	 * @return true if a hotkey was triggered due to the event call, false otherwise
	 */
	public boolean notifyReleased( Shell shell, boolean shift, boolean ctrl, boolean alt, boolean cmd, int key )
	{
		if ( shell == null )
			throw new IllegalArgumentException( "Shell must not be null." );
		if ( keyMap == null )
			return false;
		if ( !hooks( shell ) )
			return false;

		for ( Hotkey h : keyMap.get( shell ) ) {
			if ( h.isEnabled() && h.passes( shift, ctrl, alt, cmd, key ) ) {
				h.executeRelease();
				return true;
			}
		}

		return false;
	}

	public int size()
	{
		if ( keyMap == null )
			return 0;
		return keyMap.size();
	}

	public void unhook( Shell shell, Hotkey hotkey )
	{
		if ( keyMap == null )
			return;
		if ( hooks( shell ) )
			keyMap.get( shell ).remove( hotkey );
	}

	public void unhook( Shell shell )
	{
		if ( keyMap == null )
			return;
		keyMap.remove( shell );
	}

	public void dispose()
	{
		if ( keyMap != null )
			keyMap.clear();
		keyMap = null;
	}
}
