package in.infiniumglobal.infirms.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pda3505.helper.printer.PrintService;
import com.pda3505.helper.printer.PrinterClass;
import com.pda3505.printer.PrinterClassSerialPort;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;

/**
 * Created by Hiral on 1/26/2016.
 */
public class CustomerDebitFragment extends Fragment implements View.OnClickListener {

    private EditText edtRemarks, edtPaidAmt;
    private TextView tvDate, tvCustomerName, tvTitle;
    private Button btnSavePrint;
    protected static final String TAG = "Print_Application";
    public static PrinterClassSerialPort printerClass = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_customer_credit, container, false);

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
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvTitle.setText("Debit Note");
        tvCustomerName = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_customer_name);
        tvCustomerName.setText(AppConfig.customerData.getString(AppConfig.customerData.getColumnIndex(DatabaseHandler.KEY_OWNERNAME)));
        tvDate = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_date);
        tvDate.setText(Common.getCurrentDate("dd-MM-yy hh:mm"));
        edtPaidAmt = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_paid_amount);
        edtRemarks = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_remarks);
        btnSavePrint = (Button) rootView.findViewById(R.id.customer_receipt_frag_btn_save_print);

        btnSavePrint.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == btnSavePrint) {
            String remarks = edtRemarks.getText().toString().trim();
            String amount = edtPaidAmt.getText().toString().trim();
            if (amount.length() == 0) {
                Common.showAlertDialog(getActivity(), "", "Please enter amount", true);
                return;
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHandler.KEY_CUSTOMERID, AppConfig.customerID);
            values.put(DatabaseHandler.KEY_REVENUE_TYPEID, AppConfig.revenueID);
            values.put(DatabaseHandler.KEY_ADJUSTMENTDATE, AppConfig.revenueID);
            values.put(DatabaseHandler.KEY_ADJUSTMENTTYPE, "D");
            values.put(DatabaseHandler.KEY_CREATEDBY, Common.getStringPrefrences(getActivity(), getString(R.string.pref_userId), getString(R.string.app_name)));
            values.put(DatabaseHandler.KEY_CREATEDDATE, Common.getCurrentDate("yyyy-MM-dd hh:mm:ss"));
            if (remarks.length() > 0) {
                values.put(DatabaseHandler.KEY_REMARKS, remarks);
            }
            values.put(DatabaseHandler.KEY_ADJUSTMENTAMT, amount);
            DatabaseHandler dbHandler = DatabaseHandler.getInstance(getActivity());
            long row = dbHandler.addData(DatabaseHandler.TABLE_TBLR_Adjustment, values);

            String printing = "";
            printing += "NYANG'HWALE DISTRICT COUNCIL";
            printing += "\nP O BOX 352,NYANG'HWALE-GEITA";
            printing += "\n________________________\n";
            printing += "\n\t\t" + AppConfig.revenueItem.toUpperCase();

//            printing += "\n________________________\n";
//            printing += "\nAdjustment NO:" + row + "  \n" + Common.getCurrentDate("dd-MM-yy hh:mm");
//            if (businessName.length() > 0)
//                printing += "\n\t\t" + businessName.toUpperCase();
//            if (customerName.length() > 0)
//                printing += "\n\t\t" + customerName.toUpperCase();
//
//            if (tvPercent.getVisibility() == View.VISIBLE)
//                printing += "\n" + AppConfig.RevenueUnit + "\t " + totalUnit + " * " + unitRate + "%\t " + totalAmount;
//            else
//                printing += "\n" + AppConfig.RevenueUnit + "\t " + unitRate + " * " + totalUnit + "\t " + totalAmount;
//            if (llBankName.getVisibility() == View.VISIBLE) {
//                printing += "\nPaid Amt: " + paidAmount + "\t Cheque : " + edtChequeNumber.getText().toString().trim();
//            } else {
//                printing += "\nPaid Amt: " + paidAmount + "\t Cash";
//            }
//            printing += "\n________________________\n";
//            printing += "\n\t\t\tTHANK YOU\n";
//            printing += "\n________________________\n\n" +
//                    "\n" +
//                    "\n" +
//                    "\n" +
//                    "\n";
//
//            if (printing.length() > 0) {
//                AppConfig.PrintText = printing;
//                Bitmap btMap = Common.drawableTobitmap(getActivity(), R.drawable.login_logo);
//                printerClass.printImage(btMap);
//                boolean printed = printerClass.printText(printing);
//                Common.showAlertDialog(getActivity(), "", "Printed . " + printed, true);
//                btnRePrint.setVisibility(View.GONE);
//            }

            edtPaidAmt.setText("");
            edtRemarks.setText("");
        }
    }

    private void ShowMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}