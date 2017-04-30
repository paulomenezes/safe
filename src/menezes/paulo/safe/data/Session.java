package menezes.paulo.safe.data;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    private static SharedPreferences sPreferences;
    private static SharedPreferences.Editor sEditor;
    
	public Session(Context context) {
        sPreferences = context.getSharedPreferences("safe", Context.MODE_PRIVATE);
	}
	
	public static SharedPreferences getInstance() {
		return sPreferences;
	}
	
	public static SharedPreferences getEditorInstance() {
		return sPreferences;
	}

	public static void putInt(String key, int value) {
		sEditor = sPreferences.edit();
		sEditor.putInt(key, value);
        sEditor.commit();
	}

	public static void putString(String key, String value) {
		sEditor = sPreferences.edit();
		sEditor.putString(key, value);
        sEditor.commit();
	}
	
	public static int getInt(String key) {
		return sPreferences.getInt(key, -1);
	}
	
	public static String getString(String key) {
		return sPreferences.getString(key, null);
	}
}
