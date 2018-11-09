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
        String currentToken = preferences.getDingTalkToken();

        String token = intent.getStringExtra(Constant.DingTalk_Token);
        String message = intent.getStringExtra(Constant.DingTalk_Message);
        String from = intent.getStringExtra(Constant.DingTalk_From);
        sendMessage(token, message, from);
    }

    private void sendMessage(String dingTalkToken, String message, String from) {
        if (TextUtils.isEmpty(dingTalkToken)) {
            return;
        }

        final JSONObject root = new JSONObject();
        try {
            JSONObject content = new JSONObject();
            content.put("content", message);
            root.put("msgtype", "text");
            root.put("text", content);
        } catch (JSONException e) {
            Log.d("DingTalkService", e.toString());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constant.DingTalk_Robot_Url + dingTalkToken,
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

    private String getDate(long time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = new Date(time);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
