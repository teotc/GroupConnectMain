package sg.nyp.groupconnect.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainDbAdapter extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "myDatabase";
	private static final int DATABASE_VERSION = 2;

	public MainDbAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String DATABASE_CREATE = "CREATE TABLE categories (id INTEGER UNIQUE, "
				+ "name TEXT NOT NULL, typeId INTEGER );";

		db.execSQL(DATABASE_CREATE);

		DATABASE_CREATE = "CREATE TABLE categoriesType (id INTEGER UNIQUE, "
				+ "typeName TEXT NOT NULL );";

		db.execSQL(DATABASE_CREATE);

		DATABASE_CREATE = "CREATE TABLE availableLocation (id INTEGER UNIQUE, "
				+ "name TEXT NOT NULL, " + "location TEXT NOT NULL, "
				+ "latitude DOUBLE, " + "longitude DOUBLE );";

		db.execSQL(DATABASE_CREATE);

		DATABASE_CREATE = "CREATE TABLE member (id INTEGER UNIQUE, "
				+ "name TEXT NOT NULL, "
				+ "location TEXT NOT NULL, "
				+ "latitude DOUBLE, "
				+ "longitude DOUBLE, gender TEXT NOT NULL, "
				+ "schoolId INTEGERL, "
				+ "password TEXT NOT NULL, "
				+ "type TEXT NOT NULL, device TEXT NOT NULL, interestedSub TEXT NOT NULL );";

		db.execSQL(DATABASE_CREATE);

		DATABASE_CREATE = "CREATE TABLE memberGrade ("
				+ "memberId TEXT NOT NULL, " + "subjectId TEXT NOT NULL, "
				+ "oldGrade DOUBLE, " + "newGrade DOUBLE );";

		db.execSQL(DATABASE_CREATE);

		DATABASE_CREATE = "CREATE TABLE Room ( room_id INTEGER UNIQUE, "
				+ "title TEXT NOT NULL, " + "category TEXT NOT NULL, "
				+ "noOfLearner INTEGER, " + "location TEXT NOT NULL, "
				+ "latLng TEXT NOT NULL, " + "creatorId INTEGER, "
				+ "description TEXT NOT NULL, " + "status TEXT NOT NULL, "
				+ "dateFrom TEXT NOT NULL, " + "dateTo TEXT NOT NULL, "
				+ "timeFrom TEXT NOT NULL, " + "timeTo TEXT NOT NULL );";

		db.execSQL(DATABASE_CREATE);

		DATABASE_CREATE = "CREATE TABLE RoomMembers ( room_id INTEGER, "
				+ "memberId INTEGER, " + "memberType TEXT NOT NULL );";

		db.execSQL(DATABASE_CREATE);

		DATABASE_CREATE = "CREATE TABLE schools (id INTEGER UNIQUE, "
				+ "name TEXT NOT NULL, " + "category TEXT NOT NULL, "
				+ "latitude DOUBLE, " + "longitude DOUBLE );";

		db.execSQL(DATABASE_CREATE);

		DATABASE_CREATE = "CREATE TABLE voteLocation ("
				+ "memberId TEXT NOT NULL, " + "roomId TEXT NOT NULL, "
				+ "locationId TEXT NOT NULL, status TEXT NOT NULL );";

		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS availableLocation");
		db.execSQL("DROP TABLE IF EXISTS categoriesType");
		db.execSQL("DROP TABLE IF EXISTS categories");
		db.execSQL("DROP TABLE IF EXISTS member");
		db.execSQL("DROP TABLE IF EXISTS memberGrade");
		db.execSQL("DROP TABLE IF EXISTS Room");
		db.execSQL("DROP TABLE IF EXISTS RoomMembers");
		db.execSQL("DROP TABLE IF EXISTS schools");
		db.execSQL("DROP TABLE IF EXISTS voteLocation");
		onCreate(db);
	}
}
