package ott.istream;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;



public class DBManager {

    private ott.istream.DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String name, String desc) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(ott.istream.DatabaseHelper.SUBJECT, name);
        contentValue.put(ott.istream.DatabaseHelper.DESC, desc);
        database.insert(ott.istream.DatabaseHelper.TABLE_NAME, null, contentValue);
    }

   /* public Cursor fetch() {
        String[] columns = new String[] { ott.istream.DatabaseHelper._ID, ott.istream.DatabaseHelper.SUBJECT, ott.istream.DatabaseHelper.DESC };
        Cursor cursor = database.query(ott.istream.DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
*/

 /*
    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ott.istream.DatabaseHelper.SUBJECT, name);
        contentValues.put(ott.istream.DatabaseHelper.DESC, desc);
        int i = database.update(ott.istream.DatabaseHelper.TABLE_NAME, contentValues, ott.istream.DatabaseHelper._ID + " = " + _id, null);
        return i;
    }
*/
   /* public void delete(long _id) {
        database.delete(ott.istream.DatabaseHelper.TABLE_NAME, ott.istream.DatabaseHelper._ID + "=" + _id, null);
    }
*/
}
