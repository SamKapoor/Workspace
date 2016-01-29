package in.infiniumglobal.infirms.activity;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.Common;

public class InstantPayActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llChequeNumber, llBankName;
    TextView /*tvBusinessName, tvCustomername,*/ tvReceiptDate;
    EditText /*edtOutStanding,*/ edtUnitRate, edtTotalUnit, edtTotalAmount, /*edtAdjustment,*/
            edtPaidAmount, edtChequeNumber, edtBankName, edtRemarks;
    RadioGroup radioGroupCollection, radioGroupPaymentType;
    Spinner spinnerUnitType;
    Button btnScanAndPrint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_pay);

        llChequeNumber = (LinearLayout) findViewById(R.id.llChequeNumber);
        llBankName = (LinearLayout) findViewById(R.id.llBankName);

//        tvBusinessName = (TextView) findViewById(R.id.customer_receipt_frag_tv_business_name);
//        tvCustomername = (TextView) findViewById(R.id.customer_receipt_frag_tv_customer_name);
        tvReceiptDate = (TextView) findViewById(R.id.customer_receipt_frag_tv_date);
        tvReceiptDate.setText(Common.getCurrentDate("dd:MM:yyyy"));

        edtUnitRate = (EditText) findViewById(R.id.customer_receipt_frag_edt_unit_rate);
        edtTotalUnit = (EditText) findViewById(R.id.customer_receipt_frag_edt_total_units);
//        edtOutStanding = (EditText) findViewById(R.id.customer_receipt_frag_edt_outstanding);
        edtTotalAmount = (EditText) findViewById(R.id.customer_receipt_frag_edt_total_amount);
//        edtAdjustment = (EditText) findViewById(R.id.customer_receipt_frag_edt_adjustment);
        edtPaidAmount = (EditText) findViewById(R.id.customer_receipt_frag_edt_paid_amount);
        edtChequeNumber = (EditText) findViewById(R.id.customer_receipt_frag_edt_cheque_number);
        edtBankName = (EditText) findViewById(R.id.customer_receipt_frag_edt_bank_name);
        edtRemarks = (EditText) findViewById(R.id.customer_receipt_frag_edt_remarks);

        btnScanAndPrint = (Button) findViewById(R.id.customer_receipt_frag_btn_save_print);
        btnScanAndPrint.setOnClickListener(this);

        spinnerUnitType = (Spinner) findViewById(R.id.customer_receipt_frag_spinner_unit);

        radioGroupCollection = (RadioGroup) findViewById(R.id.radioCollectionGroup);
        radioGroupPaymentType = (RadioGroup) findViewById(R.id.radioGroupPaymentType);

        llBankName.setVisibility(View.GONE);
        llChequeNumber.setVisibility(View.GONE);

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


    }

    @Override
    public void onClick(View v) {
        if (v == btnScanAndPrint) {
            ContentValues receiptValues = new ContentValues();

            String unitRate = edtUnitRate.getText().toString().trim();
            String totalUnit = edtTotalUnit.getText().toString().trim();
            String totalAmount = edtTotalAmount.getText().toString().trim();
            String paidAmount = edtPaidAmount.getText().toString().trim();

            if (unitRate.length() == 0) {
                Common.showAlertDialog(this, "", "Please enter Unit rate.", true);
                return;
            } else if (totalUnit.length() == 0) {
                Common.showAlertDialog(this, "", "Please enter Total unit.", true);
                return;
            } else if (totalAmount.length() == 0) {
                Common.showAlertDialog(this, "", "Please enter Total amount.", true);
                return;
            } else if (paidAmount.length() == 0) {
                Common.showAlertDialog(this, "", "Please enter Paid amount.", true);
                return;
            }

//            receiptValues.put(DatabaseHandler.KEY_, unitRate);
            receiptValues.put(DatabaseHandler.KEY_TOTALUNIT, totalUnit);
            receiptValues.put(DatabaseHandler.KEY_TOTALAMOUNT, totalAmount);
            receiptValues.put(DatabaseHandler.KEY_PAIDAMOUNT, paidAmount);


            if (llBankName.getVisibility() == View.VISIBLE) {
                String chequeNumber = edtChequeNumber.getText().toString().trim();
                String bankName = edtBankName.getText().toString().trim();
                String remarks = edtRemarks.getText().toString().trim();

                if (chequeNumber.length() == 0) {
                    Common.showAlertDialog(this, "", "Please enter Cheque Number.", true);
                    return;
                } else if (bankName.length() == 0) {
                    Common.showAlertDialog(this, "", "Please enter Bank Name.", true);
                    return;
                } else if (remarks.length() == 0) {
                    Common.showAlertDialog(this, "", "Please enter Remark.", true);
                    return;
                }
                receiptValues.put(DatabaseHandler.KEY_CHEQUENO, chequeNumber);
                receiptValues.put(DatabaseHandler.KEY_BANKNAME, bankName);
                receiptValues.put(DatabaseHandler.KEY_REMARKS, remarks);
            }

            DatabaseHandler dbHandler = DatabaseHandler.getInstance(InstantPayActivity.this);

            dbHandler.addData(DatabaseHandler.TABLE_TBLR_RevenueReceipt, receiptValues);
        }
    }
}
