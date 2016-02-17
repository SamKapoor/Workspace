package com.pda3505.printer;

public interface SerialPortDataReceived {
	public void onDataReceivedListener(final byte[] buffer, final int size); 

}
