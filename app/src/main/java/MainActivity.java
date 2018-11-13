package net.yonsm.SMS2Ding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;


public class MainActivity extends AppCompatActivity {
    private Preferences preference;
    TelephonyManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preference = new Preferences(this);

        String noticedToken = preference.getDingTalkToken();
        if (!TextUtils.isEmpty(noticedToken)) {
            EditText editTex = (EditText) findViewById(R.id.tokenText);
            editTex.setText(noticedToken);
        }

        startService(new Intent(getBaseContext(), MainService.class));

        // 来电监听
        manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        manager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                Intent serviceIntent = new Intent(getBaseContext(), DingTalkService.class);
                serviceIntent.putExtra(Constant.DingTalk_Message, incomingNumber + " 来电");
                serviceIntent.putExtra(Constant.DingTalk_From, incomingNumber);
                startService(serviceIntent);
            }

            super.onCallStateChanged(state, incomingNumber);
        }
    }

    public void setDingTalkToken(View view) {
        EditText tokenText = (EditText) findViewById(R.id.tokenText);
        String token = tokenText.getText().toString();
        preference.setDingTalkToken(token);

        view.setEnabled(false);
        tokenText.setEnabled(false);
    }
}
