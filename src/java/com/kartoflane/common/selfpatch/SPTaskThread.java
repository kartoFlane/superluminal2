package com.kartoflane.common.selfpatch;

class SPTaskThread extends Thread {

	private final SPResultTask<?> task;


	public SPTaskThread( SPResultTask<?> task ) {
		this.task = task;
		this.setDaemon( true );
	}

	@Override
	public void run() {
		task.run();
	}

	@Override
	public void interrupt() {
		super.interrupt();
		task.interrupt();
	}
}
