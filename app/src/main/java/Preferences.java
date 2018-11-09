package net.yonsm.SMS2Ding;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private SharedPreferences mPreference;

    Preferences(Context context) {
        mPreference = context.getSharedPreferences(Constant.SETTING_FILE_NAME, Context.MODE_PRIVATE);
    }

    public String getDingTalkToken() {
        return mPreference.getString(Constant.DingTalk_Preference_Token, "");
    }

    public void setDingTalkToken(String token) {
        mPreference.edit().putString(Constant.DingTalk_Preference_Token, token).apply();
    }
}
