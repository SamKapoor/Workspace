package in.infiniumglobal.infirms.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;


public class HistoryAdapter extends CursorAdapter {

    private Context ctx;
    private LayoutInflater myInflator;
    private Cursor cursor;

    public HistoryAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        ctx = context;
        this.cursor = cursor;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_transaction_history, parent, false);
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
        TextView tvReciptNo = (TextView) view.findViewById(R.id.row_tv_ReciptNo);
        ImageView ivPrint = (ImageView) view.findViewById(R.id.row_iv_Print);
        TextView tvCustomer_name = (TextView) view.findViewById(R.id.row_tv_customer_name);
        TextView tvDate = (TextView) view.findViewById(R.id.row_tv_date);
        TextView tvCalculation = (TextView) view.findViewById(R.id.row_tv_Calculation);
        TextView tvPaidAmt = (TextView) view.findViewById(R.id.row_tv_PaidAmt);

        if (tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CUSTOMERNAME)).length() > 0)
            tvCustomer_name.setText(tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CUSTOMERNAME)));

        tvReciptNo.setText("No: " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_RECEIPTNO)));
        tvDate.setText(tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_RRECEIPTDATE)));

        String calculations = "";
        if (AppConfig.RevenueRateType.equals("A")) {
            calculations = AppConfig.RevenueUnit + "\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_REVENUERATE)) + " * " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALUNIT)) + "\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALAMOUNT));
        } else {
            calculations = "\n" + AppConfig.RevenueUnit + "\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALUNIT)) + " * " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_REVENUERATE)) + "%\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALAMOUNT));
        }
        tvCalculation.setText(calculations);

        String paid = "";
        if (tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PAYTYPE)).equals("Cheque")) {
            paid = "Paid Amt: " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PAIDAMOUNT)) + "\t Cheque";
        } else {
            paid = "Paid Amt: " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PAIDAMOUNT)) + "\t Cash";
        }
        tvPaidAmt.setText(paid);

//        ivPrint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String printing = "";
//                printing += "NYANG'HWALE DISTRICT COUNCIL";
//                printing += "\nP O BOX 352,NYANG'HWALE-GEITA";
//                printing += "\n________________________\n";
//                printing += "\n\t\t" + AppConfig.revenueItem.toUpperCase();
//
//                printing += "\n________________________\n";
//                printing += "\nRECEIPT NO:" + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_ID)) + "  " + Common.getCurrentDate("dd-MM-yy hh:mm");
//                if (tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CUSTOMERNAME)).length() > 0)
//                    printing += "\n\t\t" + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CUSTOMERNAME)).toUpperCase();
//
//                if (AppConfig.RevenueRateType.equals("A"))
//                    printing += "\n" + AppConfig.RevenueUnit + "\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_REVENUERATE)) + " * " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALUNIT)) + "%\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALAMOUNT));
//                else
//                    printing += "\n" + AppConfig.RevenueUnit + "\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALUNIT)) + " * " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_REVENUERATE)) + "\t " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_TOTALAMOUNT));
//                if (tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PAYTYPE)).equals("Cheque")) {
//                    printing += "\nPaid Amt: " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PAIDAMOUNT)) + "\t Cheque : " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CHEQUENO));
//                } else {
//                    printing += "\nPaid Amt: " + tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_PAIDAMOUNT)) + "\t Cash";
//                }
//                printing += "\n________________________\n";
//                printing += "\n\t\t\tTHANK YOU\n";
//                printing += "\n________________________\n\n" +
//                        "\n" +
//                        "\n" +
//                        "\n" +
//                        "\n";
//                ((ReceiptHistoryActivity) context).print(printing);
//            }
//        });

//        final String id = tempCursor.getString(tempCursor.getColumnIndexOrThrow(DatabaseHandler.KEY_ID));
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
