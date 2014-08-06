package sg.nyp.groupconnect.utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.EditRoom;
import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.ViewRoom;
import sg.nyp.groupconnect.data.MemberDbAdapter;
import sg.nyp.groupconnect.data.RoomDbAdapter;
import sg.nyp.groupconnect.data.RoomMembersDbAdapter;
import sg.nyp.groupconnect.entity.Member;
import sg.nyp.groupconnect.entity.MemberGrades;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PieChartBuilder extends Activity {
	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.rgb(38, 166, 91),
			Color.rgb(144, 198, 149), Color.rgb(247, 202, 24),
			Color.rgb(232, 126, 4), Color.rgb(192, 57, 43) };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	private String category, schoolName;
	private Bundle extras;
	public static ArrayList<Member> arrayMember = new ArrayList<Member>();
	public static ArrayList<MemberGrades> arrayMemberGrade = new ArrayList<MemberGrades>();
	private Intent intent = null;
	private int schoolId, subjectId;
	public static ArrayList<Integer> StudIds = new ArrayList<Integer>();
	public static Context context;
	public static int createdRoomId;
	private ArrayList<String> roomNames = new ArrayList<String>();
	private ArrayList<Integer> roomId = new ArrayList<Integer>();
	public static int count;
	private String currentMemberId = "";
	private Button viewGrp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);

		context = this;

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(PieChartBuilder.this);
		currentMemberId = sp.getString("id", null);

		extras = getIntent().getExtras();
		if (extras != null) {
			category = extras.getString("Category");
			schoolName = extras.getString("School_Name");
			schoolId = extras.getInt("School_Id");
			subjectId = extras.getInt("Subject_Id");
		}

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

		TextView tw = (TextView) findViewById(R.id.Chart_Tittle);
		tw.setText(schoolName + " - " + category);

		viewGrp = (Button) findViewById(R.id.btnGroup);
		viewGrp.setVisibility(View.GONE);

		new RetrieveMemberGradeSchool().execute();
		new RetrieveCreatedRoom().execute();

		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setChartTitle("Total No. of Student in each grade.");
		mRenderer.setChartTitleTextSize(40f); // TODO
		mRenderer.setLegendTextSize(40f); // TODO
		mRenderer.setLabelsColor(Color.WHITE);
		mRenderer.setLabelsTextSize(30f); // TODO
		mRenderer.setPanEnabled(false);
		mRenderer.setZoomEnabled(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.Chart_layout);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {

					} else {
						for (int i = 0; i < mSeries.getItemCount(); i++) {
							mRenderer.getSeriesRendererAt(i).setHighlighted(
									i == seriesSelection.getPointIndex());
						}
						mChartView.repaint();
						ArrayList<Member> arrayMembers = new ArrayList<Member>();
						ArrayList<MemberGrades> arrayGrades = new ArrayList<MemberGrades>();

						if (seriesSelection.getPointIndex() == 0) {
							int size = arrayMember.size();

							for (int i = 0; i < size; i++) {
								if (arrayMemberGrade.get(i).getNewGrade() >= 80.0) {
									arrayMembers.add(arrayMember.get(i));
									arrayGrades.add(arrayMemberGrade.get(i));
								}
							}
						} else if (seriesSelection.getPointIndex() == 1) {
							int size = arrayMember.size();

							for (int i = 0; i < size; i++) {
								if (arrayMemberGrade.get(i).getNewGrade() >= 70.0
										&& arrayMemberGrade.get(i)
												.getNewGrade() < 80.0) {
									arrayMembers.add(arrayMember.get(i));
									arrayGrades.add(arrayMemberGrade.get(i));
								}
							}
						} else if (seriesSelection.getPointIndex() == 2) {
							int size = arrayMember.size();

							for (int i = 0; i < size; i++) {
								if (arrayMemberGrade.get(i).getNewGrade() >= 60.0
										&& arrayMemberGrade.get(i)
												.getNewGrade() < 70.0) {
									arrayMembers.add(arrayMember.get(i));
									arrayGrades.add(arrayMemberGrade.get(i));
								}
							}
						} else if (seriesSelection.getPointIndex() == 3) {
							int size = arrayMember.size();

							for (int i = 0; i < size; i++) {
								if (arrayMemberGrade.get(i).getNewGrade() >= 50.0
										&& arrayMemberGrade.get(i)
												.getNewGrade() < 60.0) {
									arrayMembers.add(arrayMember.get(i));
									arrayGrades.add(arrayMemberGrade.get(i));
								}
							}
						} else if (seriesSelection.getPointIndex() == 4) {
							int size = arrayMember.size();

							for (int i = 0; i < size; i++) {
								if (arrayMemberGrade.get(i).getNewGrade() < 50.0) {
									arrayMembers.add(arrayMember.get(i));
									arrayGrades.add(arrayMemberGrade.get(i));
								}
							}
						}

						alertDialog(arrayMembers, arrayGrades);

					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	private void alertDialog(final ArrayList<Member> arrayMember,
			final ArrayList<MemberGrades> arrayGrades) {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
		builderSingle.setTitle("Select A Name: ");
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getBaseContext(), android.R.layout.select_dialog_singlechoice);

		for (int i = 0; i < arrayMember.size(); i++) {
			arrayAdapter.add(arrayMember.get(i).getName());
		}

		builderSingle.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String strName = arrayAdapter.getItem(which);

						for (int i = 0; i < arrayMember.size(); i++) {
							if (arrayMember.get(i).getName().equals(strName)) {

								final String nameId = arrayMember.get(i)
										.getName();
								final double newGrade = arrayGrades.get(i)
										.getNewGrade();
								final double oldGrade = arrayGrades.get(i)
										.getOldGrade();

								AlertDialog.Builder builderInner = new AlertDialog.Builder(
										PieChartBuilder.this);
								builderInner.setMessage(arrayMember.get(i)
										.getName()
										+ "("
										+ arrayMember.get(i).getGender()
										+ ") living in "
										+ arrayMember.get(i).getLocation());
								builderInner.setTitle("You have selected");
								builderInner.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												intent = new Intent(
														getBaseContext(),
														BarChartBuilder.class);
												intent.putExtra("Name", nameId);
												intent.putExtra("Category",
														category);
												intent.putExtra("newGrade",
														newGrade);
												intent.putExtra("oldGrade",
														oldGrade);
												startActivity(intent);
											}
										});
								builderInner.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// if this button is clicked,
												// just close
												// the dialog box and do nothing
												dialog.cancel();
											}
										});
								builderInner.show();
							}
						}
					}
				});
		builderSingle.show();
	}

	class RetrieveMemberGradeSchool extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		// Database
		public ProgressDialog pDialog;

		private static final String TAG_ID = "id";
		private static final String TAG_NAME = "name";
		private static final String TAG_LOCATION = "location";
		private static final String TAG_LATITUDE = "latitude";
		private static final String TAG_LONGITUDE = "longitude";
		private static final String TAG_GENDER = "gender";
		private static final String TAG_OLDGRADE = "oldGrade";
		private static final String TAG_NEWGRADE = "newGrade";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.context);
			pDialog.setMessage("Retreiving data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			MemberDbAdapter mDbHelper = new MemberDbAdapter(
					PieChartBuilder.this);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchMemberGradeSchool(
					Integer.toString(schoolId), Integer.toString(subjectId));
			arrayMember.clear();
			arrayMemberGrade.clear();

			if (mCursor.getCount() != 0) {
				mCursor.moveToFirst();

				Member m = new Member(mCursor.getInt(mCursor.getColumnIndex(TAG_ID)),
						mCursor.getString(mCursor.getColumnIndex(TAG_NAME)), mCursor.getString(mCursor.getColumnIndex(TAG_LOCATION)),
						mCursor.getDouble(mCursor.getColumnIndex(TAG_LATITUDE)),
						mCursor.getDouble(mCursor.getColumnIndex(TAG_LONGITUDE)),
						mCursor.getString(mCursor.getColumnIndex(TAG_GENDER)), schoolId, "", "", "", "");
				arrayMember.add(m);

				MemberGrades mg = new MemberGrades(Integer.toString(mCursor
						.getInt(mCursor.getColumnIndex(TAG_ID))), Integer.toString(subjectId),
						mCursor.getDouble(mCursor.getColumnIndex(TAG_OLDGRADE)),
						mCursor.getDouble(mCursor.getColumnIndex(TAG_NEWGRADE)));
				arrayMemberGrade.add(mg);

				while (mCursor.moveToNext()) {

					m = new Member(mCursor.getInt(mCursor.getColumnIndex(TAG_ID)),
							mCursor.getString(mCursor.getColumnIndex(TAG_NAME)), mCursor.getString(mCursor.getColumnIndex(TAG_LOCATION)),
							mCursor.getDouble(mCursor.getColumnIndex(TAG_LATITUDE)),
							mCursor.getDouble(mCursor.getColumnIndex(TAG_LONGITUDE)),
							mCursor.getString(mCursor.getColumnIndex(TAG_GENDER)), schoolId, "", "", "", "");
					arrayMember.add(m);

					mg = new MemberGrades(Integer.toString(mCursor
							.getInt(mCursor.getColumnIndex(TAG_ID))), Integer.toString(subjectId),
							mCursor.getDouble(mCursor.getColumnIndex(TAG_OLDGRADE)),
							mCursor.getDouble(mCursor.getColumnIndex(TAG_NEWGRADE)));
					arrayMemberGrade.add(mg);
				}
			}

			mCursor.close();
			mDbHelper.close();

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			String[] grades = new String[] { "Grade A", "Grade B", "Grade C",
					"Grade D", "Grade F" };

			Double GradeA = 0.0, GradeB = 0.0, GradeC = 0.0, GradeD = 0.0, GradeF = 0.0;
			int size = arrayMember.size();

			for (int i = 0; i < size; i++) {
				if (arrayMemberGrade.get(i).getNewGrade() < 50.0) {
					GradeF = GradeF + 1;
				} else if (arrayMemberGrade.get(i).getNewGrade() < 60.0) {
					GradeD = GradeD + 1;
				} else if (arrayMemberGrade.get(i).getNewGrade() < 70.0) {
					GradeC = GradeC + 1;
				} else if (arrayMemberGrade.get(i).getNewGrade() < 80.0) {
					GradeB = GradeB + 1;
				} else if (arrayMemberGrade.get(i).getNewGrade() >= 80.0) {
					GradeA = GradeA + 1;
				}
			}

			DecimalFormat df = new DecimalFormat("#.00");

			Double[] values = new Double[] {
					Double.parseDouble(df.format(GradeA / size * 100)),
					Double.parseDouble(df.format(GradeB / size * 100)),
					Double.parseDouble(df.format(GradeC / size * 100)),
					Double.parseDouble(df.format(GradeD / size * 100)),
					Double.parseDouble(df.format(GradeF / size * 100)) };

			for (int i = 0; i < values.length; i++) {
				mSeries.add(grades[i], values[i]);
				SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
				renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
						% COLORS.length]);
				mRenderer.addSeriesRenderer(renderer);
				mChartView.repaint();
			}
			// dismiss the dialog once product deleted
			pDialog.dismiss();
		}
	}

	public void Group(View v) {

		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				PieChartBuilder.this);
		builderSingle.setTitle("Select A Group: ");
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getBaseContext(), android.R.layout.select_dialog_singlechoice);

		arrayAdapter.add("Create new Group!");

		for (int i = 0; i < roomNames.size(); i++) {
			arrayAdapter.add(roomNames.get(i));
		}

		builderSingle.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String strName = arrayAdapter.getItem(which);
						if (which == 0) {
							Intent i = new Intent(PieChartBuilder.this,
									EditRoom.class);
							startActivityForResult(i, 1);
						} else {
							for (int i = 0; i < roomNames.size(); i++) {
								if (roomNames.get(i).equals(strName)) {

									createdRoomId = roomId.get(i);

									AlertDialog.Builder builderInner = new AlertDialog.Builder(
											PieChartBuilder.this);
									builderInner.setMessage(strName);
									builderInner.setTitle("You have selected");
									builderInner
											.setPositiveButton(
													"Okay",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															AlertDialog dialog1;
															int size = arrayMember
																	.size();
															final String[] items = new String[size];

															for (int i = 0; i < size; i++) {
																items[i] = arrayMember
																		.get(i)
																		.getName()
																		+ "("
																		+ arrayMember
																				.get(i)
																				.getGender()
																		+ ")";
															}

															final ArrayList<Integer> seletedItems = new ArrayList<Integer>();

															AlertDialog.Builder builder = new AlertDialog.Builder(
																	PieChartBuilder.this);
															builder.setTitle("Select Names");
															builder.setMultiChoiceItems(
																	items,
																	null,
																	new DialogInterface.OnMultiChoiceClickListener() {
																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int indexSelected,
																				boolean isChecked) {
																			if (isChecked) {
																				seletedItems
																						.add(indexSelected);
																			} else if (seletedItems
																					.contains(indexSelected)) {
																				seletedItems
																						.remove(Integer
																								.valueOf(indexSelected));
																			}
																		}
																	})
																	.setPositiveButton(
																			"OK",
																			new DialogInterface.OnClickListener() {
																				@Override
																				public void onClick(
																						DialogInterface dialog,
																						int id) {
																					StudIds.clear();
																					for (int i = 0; i < seletedItems
																							.size(); i++) {
																						StudIds.add(arrayMember
																								.get(seletedItems
																										.get(i))
																								.getId());
																					}
																					count = 0;
																					new retrieveRoomDetail()
																							.execute();
																				}
																			})
																	.setNegativeButton(
																			"Cancel",
																			new DialogInterface.OnClickListener() {
																				@Override
																				public void onClick(
																						DialogInterface dialog,
																						int id) {
																					dialog.cancel();

																				}
																			});

															dialog1 = builder
																	.create();
															dialog1.show();
														}
													});
									builderInner
											.setNegativeButton(
													"No",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {
															dialog.cancel();
														}
													});
									builderInner.show();
								}
							}
						}
					}
				});
		builderSingle.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {

				createdRoomId = data.getExtras().getInt("RoomId");
				roomId.add(createdRoomId);
				roomNames.add(data.getExtras().getString("RoomName"));

				AlertDialog dialog1;
				// following code will be in your activity.java
				// file
				// arraylist to keep the selected items

				int size = arrayMember.size();
				final String[] items = new String[size];

				for (int i = 0; i < size; i++) {
					items[i] = arrayMember.get(i).getName() + "("
							+ arrayMember.get(i).getGender() + ")";
				}

				final ArrayList<Integer> seletedItems = new ArrayList<Integer>();

				AlertDialog.Builder builder = new AlertDialog.Builder(
						PieChartBuilder.this);
				builder.setTitle("Select Names");
				builder.setMultiChoiceItems(items, null,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int indexSelected, boolean isChecked) {
								if (isChecked) {
									seletedItems.add(indexSelected);
								} else if (seletedItems.contains(indexSelected)) {
									seletedItems.remove(Integer
											.valueOf(indexSelected));
								}
							}
						})
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										StudIds.clear();
										for (int i = 0; i < seletedItems.size(); i++) {
											StudIds.add(arrayMember.get(
													seletedItems.get(i))
													.getId());
										}
										count = 0;
										new retrieveRoomDetail().execute();

									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				dialog1 = builder.create();
				dialog1.show();
			}
			if (resultCode == RESULT_CANCELED) {

				Toast.makeText(PieChartBuilder.this, "Room not created.",
						Toast.LENGTH_LONG).show();

			}
		}
	}

	class RetrieveCreatedRoom extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		// Database
		public ProgressDialog pDialog;

		JSONParser jsonParser = new JSONParser();

		private static final String TAG_ROOM_ID = "room_id";
		private static final String TAG_TITLE = "title";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.context);
			pDialog.setMessage("Retreiving data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			RoomDbAdapter mDbHelper = new RoomDbAdapter(
					PieChartBuilder.this);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchCreatedRoom(currentMemberId, category);

			roomId.clear();
			roomNames.clear();

			if (mCursor.getCount() != 0) {
				mCursor.moveToFirst();

				roomId.add(mCursor.getInt(mCursor.getColumnIndex(TAG_ROOM_ID)));
				roomNames.add(mCursor.getString(mCursor.getColumnIndex(TAG_TITLE)));

				while (mCursor.moveToNext()) {

					roomId.add(mCursor.getInt(mCursor.getColumnIndex(TAG_ROOM_ID)));
					roomNames.add(mCursor.getString(mCursor.getColumnIndex(TAG_TITLE)));
				}
			}

			mCursor.close();
			mDbHelper.close();

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			viewGrp.setVisibility(View.VISIBLE);
			pDialog.dismiss();

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			PieChartBuilder.this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class retrieveRoomDetail extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;
		private ArrayList<String> member = new ArrayList<String>();
		public ArrayList<String> memberId = new ArrayList<String>();
		private ArrayList<String> members = new ArrayList<String>();

		// Database
		public ProgressDialog pDialog;

		private static final String TAG_MEMBERID = "memberId";
		private static final String TAG_MEMBERNAME = "name";
		private static final String TAG_MEMBERTYPE = "memberType";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(PieChartBuilder.context);
			pDialog.setMessage("Adding to group...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... args) {
			RoomDbAdapter mDbHelper = new RoomDbAdapter(
					PieChartBuilder.this);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchRoom(Integer.toString(PieChartBuilder.createdRoomId));

			member.clear();
			memberId.clear();
			members.clear();

			if (mCursor.getCount() != 0) {
				mCursor.moveToFirst();

				if (mCursor.getString(mCursor.getColumnIndex(TAG_MEMBERTYPE)).equals("Learner")) {
					member.add(mCursor.getString(mCursor.getColumnIndex(TAG_MEMBERNAME)));
					memberId.add(Integer.toString(mCursor.getInt(mCursor.getColumnIndex(TAG_MEMBERID))));
				}

				while (mCursor.moveToNext()) {

					if (mCursor.getString(mCursor.getColumnIndex(TAG_MEMBERTYPE)).equals("Learner")) {
						member.add(mCursor.getString(mCursor.getColumnIndex(TAG_MEMBERNAME)));
						memberId.add(Integer.toString(mCursor.getInt(mCursor.getColumnIndex(TAG_MEMBERID))));
					}
				}

				for (int i = 0; i < PieChartBuilder.StudIds.size(); i++) {
					int check = 1;
					for (int j = 0; j < member.size(); j++) {
						if (PieChartBuilder.StudIds.get(i).equals(
								memberId.get(j))) {
							check = 0;
						}
					}
					if (check == 1) {
						members.add(Integer.toString(PieChartBuilder.StudIds
								.get(i)));
					}
				}
			}

			mCursor.close();
			mDbHelper.close();

			return null;

		}

		protected void onPostExecute(String file_url) {
			if (members.size() > 0) {
				new createRoom().execute();
			}
		}

		class createRoom extends AsyncTask<String, String, String> {
			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			boolean failure = false;
			public int success;

			// Database

			JSONParser jsonParser = new JSONParser();

			private static final String CREATE_ROOM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/createRoomMember.php"; // TODO

			private static final String TAG_SUCCESS = "success";
			private static final String TAG_MESSAGE = "message";

			@Override
			protected void onPreExecute() {
				super.onPreExecute();

			}

			@Override
			protected String doInBackground(String... args) {
				int success;
				String post_roomId = Integer
						.toString(PieChartBuilder.createdRoomId);
				String post_member = members.get(PieChartBuilder.count);

				try {
					// Building Parameters
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					// params.add(new BasicNameValuePair("username",
					// post_username));
					params.add(new BasicNameValuePair("room_id", post_roomId));
					params.add(new BasicNameValuePair("memberId", post_member));
					params.add(new BasicNameValuePair("memberType", "Learner"));

					Log.d("request!", "starting");

					// Posting user data to script
					JSONObject json = jsonParser.makeHttpRequest(
							CREATE_ROOM_MEM_URL, "POST", params);

					// json success element
					success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						
						RoomMembersDbAdapter mDbHelper = new RoomMembersDbAdapter(
								PieChartBuilder.this);
						mDbHelper.open();

						mDbHelper.createRoomMembers(PieChartBuilder.createdRoomId, Integer.parseInt(members.get(PieChartBuilder.count)), "Learner");

						mDbHelper.close();
						
						return json.getString(TAG_MESSAGE);
					} else {
						return json.getString(TAG_MESSAGE);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return null;

			}

			protected void onPostExecute(String file_url) {
				PieChartBuilder.count++;

				if (PieChartBuilder.count < members.size()) {
					new createRoom().execute();
				} else {
					pDialog.dismiss();
					Toast.makeText(PieChartBuilder.context,
							"Members added to group.", Toast.LENGTH_LONG)
							.show();

					startActivity(new Intent(PieChartBuilder.this,
							ViewRoom.class));
				}
			}
		}
	}

}
