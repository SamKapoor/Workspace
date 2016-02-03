package in.infiniumglobal.infirms.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import in.infiniumglobal.infirms.R;
import in.infiniumglobal.infirms.adapter.HistoryAdapter;
import in.infiniumglobal.infirms.adapter.SearchAdapter;
import in.infiniumglobal.infirms.db.DatabaseHandler;
import in.infiniumglobal.infirms.utils.AppConfig;
import in.infiniumglobal.infirms.utils.Common;
import print.pda3505.helper.printer.PrintService;
import print.pda3505.helper.printer.PrinterClass;
import print.pda3505.printer.PrinterClassSerialPort;

public class ReceiptHistoryActivity extends AppCompatActivity {

    private ListView lvHistory;
    private DatabaseHandler dbHandler;
    private TextView tvTotalAmount;
    private TextView tvPaidAmount;

    protected static final String TAG = "Print_Application";
    public static PrinterClassSerialPort printerClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_history);

        getSupportActionBar().setTitle(AppConfig.revenueItem);

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
                                Toast.makeText(getApplicationContext(), "80mm",
                                        Toast.LENGTH_SHORT).show();
                            } else if (readMessage.contains("580"))// 58mm paper
                            {
                                PrintService.imageWidth = 48;
                                Toast.makeText(getApplicationContext(), "58mm",
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
                                Toast.makeText(getApplicationContext(),
                                        "STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.STATE_LISTEN:
                            case PrinterClass.STATE_NONE:
                                break;
                            case PrinterClass.SUCCESS_CONNECT:
                                printerClass.write(new byte[]{0x1b, 0x2b});// ����ӡ���ͺ�
                                Toast.makeText(getApplicationContext(),
                                        "SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.FAILED_CONNECT:
                                Toast.makeText(getApplicationContext(),
                                        "FAILED_CONNECT", Toast.LENGTH_SHORT).show();

                                break;
                            case PrinterClass.LOSE_CONNECT:
                                Toast.makeText(getApplicationContext(), "LOSE_CONNECT",
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
        printerClass.open(this);

        tvTotalAmount = (TextView) findViewById(R.id.history_tv_totalAmt);
        tvPaidAmount = (TextView) findViewById(R.id.history_tv_paidAmt);
        lvHistory = (ListView) findViewById(R.id.history_lv_receipt);
        dbHandler = DatabaseHandler.getInstance(this);
        Cursor totalAmountCursor = dbHandler.getRevenueReceiptTotalAmount(AppConfig.revenueID);
        if (totalAmountCursor != null && totalAmountCursor.getCount() > 0) {
            totalAmountCursor.moveToFirst();
            try {
                String total[] = totalAmountCursor.getString(totalAmountCursor.getColumnIndex("Total")).split("-");
                tvTotalAmount.setText("Total Amt \n" + total[0]);
                tvPaidAmount.setText("Paid Amt \n" + total[1]);
            } catch (Exception e) {
                e.printStackTrace();
                tvTotalAmount.setText("Total Amt ");
                tvPaidAmount.setText("Paid Amt ");
            }
        }
        Cursor historyCursor = dbHandler.getRevenueReceipt(AppConfig.revenueID);

        HistoryAdapter historyAdapter = new HistoryAdapter(this, historyCursor, 0);
        lvHistory.setAdapter(historyAdapter);

        /*lvCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
*//*                Cursor cursor1 = ((SearchAdapter) adapterView.getAdapter()).getCursor();
                cursor1.moveToPosition(position);*//*
                AppConfig.customerData.moveToPosition(position);
                AppConfig.customerID = AppConfig.customerData.getInt(AppConfig.customerData.getColumnIndex(DatabaseHandler.KEY_RCUSTOMERID));
                startActivity(new Intent(CustomerListActivity.this, CustomerManagementActivity.class));

            }
        });*/
    }


    public void print(String printText) {
//        printerClass.printText(printText); //TODO remove for printing start
    }

    private void ShowMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_receipt_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_instant_pay) {
            finish();
        } else if (id == R.id.action_logout) {
            Common.removeAllPrefrences(this, getString(R.string.app_name));
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
