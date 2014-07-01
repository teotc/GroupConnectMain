package sg.nyp.groupconnect;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class Menu extends Activity {

	Button searchBN;
	TextView name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		searchBN = (Button) findViewById(R.id.imgbnSearch);

		searchBN.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(Menu.this, MainActivity.class);
				startActivity(myIntent);
			}
		});
		
		setUpUI();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		setUpUI();
	}
	
	private void setUpUI() {
		Bundle extras = this.getIntent().getExtras();
		String nameFromLogin = extras.getString("Name");
		
		name = (TextView) findViewById(R.id.menuUsername);
		name.setText(nameFromLogin);
	}
}
