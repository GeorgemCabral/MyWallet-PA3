package Diretory.dev.mywallet;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class BackUpAndRestore {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void importDB(Context context,String dbName) {
        try {
            File sd = Environment.getExternalStoragePublicDirectory("MyWallet");
            if (sd.canWrite()) {
                File backupDB = context.getDatabasePath("MyWallet.db");
                String backupDBPath = String.format("%s", dbName);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    public static void makeFolder() {
        String folder_main = "MyWallet";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static boolean exportDB(Context context) {
        boolean flag=false;
        try {
            File sd = Environment.getExternalStoragePublicDirectory("MyWallet");

            Log.d("cek sd",String.valueOf(sd));
            Log.d("cek export",String.valueOf(sd.canWrite()));
            if (sd.canWrite()) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                String sDate = sdf.format(c.getTime());

                String backupDBPath = String.format("MyWalletDB-%s.sqlite", sDate);
                File currentDB = context.getDatabasePath("MyWallet.db");
                File backupDB = new File(sd, backupDBPath);
                Log.d("cek export",backupDBPath+String.valueOf(context.getDatabasePath("MyWallet.db")+ String.valueOf(sd) ));
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                flag=true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
