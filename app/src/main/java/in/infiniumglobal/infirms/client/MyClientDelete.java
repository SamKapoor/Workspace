package in.infiniumglobal.infirms.client;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * This is used for post data from API.
 *
 * @author Mayank
 * @since 1.4
 */
public class MyClientDelete extends AsyncTask<Map<String, Object>, String, String> {

    public ProgressDialog dialog;
    private String message;
    private Context context;
    private OnDeleteCallComplete onDeletecallcomplete;
    private JSONObject jsonResult;

    public MyClientDelete(Context context, String progressMessage, OnDeleteCallComplete onDeleteCallComplete) {
        message = progressMessage;
        this.context = context;
        this.onDeletecallcomplete = onDeleteCallComplete;

        if (!(message.equals(""))) {
            dialog = new ProgressDialog(context);
            dialog.setMessage(progressMessage);
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
    protected String doInBackground(Map<String, Object>... params) {
        String result = null;

        if (!isCancelled()) {

            Map<String, Object> passed_params = params[0];
            // API call URL
            String serverUrl = (String) passed_params.get("url");
            serverUrl = serverUrl.replace(" ", "%20");
//            Log.v("TAG", "API url: " + serverUrl);

            InputStream is = null;
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpDelete httpDelete = new HttpDelete(serverUrl);
                HttpResponse httpResponse = httpClient.execute(httpDelete);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                result = sb.toString();
//                Log.d(getClass().getSimpleName(), result);
            } catch (Exception e) {
//                Log.d(getClass().getSimpleName(), "Error converting result " + e.toString());
            }
            return result;

        }
        //System.out.println("result in post: "+result);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (!(message.equals(""))) {
            if (dialog != null) {
                dialog.hide();
                dialog.dismiss();
            }
        }
//        System.out.println("result in onPostExecute: " + result);
        try {
            onDeletecallcomplete.response(result.replace("null", "\"\""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnDeleteCallComplete {
        void response(String result) throws JSONException;
    }
}
