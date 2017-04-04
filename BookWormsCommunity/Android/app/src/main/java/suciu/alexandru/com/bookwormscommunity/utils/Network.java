package suciu.alexandru.com.bookwormscommunity.utils;

import android.content.Context;
import android.os.AsyncTask;

import suciu.alexandru.com.bookwormscommunity.utils.Util;

/**
 * Created by Alexandru on 27.05.2016.
 */
public class Network extends AsyncTask<Context, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(Context... strings) {
         if (Util.isActiveNetwork(strings[0])) {
             return true;
         } else {
             return false;
         }
    }

}
