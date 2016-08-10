package com.tgf.exhibition.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jeff on 2016/5/22.
 */
public class FileUtil {
    public static void closeSafed(Closeable closeable) {
        if(closeable != null) {
            try {closeable.close();} catch (IOException e) {}
        }
    }

    public static boolean copyTo(File from, File to) {
        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            fin = new FileInputStream(from);
            fout = new FileOutputStream(to);

            int byteread = 0;
            byte[] buffer = new byte[1444];
            while ( (byteread = fin.read(buffer)) != -1) {
                fout.write(buffer, 0, byteread);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeSafed(fin);
            closeSafed(fout);
        }
        return true;
    }

    public static File fileFromUri(Context context, Uri uri) {
        return new File(getRealFilePath(context, uri));
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(Context context, Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
