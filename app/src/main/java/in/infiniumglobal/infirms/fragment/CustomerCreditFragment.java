package in.infiniumglobal.infirms.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import in.infiniumglobal.infirms.R;

/**
 * Created by Hiral on 1/26/2016.
 */
public class CustomerCreditFragment extends Fragment {

    private EditText edtRemarks, edtPaidAmt;
    private TextView tvDate, tvCustomerName, tvTitle;
    private Button btnSavePrint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_customer_credit, container, false);

        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvCustomerName = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_customer_name);
        tvDate = (TextView) rootView.findViewById(R.id.customer_receipt_frag_tv_date);
        edtPaidAmt = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_paid_amount);
        edtRemarks = (EditText) rootView.findViewById(R.id.customer_receipt_frag_edt_remarks);
        btnSavePrint = (Button) rootView.findViewById(R.id.customer_receipt_frag_btn_save_print);
        return rootView;
    }
}