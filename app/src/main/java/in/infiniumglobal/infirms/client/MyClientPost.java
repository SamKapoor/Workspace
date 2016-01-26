package in.infiniumglobal.infirms.client;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is used for post data from API.
 *
 * @author Mayank
 * @since 1.4
 */
public class MyClientPost extends AsyncTask<Map<String, Object>, String, String> {

    public ProgressDialog dialog;
    private String message;
    private Context context;
    private OnPostCallComplete onpostcallcomplete;
    private JSONObject jsonResult;

    public MyClientPost(Context context, String progressMessage, OnPostCallComplete onPostCallComplete2) {
        message = progressMessage;
        this.context = context;
        this.onpostcallcomplete = onPostCallComplete2;

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
//            Log.v("TAG", "API url: " + serverUrl);
            serverUrl = serverUrl.replace(" ", "%20");
            // parameter data to send
            @SuppressWarnings("unchecked")
            Map<String, String> methodParameter = (Map<String, String>) passed_params.get("method_parameters");
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(serverUrl);
                post.setHeader(HTTP.CONTENT_TYPE, "application/json");

                Iterator<Entry<String, String>> iterator = methodParameter.entrySet().iterator();
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(methodParameter.size());
                while (iterator.hasNext()) {
                    Entry<String, String> param = iterator.next();
                    nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
//                Log.v("TAG", "post latlng " + nameValuePairs.toString());
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                HttpEntity resp_entity = response.getEntity();
                result = EntityUtils.toString(resp_entity);
                // System.out.println("result in post 80: "+result);
                if (response.getStatusLine().getStatusCode() != 200) {
//                    Log.v("TAG", "post  status code " + response.getStatusLine().getStatusCode());
                    jsonResult = new JSONObject();
                    jsonResult.put("response_code", "9999");
                    jsonResult.put("response_message", "85 Server error occurred while processing request. Please try again.");
                    result = jsonResult.toString();
                    return result;
                }
            } catch (Exception e) {
                //Log.v("TAG", "post exception " + e.getMessage());
                try {
                    jsonResult = new JSONObject();
                    jsonResult.put("response_code", "9999");
                    jsonResult.put("response_message", "94 Server error occurred while processing request. Please try again.");
                    result = jsonResult.toString();
                    return result;
                } catch (JSONException jsone) {
                    jsone.printStackTrace();
                }
            }
        }
//        System.out.println("result in post: "+result);
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
            onpostcallcomplete.response(result.replace("null", "\"\""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnPostCallComplete {
        void response(String result) throws JSONException;
    }
}
