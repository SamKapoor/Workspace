package com.pda3505.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.serialport.api.SerialPortClass;
import android.serialport.api.SerialPortDataReceivedScan;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import in.infiniumglobal.infirms.R;
import com.smartdevicesdk.io.ScanGpio;

public class CaptureService extends Service {
	public static SharedPreferences sharedPreferences = null;
	public static String str_encoding = "UTF-8";

	static Context context;
	private static final String TAG = "CaptureService";
	public static ServiceBeepManager beepManager;
	public static SerialPortDataReceivedScan serialPortDataReceivedScan = null;

	public static String batLevel = "Loading...";
	public static double batValue = 100;
	

	public static long pressOne=0;

	// ��Ļ״̬�㲥
	RemoteControlReceiver screenStatusReceiver;

	/**
	 * һάͷ�ָ�������
	 */
	public static byte[] defaultSetting1D = new byte[] { 0x04, (byte) 0xC8,
			0x04, 0x00, (byte) 0xFF, 0x30 };// ������

	/**
	 * һάͷ��ݸ�ʽ
	 */
	public static byte[] dataTypeFor1D = new byte[] { 0x07, (byte) 0xC6, 0x04,
			0x08, 0x00, (byte) 0xEB, 0x07, (byte) 0xFE, 0x35 };// ��ȡ��ݸ�ʽ

	/**
	 * �޸Ĳ�����Ϊ115200
	 */
	public static byte[] dataBandRate = new byte[] { 0x07, (byte) 0xC6, 0x04,
			0x08, 0x00, (byte) 0x9C, 0x0A, (byte) 0xFE, (byte) 0x81 };

	/**
	 * ��άͷ�ָ�������
	 */
	public static byte[] defaultSetting2D = new byte[] { 0x16, 0x4D, 0x0D,
			0x25, 0x25, 0x25, 0x44, 0x45, 0x46, 0x2E };

	/**
	 * ��άͷ��ݸ�ʽ
	 */
	public static byte[] dataTypeFor2D = new byte[] { 0x16, 0x4D, 0x0D, 0x38,
			0x32, 0x30, 0x32, 0x44, 0x30, 0x31, 0x2E };

	public static ScanGpio scanGpio = new ScanGpio();

