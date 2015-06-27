package com.kartoflane.common.selfpatch;

interface SPResultTask<T> extends Runnable {

	public void interrupt();
	
	public boolean isSuccess();

	public T getResult();
}
