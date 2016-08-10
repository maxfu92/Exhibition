package com.tgf.exhibition.util;

import android.util.Log;

import java.io.File;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jeff on 2016/5/20.
 */
public final class DebugLogger {
    private static final boolean DEBUGABLE = true;
    private static final String TAG = "TGF";

    ////////////////////////////////////
    ////// 以下为 Debug 系类方法 //////
    public static void debugHeaders(String tag, Header[] headers) {
        if (DEBUGABLE && headers != null) {
            Log.d((tag==null?TAG:tag), "Return Headers:");
            StringBuilder builder = new StringBuilder();
            for (Header h : headers) {
                String _h = String.format(Locale.US, "%s : %s", h.getName(), h.getValue());
                Log.d((tag==null?TAG:tag), _h);
                builder.append(_h);
                builder.append("\n");
            }
        }
    }

    public static void debugThrowable(String tag, Throwable t) {
        if (DEBUGABLE && t != null) {
            Log.e((tag==null?TAG:tag), "AsyncHttpClient returned error", t);
        }
    }

    public static void debugResponse(String tag, String response) {
        if (DEBUGABLE && response != null) {
            Log.d((tag==null?TAG:tag), "Response data:");
            Log.d((tag==null?TAG:tag), response);
        }
    }

    public static void debugStatusCode(String tag, int statusCode) {
        if(DEBUGABLE) {
            String msg = String.format(Locale.US, "Return Status Code: %d", statusCode);
            Log.d((tag==null?TAG:tag), msg);
        }
    }

    public static void debugFile(String tag, File file) {
        if (file == null || !file.exists()) {
            debugResponse((tag==null?TAG:tag), "Response is null");
            return;
        }
        try {
            debugResponse((tag==null?TAG:tag), file.getAbsolutePath() + "\r\n\r\n");
        } catch (Throwable t) {
            Log.e((tag==null?TAG:tag), "Cannot debug file contents", t);
        }
    }
}
