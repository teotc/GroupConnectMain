package sg.nyp.groupconnect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class TestService extends Service {

	public static String INTENT_HANDLE_CONNECTIVITY_CHANGE = null;

	public TestService() {
		super();
		// TODO Auto-generated constructor stub
	}

	private void handleConnectivity() {
		final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = cm.getActiveNetworkInfo();

		Intent intent = new Intent("NetworkStatusIntent");

		if (netInfo != null && netInfo.isConnected()) {
			// WE ARE CONNECTED: DO SOMETHING
			intent.putExtra("network", "networkconnected");
			intent.setAction("sg.nyp.GroupConnect.NETWORK_CONNECTED_INTENT");
		} else {
			// WE ARE NOT: DO SOMETHING ELSE
			intent.putExtra("network", "nonetwork");
			intent.setAction("sg.nyp.GroupConnect.NETWORK_CONNECTED_INTENT");
		}
		sendBroadcast(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			final Bundle bundle = intent.getExtras();

			// Handle the intent INTENT_HANDLE_CONNECTIVITY_CHANGEif any
			if ((bundle != null)
					&& (bundle.get(INTENT_HANDLE_CONNECTIVITY_CHANGE) != null)) {
				handleConnectivity();
			}
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG)
				.show();

	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

	}
}
