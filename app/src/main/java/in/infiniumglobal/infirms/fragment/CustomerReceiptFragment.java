package in.infiniumglobal.infirms.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pda3505.helper.printer.PrintService;
import com.pda3505.helper.printer.PrinterClass;
import com.pda3505.printer.PrinterClassSerialPort;

import java.util.ArrayList;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.activity.BaseActivity;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;


/**
 * Created by Hiral on 1/26/2016.
 */
public class CustomerReceiptFragment extends Fragment implements View.OnClickListener {

    private LinearLayout llChequeNumber, llBankName;
    TextView tvBusinessName, tvCustomername, tvReceiptDate, tvOutStanding, tvPayableAmt, tvTotalAmount;
    EditText edtUnitRate, edtTotalUnit, edtAdjustment, edtPaidAmount, edtChequeNumber, edtBankName, edtRemarks;
    RadioGroup radioGroupCollection, radioGroupPaymentType;
    Spinner spinnerUnitType;
    Button btnScanAndPrint;
    public static PrinterClassSerialPort printerClass = null;
    private String TAG = "app";
    private TextView tvPercent;
    private Button btnRePrint;
    private Cursor unitCursor;
    private ArrayList<String> unitList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_customer_receipt, container, false);
        tvPercent = (TextView) rootView.findViewById(R.id.tv_percen);
        llChequeNumber = (LinearLayout) rootView.findViewById(R.id.llChequeNumber);
        llBankName = (LinearLayout) rootView.findViewById(R.id.llBankName);

        tvBusinessName = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_business_name);
        tvCustomername = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_customer_name);
        tvReceiptDate = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_date);
        tvReceiptDate.setText(Common.getCurrentDate("yyyy-MM-dd hh:mm:ss"));
        tvOutStanding = (TextView) rootView.findViewById(R.id.customer_receipt_frag_edt_outstanding);
        tvPayableAmt = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_payable);

        edtUnitRate = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_unit_rate);
        edtTotalUnit = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_total_units);
        tvTotalAmount = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_total_amount);
        edtAdjustment = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_adjustment);
        edtPaidAmount = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_paid_amount);
        edtChequeNumber = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_cheque_number);
        edtBankName = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_bank_name);
        edtRemarks = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_remarks);

        btnScanAndPrint = (Button) rootView.findViewById(R.id.customer_receipt_frag_btn_save_print);
        btnScanAndPrint.setOnClickListener(this);

        spinnerUnitType = (Spinner) rootView.findViewById(R.id.customer_receipt_frag_spinner_unit);

        radioGroupCollection = (RadioGroup) rootView.findViewById(R.id.radioCollectionGroup);
        radioGroupPaymentType = (RadioGroup) rootView.findViewById(R.id.radioGroupPaymentType);

        llBankName.setVisibility(View.GONE);
        llChequeNumber.setVisibility(View.GONE);

        Cursor customerCursor = AppConfig.customerData;
        String businessName = customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_BUSINESSNAME));
        String customerName = customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_OWNERNAME));
        String outstandingAmt = customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_OUTSTANDINGAMT));
        String ownerName = customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_OWNERNAME));
        String businessLicNo = customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_BUSINESSLICNO));
        String tinNo = customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_TINNO));
        String vrnNo = customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_VNRNO));
        String contactNo = customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_CONTACTNO));

        tvBusinessName.setText(businessName);
        tvCustomername.setText(customerName);
        if (outstandingAmt.trim().length() == 0)
            tvOutStanding.setText("0");
        else
            tvOutStanding.setText(outstandingAmt);

        radioGroupPaymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.customer_receipt_frag_rb_cash) {
                    llBankName.setVisibility(View.GONE);
                    llChequeNumber.setVisibility(View.GONE);
                } else if (checkedId == R.id.customer_receipt_frag_rb_cheque) {
                    llBankName.setVisibility(View.VISIBLE);
                    llChequeNumber.setVisibility(View.VISIBLE);
                }
            }
        });

        radioGroupCollection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.customer_receipt_frag_rb_new) {

                } else if (checkedId == R.id.customer_receipt_frag_rb_remaining) {

                }
            }
        });

        btnRePrint = (Button) rootView.findViewById(R.id.customer_receipt_frag_btn_reprint);
        btnRePrint.setOnClickListener(this);
        if (AppConfig.PrintText.length() > 0)
            btnRePrint.setVisibility(View.GONE);
        else
            btnRePrint.setVisibility(View.GONE);

        DatabaseHandler dbHandler = DatabaseHandler.getInstance(getActivity());
        unitCursor = dbHandler.getUnitType(AppConfig.revenueID);
        if (unitCursor != null && unitCursor.getCount() > 0) {
            System.out.println("revenue size:" + unitCursor.getCount());
            unitCursor.moveToFirst();
            while (!unitCursor.isAfterLast()) {
                unitList.add(unitCursor.getString(unitCursor.getColumnIndex(DatabaseHandler.KEY_REVENUEUNIT))); //add the item
                unitCursor.moveToNext();
            }

            ArrayAdapter<String> revenueAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_dropdown_item, unitList);
            revenueAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//            revenueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUnitType.setAdapter(revenueAdapter);
        }

        spinnerUnitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String unitItem = parent.getItemAtPosition(position).toString();

                unitCursor.moveToFirst();
                while (!unitCursor.isAfterLast()) {
                    if (unitCursor.getString(unitCursor.getColumnIndex(DatabaseHandler.KEY_REVENUEUNIT)).equals(unitItem)) {
                        AppConfig.RevenueRateType = unitCursor.getString(unitCursor.getColumnIndex(DatabaseHandler.KEY_REVENUERATETYPE));
                        AppConfig.RevenueRate = unitCursor.getString(unitCursor.getColumnIndex(DatabaseHandler.KEY_REVENUERATE));
                        AppConfig.RevenueRateId = unitCursor.getString(unitCursor.getColumnIndex(DatabaseHandler.KEY_REVENUERATEID));
                        AppConfig.RevenueUnit = unitItem;

                        edtUnitRate.setText(AppConfig.RevenueRate);
                        if (AppConfig.RevenueRateType.equals("A")) {
                            tvPercent.setVisibility(View.GONE);
                            edtTotalUnit.setText("1");
                            tvTotalAmount.setText("" + AppConfig.RevenueRate);
                        } else {
                            tvPercent.setVisibility(View.VISIBLE);
                        }
//                        revenueID = revenueCursor.getInt(revenueCursor.getColumnIndex(DatabaseHandler.KEY_REVENUE_TYPEID));
//                        instantPay = revenueCursor.getString(revenueCursor.getColumnIndex(DatabaseHandler.KEY_INSTANTPAY));
                    }
                    unitCursor.moveToNext();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edtUnitRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                edtTotalUnit.setText("");
                tvTotalAmount.setText("");
                edtPaidAmount.setText("");
                edtAdjustment.setText("");
                tvPayableAmt.setText("");
            }
        });

        edtTotalUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (AppConfig.RevenueRateType.equals("A")) {
                    try {
                        double total = Double.parseDouble(edtUnitRate.getText().toString()) * Double.parseDouble(edtTotalUnit.getText().toString());
                        tvTotalAmount.setText("" + String.format("%.2f", total));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    edtAdjustment.setText("");
                    edtPaidAmount.setText("");
                    tvPayableAmt.setText("");
                } else if (AppConfig.RevenueRateType.equals("P")) {
                    try {
                        double total = Double.parseDouble(edtUnitRate.getText().toString()) * Double.parseDouble(edtTotalUnit.getText().toString());
                        tvTotalAmount.setText("" + String.format("%.2f", total / 100));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        edtAdjustment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    double total = Double.parseDouble(tvTotalAmount.getText().toString());
                    double outstanding = Double.parseDouble(tvOutStanding.getText().toString());
                    double adjustment = Double.parseDouble(edtAdjustment.getText().toString());
                    tvPayableAmt.setText((total + outstanding + adjustment) + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


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
                    case PrinterClass.MESSAGE_STATE_CHANGE:// 6��l��״
                        switch (msg.arg1) {
                            case PrinterClass.STATE_CONNECTED:// �Ѿ�l��
                                break;
                            case PrinterClass.STATE_CONNECTING:// ����l��
                                Toast.makeText(getActivity(),
                                        "STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.STATE_LISTEN:
                            case PrinterClass.STATE_NONE:
                                break;
                            case PrinterClass.SUCCESS_CONNECT:
                                printerClass.write(new byte[]{0x1b, 0x2b});// ����ӡ���ͺ�
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
        return rootView;
    }

    private void ShowMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v == btnScanAndPrint) {
            ContentValues receiptValues = new ContentValues();

            String unitRate = edtUnitRate.getText().toString().trim();
            String totalUnit = edtTotalUnit.getText().toString().trim();
            String totalAmount = tvTotalAmount.getText().toString().trim();
            String payableAmount = tvPayableAmt.getText().toString().trim();
            String adjustment = edtAdjustment.getText().toString().trim();
            String paidAmount = edtPaidAmount.getText().toString().trim();
            String businessName = tvBusinessName.getText().toString().trim();
            String customerName = tvCustomername.getText().toString().trim();
            String remarks = edtRemarks.getText().toString().trim();
            if (AppConfig.RevenueUnit.trim().length() == 0) {
                Common.showAlertDialog(getActivity(), "", "Please select Unit type.", true);
                return;
            }

            String receiptNo = BaseActivity.getReceiptNo(AppConfig.DeviceCode, AppConfig.revenueCode, AppConfig.receiptCode);
            receiptValues.put(DatabaseHandler.KEY_RECEIPTNO, receiptNo);
            AppConfig.receiptCode = receiptNo;

            if (unitRate.length() == 0) {
                Common.showAlertDialog(getActivity(), "", "Please enter Unit rate.", true);
                return;
            } else if (totalUnit.length() == 0) {
                Common.showAlertDialog(getActivity(), "", "Please enter Total unit.", true);
                return;
            } else if (totalAmount.length() == 0) {
                Common.showAlertDialog(getActivity(), "", "Please enter Total amount.", true);
                return;
            } else if (adjustment.length() == 0) {
                Common.showAlertDialog(getActivity(), "", "Please enter adjustment.", true);
                return;
            } else if (paidAmount.length() == 0) {
                Common.showAlertDialog(getActivity(), "", "Please enter Paid amount.", true);
                return;
            } else if (Double.parseDouble(paidAmount) > Double.parseDouble(payableAmount)) {
                Common.showAlertDialog(getActivity(), "", "Paid amount must be less than total amount.", true);
                return;
            }


//            receiptValues.put(DatabaseHandler.KEY_, unitRate);
            receiptValues.put(DatabaseHandler.KEY_REVENUE_TYPEID, AppConfig.revenueID);
            if (!businessName.isEmpty())
                receiptValues.put(DatabaseHandler.KEY_BUSINESSNAME, businessName);
            if (!customerName.isEmpty())
                receiptValues.put(DatabaseHandler.KEY_CUSTOMERNAME, customerName);

            receiptValues.put(DatabaseHandler.KEY_RRECEIPTDATE, Common.getCurrentDate("yyyy-MM-dd hh:mm:ss"));
            receiptValues.put(DatabaseHandler.KEY_REVENUERATEID, AppConfig.RevenueRateId);
            receiptValues.put(DatabaseHandler.KEY_REVENUERATE, unitRate);
            receiptValues.put(DatabaseHandler.KEY_TOTALUNIT, totalUnit);
            receiptValues.put(DatabaseHandler.KEY_TOTALAMOUNT, totalAmount);
            receiptValues.put(DatabaseHandler.KEY_ADJUSTMENTAMT, adjustment);
            receiptValues.put(DatabaseHandler.KEY_PAIDAMOUNT, paidAmount);
            receiptValues.put(DatabaseHandler.KEY_DeviceCode, AppConfig.DeviceCode);

            if (llBankName.getVisibility() == View.VISIBLE) {
                String chequeNumber = edtChequeNumber.getText().toString().trim();
                String bankName = edtBankName.getText().toString().trim();


                if (chequeNumber.length() == 0) {
                    Common.showAlertDialog(getActivity(), "", "Please enter Cheque Number.", true);
                    return;
                } else if (bankName.length() == 0) {
                    Common.showAlertDialog(getActivity(), "", "Please enter Bank Name.", true);
                    return;
                }
                /*else if (remarks.length() == 0) {
                    Common.showAlertDialog(getActivity(), "", "Please enter Remark.", true);
                    return;
                }*/
                receiptValues.put(DatabaseHandler.KEY_CHEQUENO, chequeNumber);
                receiptValues.put(DatabaseHandler.KEY_BANKNAME, bankName);


                receiptValues.put(DatabaseHandler.KEY_PAYTYPE, "Cheque");
            } else {
                receiptValues.put(DatabaseHandler.KEY_PAYTYPE, "Cash");
            }

            if (remarks.length() > 0)
                receiptValues.put(DatabaseHandler.KEY_PAYREMARKS, remarks);

            receiptValues.put(DatabaseHandler.KEY_CREATEDBY, Common.getStringPrefrences(getActivity(), getString(R.string.pref_userId), getString(R.string.app_name)));
            receiptValues.put(DatabaseHandler.KEY_CREATEDDATE, Common.getCurrentDate("yyyy-MM-dd hh:mm:ss"));

            receiptValues.put(DatabaseHandler.KEY_LOCATIONID, AppConfig.locationID);
            receiptValues.put(DatabaseHandler.KEY_AREAID, AppConfig.areaID);

            DatabaseHandler dbHandler = DatabaseHandler.getInstance(getActivity());

            ContentValues values = new ContentValues();
            values.put(DatabaseHandler.KEY_RECEIPTCODE, receiptNo);
            dbHandler.updateRevenues(AppConfig.revenueID + "", values);

            long rowId = dbHandler.addData(DatabaseHandler.TABLE_TBLR_RevenueReceipt, receiptValues);
            Common.showAlertDialog(getActivity(), "", "Data saved. " + receiptNo, true);
            String printing = "";
            printing += "NYANG'HWALE DISTRICT COUNCIL";
            printing += "\nP O BOX 352,NYANG'HWALE-GEITA";
            printing += "\n________________________\n";
            printing += "\n\t\t" + AppConfig.revenueItem.toUpperCase();

            printing += "\n________________________\n";
            printing += "\nRECEIPT NO:" + receiptNo + "  \n" + Common.getCurrentDate("dd-MM-yy hh:mm");
            if (businessName.length() > 0)
                printing += "\n\t\t" + businessName.toUpperCase();
            if (customerName.length() > 0)
                printing += "\n\t\t" + customerName.toUpperCase();

            if (tvPercent.getVisibility() == View.VISIBLE)
                printing += "\n" + AppConfig.RevenueUnit + "\t " + totalUnit + " * " + unitRate + "%\t " + totalAmount;
            else
                printing += "\n" + AppConfig.RevenueUnit + "\t " + unitRate + " * " + totalUnit + "\t " + totalAmount;
            if (llBankName.getVisibility() == View.VISIBLE) {
                printing += "\nPaid Amt: " + paidAmount + "\t Cheque : " + edtChequeNumber.getText().toString().trim();
            } else {
                printing += "\nPaid Amt: " + paidAmount + "\t Cash";
            }
            printing += "\n________________________\n";
            printing += "\n\t\t\tTHANK YOU\n";
            printing += "\n________________________\n\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n";

            if (printing.length() > 0) {
                AppConfig.PrintText = printing;
                Bitmap btMap = Common.drawableTobitmap(getActivity(), R.drawable.login_logo);
                printerClass.printImage(btMap);
                boolean printed = printerClass.printText(printing);
                Common.showAlertDialog(getActivity(), "", "Printed . " + printed, true);
                btnRePrint.setVisibility(View.GONE);
            }

            tvReceiptDate.setText(Common.getCurrentDate("yyyy-MM-dd hh:mm:ss"));
//            edtName.setText("");
            edtTotalUnit.setText("");
            tvTotalAmount.setText("");
            edtPaidAmount.setText("");
            edtBankName.setText("");
            edtChequeNumber.setText("");
            edtRemarks.setText("");

        } else if (v == btnRePrint) {
            boolean printed = printerClass.printText(AppConfig.PrintText);
        }
    }
}