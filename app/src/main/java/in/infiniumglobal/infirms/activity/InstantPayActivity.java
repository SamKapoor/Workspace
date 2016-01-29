package in.infiniumglobal.infirms.activity;

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

public class InstantPayActivity extends BaseActivity {

    private LinearLayout llChequeNumber, llBankName;
    TextView tvBusinessName, tvCustomername, tvReceiptDate;
    EditText edtOutStanding, edtUnitRate, edtTotalUnit, edtTotalAmount, edtAdjustment, edtPaidAmount, edtChequeNumber, edtBankName, edtRemarks;
    RadioGroup radioGroupCollection, radioGroupPaymentType;
    Spinner spinnerUnitType;
    Button btnScanAndPrint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_pay);

        llChequeNumber = (LinearLayout) findViewById(R.id.llChequeNumber);
        llBankName = (LinearLayout) findViewById(R.id.llBankName);

        tvBusinessName = (TextView) findViewById(R.id.customer_receipt_frag_tv_business_name);
        tvCustomername = (TextView) findViewById(R.id.customer_receipt_frag_tv_customer_name);
        tvReceiptDate = (TextView) findViewById(R.id.customer_receipt_frag_tv_date);

        edtUnitRate = (EditText) findViewById(R.id.customer_receipt_frag_edt_unit_rate);
        edtTotalUnit = (EditText) findViewById(R.id.customer_receipt_frag_edt_total_units);
        edtOutStanding = (EditText) findViewById(R.id.customer_receipt_frag_edt_outstanding);
        edtTotalAmount = (EditText) findViewById(R.id.customer_receipt_frag_edt_total_amount);
        edtAdjustment = (EditText) findViewById(R.id.customer_receipt_frag_edt_adjustment);
        edtPaidAmount = (EditText) findViewById(R.id.customer_receipt_frag_edt_paid_amount);
        edtChequeNumber = (EditText) findViewById(R.id.customer_receipt_frag_edt_cheque_number);
        edtBankName = (EditText) findViewById(R.id.customer_receipt_frag_edt_bank_name);
        edtRemarks = (EditText) findViewById(R.id.customer_receipt_frag_edt_remarks);

        btnScanAndPrint = (Button) findViewById(R.id.customer_receipt_frag_btn_save_print);

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

}
