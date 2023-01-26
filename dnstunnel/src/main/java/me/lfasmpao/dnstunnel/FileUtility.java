package me.lfasmpao.dnstunnel;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FileUtility {

    private Context mContext;
    private static String TAG = "copyFile";

    public FileUtility(Context context) {
        mContext = context;
    }

    public static PrintWriter printToFile(File f) {
        Log.d(TAG, "printToFile: " + f.getName());
        PrintWriter p;
        try {
            p = new PrintWriter(f);
            return p;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFilePath(String FILE_NAME) {
        String filePath = mContext.getFilesDir() + "/" + FILE_NAME;
        Log.d(TAG, "File path: " + filePath);
        return filePath;
    }

}
