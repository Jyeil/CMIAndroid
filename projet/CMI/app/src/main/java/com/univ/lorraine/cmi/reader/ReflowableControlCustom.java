package com.univ.lorraine.cmi.reader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.skytree.epub.ReflowableControl;

/**
 * Created by alexis on 13/05/2016.
 */
public class ReflowableControlCustom extends ReflowableControl {

    public ReflowableControlCustom(Context context) {
        super(context);
    }

    public ReflowableControlCustom(Context context, int i) {
        super(context, i);
    }

    @Override
    public boolean isRTL() {
        try {
            return super.isRTL();
        } catch (NullPointerException e) {
            Log.e("RTL", "RTL");
            // Sleep requis
            return false;
            //return isRTL();
        }
    }

}