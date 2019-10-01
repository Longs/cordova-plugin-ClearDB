package com.longs.plugin;

import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClearDB extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("recreateFromAssets")){
            String name = data.getString(0);

            return recreateFromAssets(name, callbackContext);

        }
        return false;
    }

    private boolean recreateFromAssets (String DBName, CallbackContext callbackContext){

        //delete the existing file - assumes that it has been closed successfully in sqlite DB - this is required before calling
        File DBFile = this.cordova.getActivity().getDatabasePath(DBName);
        //recreate
        boolean retVal =  createFromAssets(DBName,DBFile);

        if (retVal){
            callbackContext.success("rFA returns true");
        }else{
            callbackContext.error("rFA returns false") ;
        }

        return retVal;
    }

    private boolean createFromAssets(String myDBName, File dbfile)
    {
        boolean retVal = false;
        InputStream in = null;
        OutputStream out = null;

        try {
            in = this.cordova.getActivity().getAssets().open("www/" + myDBName);
            String dbPath = dbfile.getAbsolutePath();
            dbPath = dbPath.substring(0, dbPath.lastIndexOf("/") + 1);

            File dbPathFile = new File(dbPath);
            if (!dbPathFile.exists())
                dbPathFile.mkdirs();

            File newDbFile = new File(dbPath + myDBName);
            out = new FileOutputStream(newDbFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);

            Log.v("info", "Copied prepopulated DB content to: " + newDbFile.getAbsolutePath());
            retVal = true;
        } catch (IOException e) {
            Log.v("createFromAssets", "No prepopulated DB found, Error= " + e.getMessage());
            retVal = false;
        } catch (Exception e){
            Log.v("createFromAssets", "Exception= " + e.getMessage());
            retVal = false;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
            return retVal;
        }
    }
}
