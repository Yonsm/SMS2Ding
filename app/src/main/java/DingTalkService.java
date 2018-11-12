package net.yonsm.SMS2Ding;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.database.Cursor;
import android.content.Context;
import android.content.ContentResolver;
import android.provider.ContactsContract;

public class DingTalkService extends IntentService {
    public DingTalkService() {
        super("DingTalkService");
    }

    public DingTalkService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        Preferences preferences = new Preferences(this);
        String token = preferences.getDingTalkToken();

        String message = intent.getStringExtra(Constant.DingTalk_Message);
        String from = intent.getStringExtra(Constant.DingTalk_From);
        sendMessage(token, message, from);
    }

    private void sendMessage(String token, String message, String from) {
        if (TextUtils.isEmpty(token)) {
            return;
        }

        final JSONObject root = new JSONObject();
        try {
            JSONObject markdown = new JSONObject();
            markdown.put("title", message);
            markdown.put("text", ">" + message + "\n\n###### 　来自 **[" + getName(from) + "](tel:\" + from + \")** 于 " + getStamp());
            root.put("msgtype", "markdown");
            root.put("markdown", markdown);
        } catch (JSONException e) {
            Log.d("DingTalkService", e.toString());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constant.DingTalk_Robot_Url + token,
                root,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private String getStamp() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = new Date(System.currentTimeMillis());
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getName(String phoneNum) {
        String contactName = phoneNum;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            Cursor pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                    new String[]{phoneNum}, null);
            if (pCur.moveToFirst()) {
                contactName = pCur
                        .getString(pCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                pCur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return contactName;
    }
}
