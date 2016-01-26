package in.infiniumglobal.infirms.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.model.TBLR_RevenueCustomer;


public class SearchAdapter extends CursorAdapter {

    private Context ctx;
    private LayoutInflater myInflator;
    private Cursor cursor;

    public SearchAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        ctx = context;
        this.cursor = cursor;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_search_view, parent, false);
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, final Context context, final Cursor tempCursor) {
        // Find fields to populate in inflated template
        TextView tvBusinessName = (TextView) view.findViewById(R.id.row_tv_business_name);
        TextView tvCustomerName = (TextView) view.findViewById(R.id.row_tv_customer_name);

        if (tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_BUSINESSNAME)).length() >0)
            tvBusinessName.setText(tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_BUSINESSNAME)));
        if (tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_OWNERNAME)).length() >0)
            tvCustomerName.setText(tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_OWNERNAME)));

        final String id = tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_ID));
        /*view.findViewById(R.id.delete_rule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler dbHandler = DatabaseHandler.getInstance(context);
                if (dbHandler.deleteRule(id) > 0) {
                    Toast.makeText(context, "Rule Deleted.", Toast.LENGTH_SHORT).show();
                    tempCursor.requery();
                    notifyDataSetChanged();
                }
            }
        });*/
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