	public static String barCodeStr = "";
	public static byte[] barCodeHex = new byte[4096];
	public static int barCodeLen = 0;

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "CaptureService onBind");
		return null;
	}

	/**
	 * ��byte����ת��Ϊ�ַ���ʽ��ʾ��ʮ��������鿴
	 */
	public static StringBuffer bytesToString(byte[] bytes, int size) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < size; i++) {
			String s = Integer.toHexString(bytes[i] & 0xff);
			if (s.length() < 2)
				sBuffer.append('0');
			sBuffer.append(s + " ");
		}
		return sBuffer;
	}

	public static boolean RootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			// process.waitFor();
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				Thread.sleep(10);
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "CaptureService onCreate");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		context = this;
		Log.v(TAG, "CaptureService onStart");

		beepManager = new ServiceBeepManager(this);
		beepManager.updatePrefs();

		serialPortDataReceivedScan = new SerialPortDataReceivedScan() {
			@Override
			public void onDataReceivedListener(byte[] buffer, int size) {
				if (size < 1) {
					return;
				}

				// Check Battery
				/*
				 * if (size == 6 && (buffer[0] == (byte) 0xaa &&
				 * buffer[buffer[1] + 2] == (byte) 0x55)) { batValue = buffer[3]
				 * * 256 + buffer[4]; new Thread(new Runnable() {
				 * 
				 * @Override public void run() { ShowBatState(batValue); }
				 * }).start(); return; } else {
				 */
				System.arraycopy(buffer, 0, barCodeHex, barCodeLen, size);
				barCodeLen += size;

				if ((barCodeLen>1&&barCodeHex[barCodeLen - 2] == 13 && barCodeHex[barCodeLen - 1] == 10)||barCodeLen>1&&barCodeHex[barCodeLen - 2] == 10 && barCodeHex[barCodeLen - 1] == 13) {
					try {
						if (beepManager != null) {
							beepManager.playBeepSoundAndVibrate();
						}

						byte[] btdata = new byte[barCodeLen];
						System.arraycopy(barCodeHex, 0, btdata, 0,
								btdata.length);

						barCodeStr = new String(btdata, 0, btdata.length,
								CaptureService.str_encoding);
						barCodeStr = barCodeStr.trim();

						Log.i(TAG,
								"BarCode2:"
										+ bytesToString(barCodeHex, barCodeLen));
						Log.i(TAG, "BarCode3:" + barCodeStr);

						if (barCodeStr != "") {
							Intent intentBroadcast = new Intent();
							intentBroadcast.setAction("com.zkc.scancode");
							intentBroadcast.putExtra("code", barCodeStr);
							context.sendBroadcast(intentBroadcast);
						}
						barCodeStr = "";
						barCodeHex = new byte[4096];
						barCodeLen = 0;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// }
			}
		};

		scanGpio.openPower();

		screenStatusReceiver = new RemoteControlReceiver();
		IntentFilter screenStatusIF = new IntentFilter();
		screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
		screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
		screenStatusIF.addAction(Intent.ACTION_SHUTDOWN);
		// screenStatusIF.addAction("com.zkc.Receiver.RemoteControlReceiver");
		// register
		registerReceiver(screenStatusReceiver, screenStatusIF);

		// Check BATTERY
		// new ThreadCheckBat().start();

		sharedPreferences = getSharedPreferences("com.zkc.barcodescan",
				Activity.MODE_PRIVATE);
		if (sharedPreferences != null) {
			String str = CaptureService.sharedPreferences.getString("encoding",
					"");
			if (str != null && str != "") {
				CaptureService.str_encoding = str;
			}
		}
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "CaptureService onDestroy");
		scanGpio.closePower();
		unregisterReceiver(screenStatusReceiver);
		super.onDestroy();
	}

	// Check BATTERY
	private class ThreadCheckBat extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				SerialPortClass.serialPortHelper.Write(new byte[] { 0x1b, 0x26,
						0x03 });
				try {
					Thread.sleep(1000 * 5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	static boolean level_20 = false;
	static boolean level_other = false;
	static List<Object> batList = new ArrayList<Object>();

	private static void ShowBatState(double batValue) {
		if (batValue >= 7000 && batList.size() < 10) {
			batList.add(batValue);
		} else if (batList.size() == 10) {
			int maxLoc = -1;
			int minLoc = batList.size();
			double maxValue = 7000;
			double minValue = 8400;
			for (int i = 0; i < batList.size(); i++) {
				Object obj = batList.get(i);
				if (Double.parseDouble(obj.toString()) > maxValue) {
					maxLoc = i;
					maxValue = Double.parseDouble(obj.toString());
				} else if (Double.parseDouble(obj.toString()) < minValue) {
					minLoc = i;
					minValue = Double.parseDouble(obj.toString());
				}
			}

			if (maxLoc < batList.size()) {
				batList.remove(maxLoc);
			}
			if (minLoc < batList.size()) {
				batList.remove(minLoc);
			}
			double batTotal = 0;
			for (int i = 0; i < batList.size(); i++) {
				batTotal += Double.parseDouble(batList.get(i).toString());
			}
			double batAvg = batTotal / batList.size();

			batValue = batAvg;

			double bat = (batValue - 7000) % 1400 * 0.01;
			batLevel = String.format("%.0f", bat) + "%";
			try {
				if (bat <= 20 && !level_20) {
					level_20 = true;
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.str_bat_tip)
									+ batLevel, 2000).show();
				} else if (!level_other) {
					level_other = true;
					Toast.makeText(
							context,
							context.getResources().getString(
									R.string.str_bat_tip)
									+ batLevel, 2000).show();
				} else if (bat < 5) {
				}
			} catch (Exception ex) {
			}
		}
	}

}