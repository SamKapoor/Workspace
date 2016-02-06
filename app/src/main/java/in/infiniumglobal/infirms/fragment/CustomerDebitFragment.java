package in.infiniumglobal.infirms.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_customer_credit, container, false);

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
            dbHandler.addData(DatabaseHandler.TABLE_TBLR_Adjustment, values);

            edtPaidAmt.setText("");
            edtRemarks.setText("");
        }
    }
}