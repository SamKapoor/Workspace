package in.infiniumglobal.infirms.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.infiniumglobal.infirms.R;

/**
 * Created by Hiral on 1/26/2016.
 */
public class CustomerReceiptFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_customer_receipt, container, false);

        return rootView;
    }
}
