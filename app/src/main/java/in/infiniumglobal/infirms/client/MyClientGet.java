package in.infiniumglobal.infirms.client;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import in.infiniumglobal.infirms.utils.Common;


/**
 * This is used for get data from API.
 *
 * @author Mayank
 * @since 1.4
 */
public class MyClientGet extends AsyncTask<String, String, String> {

    private ProgressDialog dialog;
    private String message;
    private Context context;
    private OnGetCallComplete ongetcallcomplete;

    public MyClientGet(Context ctx, String progressMessage, OnGetCallComplete onGetCallComplete) {
        message = progressMessage;
        this.context = ctx;
        this.ongetcallcomplete = onGetCallComplete;
        if (!(message.equals(""))) {
            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Loading...");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!(message.equals(""))) {
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String result = null;
        if (!isCancelled()) {
            String serverurl = params[0];
//            Log.v("TAG", "API url: " + serverurl);
            serverurl = serverurl.replace(" ", "%20");
            Log.e("URL: ", serverurl);
            result = Common.NetworkOperation(serverurl);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("URL: ", result);
        if (!(message.equals(""))) {
            if (dialog != null) {
                dialog.hide();
                dialog.dismiss();
            }
        }
//        Log.v("TAG", "Result: " + result);
        ongetcallcomplete.response(result.replace("null", "\"\""));
    }

    protected void onProgressUpdate(String... progress) {
    }

    public interface OnGetCallComplete {
        void response(String result);
    }
}
