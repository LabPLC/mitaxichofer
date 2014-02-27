package codigo.labplc.mx.mitaxichofer.trip.dialogues;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author zace3d
 * 
 */
public class Dialogues {

	public static void Toast(Context context, String text, int duration) {
		Toast.makeText(context, text, duration).show();
	}

	public static void Log(Context context, String text, int type) {
		if (type == Log.DEBUG) {
			Log.d(context.getClass().getName().toString(), text);
		} else if (type == Log.ERROR) {
			Log.e(context.getClass().getName().toString(), text);
		} else if (type == Log.INFO) {
			Log.i(context.getClass().getName().toString(), text);
		} else if (type == Log.VERBOSE) {
			Log.v(context.getClass().getName().toString(), text);
		} else if (type == Log.WARN) {
			Log.w(context.getClass().getName().toString(), text);
		}
	}
}