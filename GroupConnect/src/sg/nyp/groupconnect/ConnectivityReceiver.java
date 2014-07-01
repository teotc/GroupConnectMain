package sg.nyp.groupconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectivityReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		final Intent intentService = new Intent(context, TestService.class);
		intentService.putExtra(
				TestService.INTENT_HANDLE_CONNECTIVITY_CHANGE, "");

		context.startService(intentService);
	}
}
