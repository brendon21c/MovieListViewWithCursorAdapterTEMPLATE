package com.clara.movielistviewwithcursoradapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

public class DatabaseManager {

	private Context context;
	private SQLHelper helper;
	private SQLiteDatabase db;

	protected static final String DB_NAME = "movies";
	protected static final int DB_VERSION = 1;
	protected static final String DB_TABLE = "ratings";

	protected static final String ID_COL = "_id";
	protected static final String MOVIE_NAME_COL = "name";
	protected static final String MOVIE_RATING_COL = "rating";
	protected static final String MOVIE_YEAR_COL= "Year";
	protected static final String MOVIE_REVIEWED_DATE_COL = "Date";

	private static final String DB_TAG = "DatabaseManager" ;
	private static final String SQLTAG = "SQLHelper" ;

	public DatabaseManager(Context c) {
		this.context = c;
		helper = new SQLHelper(c);
		this.db = helper.getWritableDatabase();
	}

	public void close() {
		helper.close(); //Closes the database - very important!
	}


	public Cursor getAllMovies() {

		Cursor cursor = db.query(DB_TABLE,null,null,null, null, null, MOVIE_YEAR_COL);

		return cursor;
	}



	public boolean updateRating(int movieID, float rating) {

		Log.d(DB_TAG, "About to update rating for: " + movieID + " to " + rating);

		ContentValues updateValues = new ContentValues();
		updateValues.put(MOVIE_RATING_COL, rating);

		String where = ID_COL + " = ? ";
		String[] whereArgs = { Integer.toString(movieID) };

		int rowsMod = db.update(DB_TABLE, updateValues, where, whereArgs);

		if (rowsMod == 1 ) {

			return true;

		} else {
			return false;
		}

	}

	//Add a product and quantity to the database.
	// Returns true if movie added, false if movie is already in the database
	public boolean addMovie(String name, float rating, Integer yearMade, String reviewDate) {



		ContentValues newMovie = new ContentValues();
		newMovie.put(MOVIE_NAME_COL, name);
		newMovie.put(MOVIE_RATING_COL, rating);
		newMovie.put(MOVIE_YEAR_COL, yearMade);
		newMovie.put(MOVIE_REVIEWED_DATE_COL, reviewDate); // current date as string

		try {
			db.insertOrThrow(DB_TABLE, null, newMovie);
			Log.d(DB_TAG, "Added new movie " + name + " with rating " + rating + " with year " + yearMade
			+ " reviewed on " + reviewDate);

			return true;

		} catch (SQLiteConstraintException sqlce) {

			Log.e(DB_TAG, "Error inserting into table " + " Name: " + name +
			" Rating: " + rating, sqlce);

			return false;
		}


	}


	public class SQLHelper extends SQLiteOpenHelper {
		public SQLHelper(Context c){
			super(c, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String createSQLbase = "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s FLOAT, %s INTEGER, %s TEXT )";
			String createSQL = String.format(createSQLbase, DB_TABLE, ID_COL, MOVIE_NAME_COL, MOVIE_RATING_COL,
					MOVIE_YEAR_COL, MOVIE_REVIEWED_DATE_COL); // Adding new columns to database.
			db.execSQL(createSQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);
			Log.w(SQLTAG, "Upgrade table - drop and recreate it");
		}
	}
}
