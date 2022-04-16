package com.small.libcommon.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by small-ho on 2021/01 3:25 PM
 * title: 配置储存
 * @author zsh
 */
public class XSPUtils {
    private static final String Tag = "SharedPreferencesUtils";

    private static final String SP_NAME = "config";
    private static SharedPreferences sp;
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static void saveString(String key, String value) {
        if (sp == null) {
            sp = mContext.getSharedPreferences(SP_NAME, 0);
        }
        sp.edit().putString(key, value).commit();
    }

    public static String getString(String key, String defValue) {
        if (sp == null) {
            sp = mContext.getSharedPreferences(SP_NAME, 0);
        }
        return sp.getString(key, defValue);
    }

    public static void saveInt(String key, int value) {
        if (sp == null) {
            sp = mContext.getSharedPreferences(SP_NAME, 0);
        }
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int defValue) {
        if (sp == null) {
            sp = mContext.getSharedPreferences(SP_NAME, 0);
        }
        return sp.getInt(key, defValue);
    }

    public static void saveBoolean(String key, boolean value) {
        if (sp == null) {
            sp = mContext.getSharedPreferences(SP_NAME, 0);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        if (sp == null) {
            sp = mContext.getSharedPreferences(SP_NAME, 0);
        }
        return sp.getBoolean(key, defValue);
    }

    /** SharedPreferences 清除数据 */
    public static void clear(){
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /** 清除指定key */
    public static void clearkey(String key) {
        if (sp == null) {
            sp = mContext.getSharedPreferences(SP_NAME,0);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
}
