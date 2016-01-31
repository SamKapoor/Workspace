package in.infiniumglobal.infirms.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;

/**
 * Created by Hiral on 1/26/2016.
 */
public class CustomerDetailsFragment extends Fragment {

    TextView tvBusinessName, tvCustomerNo, tvOwnerName, tvBusinessLICNo, tvTINNo, tvVRNo, tvOutstandingAmount, tvContactNo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_customer_details, container, false);

        tvBusinessName = (TextView) rootView.findViewById(R.id.customer_detail_frag_tv_businessName);
        tvCustomerNo = (TextView) rootView.findViewById(R.id.customer_detail_frag_tv_number);
        tvOwnerName = (TextView) rootView.findViewById(R.id.customer_detail_frag_tv_ownerName);
        tvBusinessLICNo = (TextView) rootView.findViewById(R.id.customer_detail_frag_tv_businessLic);
        tvTINNo = (TextView) rootView.findViewById(R.id.customer_detail_frag_tv_tinNo);
        tvVRNo = (TextView) rootView.findViewById(R.id.customer_detail_frag_tv_vnrNo);
        tvOutstandingAmount = (TextView) rootView.findViewById(R.id.customer_detail_frag_tv_OutstandingAmt);
        tvContactNo = (TextView) rootView.findViewById(R.id.customer_detail_frag_tv_ContactNo);


        DatabaseHandler dbDatabaseHandler = DatabaseHandler.getInstance(getActivity());
        Cursor customerCursor = dbDatabaseHandler.getCustomer(AppConfig.customerID + "");
        if (customerCursor != null && customerCursor.getCount() > 0) {
            customerCursor.moveToFirst();
            tvBusinessName.setText("Business : " + customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_BUSINESSNAME)));
            tvCustomerNo.setText("Customer No. : " + customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_CUSTOMERNO)));
            tvOwnerName.setText("Name : " + customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_OWNERNAME)));
            tvBusinessLICNo.setText("Business LIC No. : " + customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_BUSINESSLICNO)));
            tvTINNo.setText("TIN No. : " + customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_TINNO)));
            tvVRNo.setText("VNR No.  : " + customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_VNRNO)));
            tvOutstandingAmount.setText("Outstanding Amount : " + customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_OUTSTANDINGAMT)));
            tvContactNo.setText("Contact No. : " + customerCursor.getString(customerCursor.getColumnIndex(DatabaseHandler.KEY_CONTACTNO)));
        }


        return rootView;
    }
}
