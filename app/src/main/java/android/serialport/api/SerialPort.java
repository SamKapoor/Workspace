package android.serialport.api;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialPort {

	private static final String TAG = "SerialPort";
	private ReadThread mReadThread;
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	public byte[] buffer = null;
	public boolean isOpen = false;

	SerialPortDataReceived serialportDataReceived = null;

	public void setOnserialportDataReceived(
			SerialPortDataReceived _serialportDataReceived) {
		this.serialportDataReceived = _serialportDataReceived;
	}

	public boolean RootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

	public boolean open(String device, int baudrate) {
		mFd = open(device, baudrate, 0, SerialPortParam.DataBits,
				SerialPortParam.StopBits, SerialPortParam.Parity,
				SerialPortParam.Flowcontrol);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			return false;
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
		mReadThread = new ReadThread();
		mReadThread.setPriority(Thread.MAX_PRIORITY);
		mReadThread.setPriority(Thread.NORM_PRIORITY);
		mReadThread.start();
		isOpen = true;
		return true;
	}

	public boolean closePort() {
		if (mFd != null) {
			try {
				mReadThread.interrupt();
				close();
				mFd = null;
				mFileInputStream = null;
				mFileOutputStream = null;
				isOpen = false;
			} catch (Exception ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		return true;
	}

	public boolean Write(byte[] buffer) {
		try {
			if (mFileOutputStream == null) {
				return false;
			}
			int sendSize = 100;
			if (buffer.length <= sendSize) {
				mFileOutputStream.write(buffer);
				return true;
			}
			for (int j = 0; j < buffer.length; j += sendSize) {
				byte[] btPackage = new byte[sendSize];
				if (buffer.length - j < sendSize) {
					btPackage = new byte[buffer.length - j];
				}
				System.arraycopy(buffer, j, btPackage, 0, btPackage.length);
				mFileOutputStream.write(btPackage);
				Thread.sleep(10);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public boolean Write(String str) {
		byte[] buffer = str.getBytes();
		return Write(buffer);
	}

	public SerialPort() {
		String device = SerialPortParam.Path;
		String cmd = "chmod 777 " + device + "\n" + "exit\n";
		if (RootCommand(cmd)) {
			System.out.println("ok");
		} else {
			System.out.println("no");
		}
	}

	protected class ReadThread extends Thread {
		private static final String TAG = "ReadThread";

		@Override
		public void run() {
			super.run();

			byte[] buffer = new byte[4096];

			while (true) {
				int size;
				try {
					if (mFileInputStream == null) {
						return;
					}
					int canRead=mFileInputStream.available();
					if (canRead > 0) {
						buffer = new byte[4096];
						size = mFileInputStream.read(buffer);
						if (size > 0) {
							onDataReceived(buffer, size);
							Log.i(TAG, "ReadThread:" + byteToString(buffer, size));
							size=0;
						}
					}
					Thread.sleep(1);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	protected void onDataReceived(final byte[] bufferRec, final int size) {
		buffer = new byte[size];
		System.arraycopy(bufferRec, 0, buffer, 0, size);
		if (serialportDataReceived != null) {
			serialportDataReceived
					.onDataReceivedListener(buffer, size);
		}
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate,
			int flags, int databits2, int stopbits, int parity2, int flowcontrol);

	private native void close();

	static {
		System.loadLibrary("serial_port");
	}

	private static String byteToString(byte[] b, int size) {
		byte high, low;
		byte maskHigh = (byte) 0xf0;
		byte maskLow = 0x0f;

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < size; i++) {
			high = (byte) ((b[i] & maskHigh) >> 4);
			low = (byte) (b[i] & maskLow);
			buf.append(findHex(high));
			buf.append(findHex(low));
			buf.append(" ");
		}

		return buf.toString();
	}

	private static char findHex(byte b) {
		int t = new Byte(b).intValue();
		t = t < 0 ? t + 16 : t;

		if ((0 <= t) && (t <= 9)) {
			return (char) (t + '0');
		}

		return (char) (t - 10 + 'A');
	}

	/**
	 * ���ַ���ʽ��ʾ��ʮ�������ת��Ϊbyte����
	 */
	private static byte[] hexStringToBytes(String hexString) {
		hexString = hexString.toLowerCase();
		String[] hexStrings = hexString.split(" ");
		byte[] bytes = new byte[hexStrings.length];
		for (int i = 0; i < hexStrings.length; i++) {
			char[] hexChars = hexStrings[i].toCharArray();
			bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
		}
		return bytes;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789abcdef".indexOf(c);
	}
}
