package Diretory.dev.mywallet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class Internet {
    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return  netInfo != null && netInfo.isConnected();
    }
}
