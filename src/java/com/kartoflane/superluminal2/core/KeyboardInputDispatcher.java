package com.kartoflane.superluminal2.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class KeyboardInputDispatcher implements KeyListener, Listener {

	@Override
	public void keyPressed(KeyEvent e) {
		Manager.notifyKeyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Manager.notifyKeyReleased(e);
	}

	@Override
	public void handleEvent(Event e) {
		KeyEvent ke = new KeyEvent(e);
		if (e.type == SWT.KeyDown)
			keyPressed(ke);
		else if (e.type == SWT.KeyUp)
			keyReleased(ke);
	}
}
