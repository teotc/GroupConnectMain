package sg.nyp.groupconnect.notification;

import android.content.Context;
import android.content.Intent;

public final class Util {

  public static final String TAG = "sg.nyp.groupconnect";
  public static final String DISPLAY_MESSAGE_ACTION = "sg.nyp.groupconnect.DISPLAY_MESSAGE";
  public static final String EXTRA_MESSAGE = "message";

  public static void displayMessage(Context context, String message) {
    Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
    intent.putExtra(EXTRA_MESSAGE, message);
    context.sendBroadcast(intent);
  }

}
