package sg.nyp.groupconnect;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import sg.nyp.groupconnect.*;
import sg.nyp.groupconnect.R.drawable;
import sg.nyp.groupconnect.R.string;
import sg.nyp.groupconnect.notification.AppServices;
import sg.nyp.groupconnect.notification.PushMainActivity;
import sg.nyp.groupconnect.room.RoomDetails;
import sg.nyp.groupconnect.room.RoomMap;

import com.google.android.gcm.GCMBaseIntentService;

import static sg.nyp.groupconnect.notification.Settings.GCM_SENDER_ID;
import static sg.nyp.groupconnect.notification.Util.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {

	//Must be in the Main package
	
  public GCMIntentService() {
    super(GCM_SENDER_ID);
  }

  /**
   * Method called on device registered
   **/
  @Override
  protected void onRegistered(Context context, String registrationId) {
    Log.i(TAG, "Device registered: " + registrationId);
    displayMessage(context, getString(R.string.gcm_registered, registrationId));
    AppServices.register(context, registrationId);
  }

  /**
   * Method called on device unregistered
   * */
  @Override
  protected void onUnregistered(Context context, String registrationId) {
    Log.i(TAG, "Device unregistered");
    displayMessage(context, getString(R.string.gcm_unregistered, registrationId));
    AppServices.unregister(context, registrationId);
  }

  /**
   * Method called on receiving a new message
   * */
  @Override
  protected void onMessage(Context context, Intent intent) {
    String message = intent.getExtras().getString("data");
    Log.i(TAG, "Received message: " + message);

    displayMessage(context, message);
    generateNotification(context, message);
  }

  /**
   * Method called on receiving a deleted message
   * */
  @Override
  protected void onDeletedMessages(Context context, int total) {
    Log.i(TAG, "Received deleted messages notification");
    String message = getString(R.string.gcm_deleted, total);
    displayMessage(context, message);
    generateNotification(context, message);
  }

  /**
   * Method called on Error
   * */
  @Override
  public void onError(Context context, String errorId) {
    Log.i(TAG, "Received error: " + errorId);
    displayMessage(context, getString(R.string.gcm_error, errorId));
  }

  @Override
  protected boolean onRecoverableError(Context context, String errorId) {
    Log.i(TAG, "Received recoverable error: " + errorId);
    displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
    return super.onRecoverableError(context, errorId);
  }

  /**
   * Issues a Notification to inform the user that server has sent a message.
   */
  private static void generateNotification(Context context, String message) {
    int icon = R.drawable.ic_launcher;
    long when = System.currentTimeMillis();
    NotificationManager notificationManager = (NotificationManager)
        context.getSystemService(Context.NOTIFICATION_SERVICE);
    
    Intent notificationIntent = new Intent(context, RoomDetails.class);
    notificationIntent.putExtra("title", RoomDetails.tvTitle.toString());
    notificationIntent.putExtra("location", RoomDetails.tvCategory.toString());
    notificationIntent.putExtra("category", RoomDetails.tvLocation.toString());
    // set intent so it does not start a new activity
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

    Notification notification = new NotificationCompat.Builder(context)
    	.setContentText(message)
    	.setContentTitle(RoomDetails.nTitle)//context.getString(R.string.app_name))
    	.setSmallIcon(icon)
    	.setWhen(when)
    	.setContentIntent(intent)
    	.build();

    notification.flags |= Notification.FLAG_AUTO_CANCEL;

    // Play default notification sound
    notification.defaults |= Notification.DEFAULT_SOUND;

    // Vibrate if vibrate is enabled
    notification.defaults |= Notification.DEFAULT_VIBRATE;
    notificationManager.notify(0, notification);
  }
}
