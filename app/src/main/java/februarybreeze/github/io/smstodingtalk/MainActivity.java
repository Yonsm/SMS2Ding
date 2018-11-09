package februarybreeze.github.io.smstodingtalk;

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

        String noticedToken = preference.getDingTalkNoticedToken();
        if (!TextUtils.isEmpty(noticedToken)) {
            TextView tokenView = (TextView) findViewById(R.id.noticedTokenView);
            tokenView.setText(noticedToken);
        }

        String notNoticedToken = preference.getDingTalkNotNoticedToken();
        if (!TextUtils.isEmpty(notNoticedToken)) {
            TextView notNoticedTokenView = (TextView) findViewById(R.id.notNoticedTokenView);
            notNoticedTokenView.setText(notNoticedToken);
        }

        startService(new Intent(getBaseContext(), MainService.class));

        manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        manager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:

                    String message = "来电号码：" + incomingNumber;

                    String currentToken;
                    currentToken = preference.getDingTalkNotNoticedToken();

                    Intent serviceIntent = new Intent(getBaseContext(), DingTalkService.class);
                    serviceIntent.putExtra(Constant.Current_Ding_Talk_Token, currentToken);
                    serviceIntent.putExtra(Constant.SMS_Message, message);
                    startService(serviceIntent);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                default:
                    break;
            }

            super.onCallStateChanged(state, incomingNumber);
        }
    }

    public void setDingTalkNoticedToken(View view) {
        EditText tokenText = (EditText) findViewById(R.id.tokenText);
        String token = tokenText.getText().toString();
        preference.setDingTalkNoticedToken(token);

        TextView tokenView = (TextView) findViewById(R.id.noticedTokenView);
        tokenView.setText(token);
    }

    public void setDingTalkNotNoticedToken(View view) {
        EditText tokenText = (EditText) findViewById(R.id.tokenText);
        String token = tokenText.getText().toString();
        preference.setDingTalkNotNoticedToken(token);

        TextView tokenView = (TextView) findViewById(R.id.notNoticedTokenView);
        tokenView.setText(token);
    }
}
