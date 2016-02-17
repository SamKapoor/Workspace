package com.pda3505.Service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.serialport.api.SerialPortClass;
import android.serialport.api.SerialPortClass.SERIALPORT;
import android.util.Log;

/**
 * ���������㲥����
 * 
 * @author zkc-soft2
 * 
 */
public class RemoteControlReceiver extends BroadcastReceiver {

	private static final String TAG = "RemoteControlReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// �㲥����
		String action = intent.getAction();
		Log.i(TAG, "System message " + action);
		if (action.equals("com.zkc.keycode")) {
			if (StartReceiver.times++ > 0) {
				StartReceiver.times = 0;
				int keyValue = intent.getIntExtra("keyvalue", 0);
				Log.i(TAG, "KEY VALUE:" + keyValue);
				if (keyValue == 136 || keyValue == 135 || keyValue == 131) {
					//防止连续按键
					if(CaptureService.pressOne!=0&&System.currentTimeMillis()-CaptureService.pressOne<500)
					{
						CaptureService.pressOne=System.currentTimeMillis();
						return;
					}
					CaptureService.pressOne=System.currentTimeMillis();
					
					Log.i(TAG, "Scan key down.........");
					if (!SerialPortClass.serialPortHelper.IsOpen()) {
						SerialPortClass.serialPortHelper.OpenSerialPort(SerialPortClass.choosed_serial, SerialPortClass.choosed_buad);
					}
					if(!SerialPortClass.serialPortName.equals(SERIALPORT.comScan))
					{
						SerialPortClass.serialPortName=SERIALPORT.comScan;
						SerialPortClass.serialPortHelper.Write(SerialPortClass.bt_scan);
						SerialPortClass.serialPortHelper.setOnserialportDataReceivedScan(CaptureService.serialPortDataReceivedScan);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					CaptureService.barCodeStr = "";
					CaptureService.barCodeHex = new byte[4096];

					CaptureService.scanGpio.openScan();
				}
			}
		} else if (action.equals("android.intent.action.SCREEN_ON")) {
			Log.i(TAG, "Power off,Close scan modules power.........");
			SerialPortClass.serialPortHelper.OpenSerialPort(
					SerialPortClass.choosed_serial, SerialPortClass.choosed_buad);
			CaptureService.scanGpio.openPower();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CaptureService.scanGpio.closeScan();
		} else if (action.equals("android.intent.action.SCREEN_OFF")) {
			Log.i(TAG, "ACTION_SCREEN_OFF,Close scan modules power.........");
			SerialPortClass.serialPortHelper.CloseSerialPort();
			SerialPortClass.serialPortHelper.OpenSerialPort(
					SerialPortClass.choosed_serial, 115200);
			SerialPortClass.serialPortHelper.CloseSerialPort();
			CaptureService.scanGpio.closePower();
			CaptureService.scanGpio.closeScan();
		} else if (action.equals("android.intent.action.ACTION_SHUTDOWN")) {
			Log.i(TAG, "ACTION_SCREEN_ON,Open scan modules power.........");
			SerialPortClass.serialPortHelper.CloseSerialPort();
			SerialPortClass.serialPortHelper.OpenSerialPort(
					SerialPortClass.choosed_serial, 115200);
			SerialPortClass.serialPortHelper.CloseSerialPort();
			CaptureService.scanGpio.closePower();
			CaptureService.scanGpio.closeScan();
		}
	}
}
