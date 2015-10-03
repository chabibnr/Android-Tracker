package net.chabibnr.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;

/**
 * Created by admin on 26/08/2015.
 */
public class Conf {
    Context mContext;
    private static final String KEY = "ConfPrefe";

    private final String KEY_AUTO_CHECK = "CONFIG_AUTO_CHECK";
    //private final String KEY_AUTO_CHECK_DELAY = "CONFIG_AUTO_CHECK_DELAY"; //detik

    private final String KEY_NOTIFICATION_URI = "CONFIG_NOTIFICATION_URI";
    private final String KEY_NOTIFICATION_TITLE = "CONFIG_NOTIFICATION_TITLE";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferenceEditor;

    public Conf(Context c) {
        mContext = c;
    }

    public Conf getPreference() {
        //mSharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.PREF_KEY), mContext.MODE_PRIVATE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return this;
    }

    public Conf editor() {
        if (mSharedPreferences == null) {
            getPreference();
        }
        mSharedPreferenceEditor = mSharedPreferences.edit();
        return this;
    }

    public void save() {
        mSharedPreferenceEditor.commit();
    }

    public int autoCheckDelay() {
        if (mSharedPreferences == null) {
            getPreference();
        }
        int i = Integer.parseInt(mSharedPreferences.getString(mContext.getString(R.string.PREF_KEY_AUTO_CHECK_DELAY), "10"));
        return i == 0 ? 10 : i;
    }

    public Conf autoCheckDelay(int delay) {
        if (mSharedPreferenceEditor == null) {
            editor();
        }

        mSharedPreferenceEditor.putInt(mContext.getString(R.string.PREF_KEY_AUTO_CHECK_DELAY), delay);
        return this;
    }

    public boolean autoCheck() {
        if (mSharedPreferences == null) {
            getPreference();
        }

        return mSharedPreferences.getBoolean(mContext.getString(R.string.PREF_KEY_AUTO_CHECK), true);
    }

    public Conf autoCheck(Boolean check) {
        if (mSharedPreferenceEditor == null) {
            editor();
        }

        mSharedPreferenceEditor.putBoolean(mContext.getString(R.string.PREF_KEY_AUTO_CHECK), check);
        return this;
    }

    public String account(){
        if (mSharedPreferences == null) {
            getPreference();
        }

        return mSharedPreferences.getString(mContext.getString(R.string.PREF_KEY_USERNAME_TEXT), "");
    }

    public Conf account(String account) {
        if (mSharedPreferenceEditor == null) {
            editor();
        }

        mSharedPreferenceEditor.putString(mContext.getString(R.string.PREF_KEY_USERNAME_TEXT), account);
        return this;
    }

    public boolean notification() {
        if (mSharedPreferences == null) {
            getPreference();
        }

        return mSharedPreferences.getBoolean(mContext.getString(R.string.PREF_KEY_NOTIFICATION), true);
    }

    public boolean notificationVibrate() {
        if (mSharedPreferences == null) {
            getPreference();
        }

        return mSharedPreferences.getBoolean(mContext.getString(R.string.PREF_KEY_NOTIFICATION_VIBRATE), true);
    }

    public boolean notificationLED() {
        if (mSharedPreferences == null) {
            getPreference();
        }

        return mSharedPreferences.getBoolean(mContext.getString(R.string.PREF_KEY_NOTIFICATION_LED), true);
    }

    public String notificationRingtone(){
        if (mSharedPreferences == null) {
            getPreference();
        }

        return mSharedPreferences.getString(mContext.getString(R.string.PREF_KEY_NOTIFICATION_RINGTONE), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
    }

    public Conf notificationRingtone(String uri) {
        if (mSharedPreferenceEditor == null) {
            editor();
        }

        mSharedPreferenceEditor.putString(mContext.getString(R.string.PREF_KEY_NOTIFICATION_RINGTONE), uri);
        return this;
    }



}
