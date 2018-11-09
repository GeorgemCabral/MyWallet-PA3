package Dir.dev.mywallet;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BackUpTask extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BackUpDatabase(context);
    }
    public void BackUpDatabase(Context context){
        try {
            File sd = Environment.getExternalStoragePublicDirectory("MyWallet");
            if (sd.canWrite()) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                String sDate = sdf.format(c.getTime());

                String backupDBPath = String.format("MyWalletDB-%s.sqlite", sDate);
                File currentDB = context.getDatabasePath("MyWallet.db");
                File backupDB = new File(sd, backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Log.d("CEK BACK UPDONE",backupDBPath+String.valueOf(context.getDatabasePath("MyWallet.db")+ String.valueOf(sd) ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CEK BACKUP ERROR",String.valueOf(e));
        }
    }
}

