package in.infiniumglobal.infirms.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pda3505.helper.printer.PrintService;
import com.pda3505.helper.printer.PrinterClass;
import com.pda3505.printer.PrinterClassSerialPort;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.adapter.HistoryAdapter;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;

/**
 * Created by Hiral on 1/26/2016.
 */
public class CustomerHistoryFragment extends Fragment {

    private ListView lvHistory;
    private DatabaseHandler dbHandler;
    private TextView tvTotalAmount;
    private TextView tvPaidAmount;

    protected static final String TAG = "Print_Application";
    public static PrinterClassSerialPort printerClass = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_customer_history, container, false);

        Handler mhandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PrinterClass.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        Log.i(TAG, "readBuf:" + readBuf[0]);
                        if (readBuf[0] == 0x13) {
                            // PrintService.isFUll = true;
                            // ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_bufferfull));
                        } else if (readBuf[0] == 0x11) {
                            // PrintService.isFUll = false;
                            // ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_buffernull));
                        } else if (readBuf[0] == 0x08) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_nopaper));
                        } else if (readBuf[0] == 0x01) {
                            // ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
                        } else if (readBuf[0] == 0x04) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_hightemperature));
                        } else if (readBuf[0] == 0x02) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_lowpower));
                        } else {
                            String readMessage = new String(readBuf, 0, msg.arg1);
                            if (readMessage.contains("800"))// 80mm paper
                            {
                                PrintService.imageWidth = 72;
                                Toast.makeText(getActivity(), "80mm",
                                        Toast.LENGTH_SHORT).show();
                            } else if (readMessage.contains("580"))// 58mm paper
                            {
                                PrintService.imageWidth = 48;
                                Toast.makeText(getActivity(), "58mm",
                                        Toast.LENGTH_SHORT).show();
                            } else {

                            }
                        }
                        break;
                    case PrinterClass.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case PrinterClass.STATE_CONNECTED:
                                break;
                            case PrinterClass.STATE_CONNECTING:
                                Toast.makeText(getActivity(),
                                        "STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.STATE_LISTEN:
                            case PrinterClass.STATE_NONE:
                                break;
                            case PrinterClass.SUCCESS_CONNECT:
                                printerClass.write(new byte[]{0x1b, 0x2b});
                                Toast.makeText(getActivity(),
                                        "SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.FAILED_CONNECT:
                                Toast.makeText(getActivity(),
                                        "FAILED_CONNECT", Toast.LENGTH_SHORT).show();

                                break;
                            case PrinterClass.LOSE_CONNECT:
                                Toast.makeText(getActivity(), "LOSE_CONNECT",
                                        Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case PrinterClass.MESSAGE_WRITE:

                        break;
                }
                super.handleMessage(msg);
            }
        };
        printerClass = new PrinterClassSerialPort(mhandler);
        printerClass.open(getActivity());

        tvTotalAmount = (TextView) rootView.findViewById(R.id.history_tv_totalAmt);
        tvPaidAmount = (TextView) rootView.findViewById(R.id.history_tv_paidAmt);
        lvHistory = (ListView) rootView.findViewById(R.id.history_lv_receipt);
        dbHandler = DatabaseHandler.getInstance(getActivity());
        Cursor totalAmountCursor = dbHandler.getRevenueReceiptTotalAmount(AppConfig.revenueID);
        if (totalAmountCursor != null && totalAmountCursor.getCount() > 0) {
            totalAmountCursor.moveToFirst();
            try {
                String total[] = totalAmountCursor.getString(totalAmountCursor.getColumnIndex("Total")).split("-");
                tvTotalAmount.setText("Total Amt \n" + total[0]);
                tvPaidAmount.setText("Paid Amt \n" + total[1]);
            } catch (Exception e) {
                e.printStackTrace();
                tvTotalAmount.setText("Total Amt ");
                tvPaidAmount.setText("Paid Amt ");
            }
        }
        Cursor historyCursor = dbHandler.getRevenueReceipt(AppConfig.revenueID);

        HistoryAdapter historyAdapter = new HistoryAdapter(getActivity(), historyCursor, 0);
        lvHistory.setAdapter(historyAdapter);

        lvHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog alertDialog = null;
                if (alertDialog == null)
                    alertDialog = new AlertDialog.Builder(getActivity()).create();
                // Setting Dialog Title
                alertDialog.setTitle("Print Receipt");
                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want to print receipt?");
                // Setting OK Button
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor tempCursor = ((HistoryAdapter) parent.getAdapter()).getCursor();
                        tempCursor.moveToPosition(position);
                        String printing = "";
                        printing += "NYANG'HWALE DISTRICT COUNCIL";
                        printing += "\nP O BOX 352,NYANG'HWALE-GEITA";
                        printing += "\n________________________\n";
                        printing += "\n\t\t" + AppConfig.revenueItem.toUpperCase();

                        printing += "\n________________________\n";
                        printing += "\nRECEIPT NO:" + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_ID)) + "  " + Common.getCurrentDate("dd-MM-yy hh:mm");
                        if (tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CUSTOMERNAME)).length() > 0)
                            printing += "\n\t\t" + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CUSTOMERNAME)).toUpperCase();

                        if (AppConfig.RevenueRateType.equals("A"))
                            printing += "\n" + AppConfig.RevenueUnit + "\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_REVENUERATE)) + " * " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALUNIT)) + "%\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALAMOUNT));
                        else
                            printing += "\n" + AppConfig.RevenueUnit + "\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALUNIT)) + " * " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_REVENUERATE)) + "\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALAMOUNT));
                        if (tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PAYTYPE)).equals("Cheque")) {
                            printing += "\nPaid Amt: " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PAIDAMOUNT)) + "\t Cheque : " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CHEQUENO));
                        } else {
                            printing += "\nPaid Amt: " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PAIDAMOUNT)) + "\t Cash";
                        }
                        printing += "\n________________________\n";
                        printing += "\n\t\t\tTHANK YOU\n";
                        printing += "\n________________________\n\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "\n";
                        print(printing);
                        dialog.dismiss();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
                return true;
            }
        });

        return rootView;
    }


    public void print(String printText) {
//        Log.e(TAG, "print: " + printText);
        Bitmap btMap = Common.drawableTobitmap(getActivity(), R.drawable.login_logo);
        printerClass.printImage(btMap);
        printerClass.printText(printText); //TODO remove for printing start
    }

    private void ShowMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
