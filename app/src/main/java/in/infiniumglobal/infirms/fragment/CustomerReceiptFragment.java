package in.infiniumglobal.infirms.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import in.infiniumglobal.infirms.R;


/**
 * Created by Hiral on 1/26/2016.
 */
public class CustomerReceiptFragment extends Fragment {

    private LinearLayout llChequeNumber, llBankName;
    TextView tvBusinessName, tvCustomername, tvReceiptDate;
    EditText edtOutStanding, edtUnitRate, edtTotalUnit, edtTotalAmount, edtAdjustment, edtPaidAmount, edtChequeNumber, edtBankName, edtRemarks;
    RadioGroup radioGroupCollection, radioGroupPaymentType;
    Spinner spinnerUnitType;
    Button btnScanAndPrint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_customer_receipt, container, false);
        llChequeNumber = (LinearLayout) rootView.findViewById(R.id.llChequeNumber);
        llBankName = (LinearLayout) rootView.findViewById(R.id.llBankName);

        tvBusinessName = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_business_name);
        tvCustomername = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_customer_name);
        tvReceiptDate = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_date);

        edtUnitRate = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_unit_rate);
        edtTotalUnit = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_total_units);
        edtOutStanding = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_outstanding);
        edtTotalAmount = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_total_amount);
        edtAdjustment = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_adjustment);
        edtPaidAmount = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_paid_amount);
        edtChequeNumber = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_cheque_number);
        edtBankName = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_bank_name);
        edtRemarks = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_remarks);

        btnScanAndPrint = (Button) rootView.findViewById(R.id.customer_receipt_frag_btn_save_print);

        spinnerUnitType = (Spinner) rootView.findViewById(R.id.customer_receipt_frag_spinner_unit);

        radioGroupCollection = (RadioGroup) rootView.findViewById(R.id.radioCollectionGroup);
        radioGroupPaymentType = (RadioGroup) rootView.findViewById(R.id.radioGroupPaymentType);

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

        return rootView;
    }
}