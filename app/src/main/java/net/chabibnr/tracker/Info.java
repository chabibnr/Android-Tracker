package net.chabibnr.tracker;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 18/08/2015.
 */
public class Info {
    ArrayList<HashMap<String, String>> infoList;

    //public static final String INFO_URL_SERVICE = "http://192.168.203.1/tracking-111/getinfo.php";
    public static final String INFO_URL_SERVICE = "http://192.168.43.159/tracking-111/getinfo.php";
    //public static final String INFO_URL_SERVICE = "http://chabibnr.net/tracking-111/getinfo.php";

    public static final String INFO_LABEL = "INFO_LABEL";
    public static final String INFO_VALUE = "INFO_VALUE";
    public static final String INFO_CHECKBOX = "INFO_CHECKBOX";

    public void setArrayList(ArrayList<HashMap<String, String>> al){
        infoList = al;
    }

    public void addMap(String key, String value){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(INFO_LABEL, key);
        map.put(INFO_VALUE, value);
        infoList.add(map);
    }

    public static void notificationPlay(Context context){

        //System.exit(0);
        Conf config = new Conf(context);
        final Uri defaultRingtoneUri = Uri.parse(config.notificationRingtone());
        //Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(context, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
