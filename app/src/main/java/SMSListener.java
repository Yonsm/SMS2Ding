package net.yonsm.SMS2Ding;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.util.Objects;


public class SMSListener extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                StringBuilder body = new StringBuilder();
                String senderNumber = "";

                for (SmsMessage message : messages) {
                    senderNumber = message.getDisplayOriginatingAddress();
                    body.append(message.getDisplayMessageBody());
                }

                String text = body.toString() + "[" + senderNumber + "]";
                startSmsService(context, text);
            }
        }
    }

    private void startSmsService(Context context, String message) {
        Intent serviceIntent = new Intent(context, FilterService.class);
        serviceIntent.putExtra(Constant.DingTalk_Message, message);
        context.startService(serviceIntent);
    }
}
