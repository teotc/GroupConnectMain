package sg.nyp.groupconnect;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import sg.nyp.groupconnect.*;
import sg.nyp.groupconnect.R.drawable;
import sg.nyp.groupconnect.R.string;
import sg.nyp.groupconnect.notification.AppServices;
import sg.nyp.groupconnect.notification.PushMainActivity;
import sg.nyp.groupconnect.room.CreateRmStep3;
import sg.nyp.groupconnect.room.RoomDetails;
import sg.nyp.groupconnect.room.RoomMap;

import com.google.android.gcm.GCMBaseIntentService;

import static sg.nyp.groupconnect.notification.Settings.GCM_SENDER_ID;
import static sg.nyp.groupconnect.notification.Util.displayMessage;
import static sg.nyp.groupconnect.notification.Util.displayMsgForJoin;

public class GCMIntentService extends GCMBaseIntentService {

	//Must be in the Main package
	
	//Possible Value in type: Join, Invite, Delete, InterestedMember
	static String type = "";

	public GCMIntentService() {
		super(GCM_SENDER_ID);
	}
	
	public void setType(String typeNow)
	{
		type = typeNow;
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
	@SuppressLint("NewApi")
	private static void generateNotification(Context context, String message) {
		
		if (type.equals("Join"))
		{
			int icon = R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
			NotificationManager notificationManager = (NotificationManager)
					context.getSystemService(Context.NOTIFICATION_SERVICE);

			String []content = message.split("\n");
			String titleWithBracket = content[1].replace("(", "");
			String title = titleWithBracket.replace(")", "");

			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(context, RoomMap.class);
			resultIntent.putExtra("titleToFocus", title);
			resultIntent.putExtra("arrive", true);
			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);

			PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			Intent toMainIntent = new Intent(context, MainActivity.class);
			PendingIntent toMainPIntent = PendingIntent.getActivity(context, 1, toMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);


			// mId allows you to update the notification later on.
			Notification notification = new NotificationCompat.Builder(context)
			.setContentText(message)
			.setContentTitle(RoomDetails.nTitle)//context.getString(R.string.app_name))
			.setSmallIcon(icon)
			.setWhen(when)
			//.setContentIntent(resultPendingIntent)
			.addAction(R.drawable.tohome_notificationicon, "To Home", toMainPIntent)
			.addAction(R.drawable.toroom_notificationicon, "To Room", resultPendingIntent)
			.build();

			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			// Play default notification sound
			notification.defaults |= Notification.DEFAULT_SOUND;

			// Vibrate if vibrate is enabled
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notificationManager.notify(0, notification);
			
			RoomDetails.joinForNotification = false;
		}
		
		else if (type.equals("Invite"))
		{
			int icon = R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
			NotificationManager notificationManager = (NotificationManager)
					context.getSystemService(Context.NOTIFICATION_SERVICE);

			//String []content = message.split("\n");
			//String titleWithBracket = content[1].replace("(", "");
			String title = CreateRmStep3.title;

			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(context, RoomMap.class);
			resultIntent.putExtra("titleToFocus", title);
			resultIntent.putExtra("arrive", true);
			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);

			PendingIntent toRoomPendingIntent = PendingIntent.getActivity(context, 2, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			Intent toMainIntent = new Intent(context, RoomMap.class);
			toMainIntent.putExtra("titleToFocus", title);
			toMainIntent.putExtra("arrive", true);
			toMainIntent.putExtra("memId", CreateRmStep3.chosenMemberIdList.get(CreateRmStep3.countForMemId));
			toMainIntent.putExtra("roomId", CreateRmStep3.createdRoomId);
			PendingIntent yesPIntent = PendingIntent.getActivity(context, 3, toMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				

			// mId allows you to update the notification later on.
			Notification notification = new NotificationCompat.Builder(context)
			.setContentText(message)
			.setContentTitle("Invite From " + CreateRmStep3.mem_username + "(" + CreateRmStep3.mem_type + ")")//context.getString(R.string.app_name))
			.setSmallIcon(icon)
			.setWhen(when)
			//.setContentIntent(resultPendingIntent)
			.addAction(R.drawable.ok, "Yes", yesPIntent)
			.addAction(R.drawable.toroom_notificationicon, "To Room", toRoomPendingIntent)
			.build();

			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			// Play default notification sound
			notification.defaults |= Notification.DEFAULT_SOUND;

			// Vibrate if vibrate is enabled
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notificationManager.notify(0, notification);
			
			RoomDetails.joinForNotification = false;
		}
		else if (type.equals("Delete"))
		{
			int icon = R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
			NotificationManager notificationManager = (NotificationManager)
					context.getSystemService(Context.NOTIFICATION_SERVICE);


			// mId allows you to update the notification later on.
			Notification notification = new NotificationCompat.Builder(context)
			.setContentText(message)
			.setContentTitle("Room Removed")//context.getString(R.string.app_name))
			.setSmallIcon(icon)
			.setWhen(when)
			.build();

			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			// Play default notification sound
			notification.defaults |= Notification.DEFAULT_SOUND;

			// Vibrate if vibrate is enabled
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notificationManager.notify(0, notification);
			
			RoomDetails.joinForNotification = false;
		}
		else if (type.equals("Leave"))
		{
			int icon = R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
			NotificationManager notificationManager = (NotificationManager)
					context.getSystemService(Context.NOTIFICATION_SERVICE);


			// mId allows you to update the notification later on.
			Notification notification = new NotificationCompat.Builder(context)
			.setContentText(message)
			.setContentTitle("Somone has leave the room: " + RoomDetails.tvTitle.getText())//context.getString(R.string.app_name))
			.setSmallIcon(icon)
			.setWhen(when)
			.build();

			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			// Play default notification sound
			notification.defaults |= Notification.DEFAULT_SOUND;

			// Vibrate if vibrate is enabled
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notificationManager.notify(0, notification);
			
			RoomDetails.joinForNotification = false;
		}
		else if (type.equals("InterestedMember"))
		{
			int icon = R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
			NotificationManager notificationManager = (NotificationManager)
					context.getSystemService(Context.NOTIFICATION_SERVICE);


			// mId allows you to update the notification later on.
			Notification notification = new NotificationCompat.Builder(context)
			.setContentText(message)
			.setContentTitle("Somone has open a room you are interested in.")//context.getString(R.string.app_name))
			.setSmallIcon(icon)
			.setWhen(when)
			.build();

			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			// Play default notification sound
			notification.defaults |= Notification.DEFAULT_SOUND;

			// Vibrate if vibrate is enabled
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notificationManager.notify(0, notification);

		}
	}
}
