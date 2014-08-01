package sg.nyp.groupconnect.room;

import sg.nyp.groupconnect.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class CreateRm extends Activity {
	// Variable
	EditText etTitle, etCategory, etDesc;
	Button btnNext, btnClear, btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_rm);

		// Get the selected Location from MainActivity
		// Intent intent = new Intent();
		// location = this.getIntent().getStringExtra("location");
		// lat = this.getIntent().getStringExtra("lat");
		// lng = this.getIntent().getStringExtra("lng");

		etTitle = (EditText) findViewById(R.id.etTitle);
		etCategory = (EditText) findViewById(R.id.etCategory);
		etDesc = (EditText) findViewById(R.id.etDesc);
		btnNext = (Button) findViewById(R.id.btnNext);
		// btnClear = (Button) findViewById(R.id.btnClear);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		btnNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// String message;
				boolean success = true;

				// Check for any empty EditText
				if (etTitle.getText().toString().length() <= 0) {
					etTitle.setError("Enter Title");
					success = false;
				}

				if (etCategory.getText().toString().length() <= 0) {
					etCategory.setError("Enter Category");
					success = false;
				}

				if (etDesc.getText().toString().length() <= 0) {
					etDesc.setText("None");
				}

				if (success == true) // If all fields are filled
				{
					// put data to be return to parent in an intent
					/*
					 * Intent output = new Intent();
					 * output.putExtra("roomCreated", "rmCreated");
					 * //output.putExtra(CREATE, "Room Created");
					 * //output.putExtra(TITLE, etTitle.getText().toString());
					 * //output.putExtra(NOOFLEARNER,
					 * etNoOfLearner.getText().toString());
					 * //output.putExtra(LOCATION,
					 * etLocation.getText().toString());
					 * //output.putExtra(CATEGORY,
					 * etCategory.getText().toString()); // Set the results to
					 * be returned to parent setResult(RESULT_OK, output);
					 */

					Intent myIntent = new Intent(CreateRm.this,
							CreateRmStep2.class);
					myIntent.putExtra("title", etTitle.getText().toString());
					myIntent.putExtra("category", etCategory.getText()
							.toString());
					myIntent.putExtra("desc", etDesc.getText().toString());
					startActivity(myIntent);
					// finish();

				}

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// put data to be return to parent in an intent
				Intent output = new Intent();
				output.putExtra("Cancel", "Canceled");
				// Set the results to be returned to parent
				setResult(RESULT_CANCELED, output);

				// Ends the sub-activity
				finish();

			}
		});

		/*
		 * btnClear.setOnClickListener(new OnClickListener(){ public void
		 * onClick(View v) { etTitle.setText(""); etCategory.setText("");
		 * 
		 * } });
		 */

		/*
		 * Button btnLoadIcon = (Button) findViewById(R.id.btnLoadIcon);
		 * btnLoadIcon.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * 
		 * Intent i = new Intent( Intent.ACTION_PICK,
		 * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		 * 
		 * startActivityForResult(i, RESULT_LOAD_IMAGE); } });
		 */
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		/*
		 * if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK &&
		 * null != data) { Uri selectedImage = data.getData(); String[]
		 * filePathColumn = { MediaStore.Images.Media.DATA };
		 * 
		 * Cursor cursor = getContentResolver().query(selectedImage,
		 * filePathColumn, null, null, null); cursor.moveToFirst();
		 * 
		 * int columnIndex = cursor.getColumnIndex(filePathColumn[0]); String
		 * picturePath = cursor.getString(columnIndex); cursor.close();
		 * 
		 * ImageView imageView = (ImageView) findViewById(R.id.imgView);
		 * imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		 * 
		 * img = picturePath;
		 * 
		 * }
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_rm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == android.R.id.home) {
			CreateRm.this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
