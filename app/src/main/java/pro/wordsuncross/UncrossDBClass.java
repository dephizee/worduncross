package pro.wordsuncross;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by AbdulfataiAbdulhafiz on 8/10/2018.
 */

public class UncrossDBClass extends SQLiteOpenHelper {
    private static final String DB_NAME = "uncrossdb";
    private static final int DB_VERSION = 1;
    private static final String WORD_TABLE = "uncross_words_table";

    public static final String T_ID = "_id";
    public static final String T_WORD = "word";
    public static final String T_DIFINITION = "definition";
    private static final String[] T_COLOUMNS = {T_ID, T_WORD, T_DIFINITION};

    public UncrossDBClass(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ WORD_TABLE+" ( "+T_ID+" INTEGER PRIMARY KEY, "+T_WORD+" TEXT,"+T_DIFINITION+" TEXT )" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d("UPGRADING>>>...... ",
                "Upgrading database from version " + i + " to "
                        + i1 + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WORD_TABLE);
        onCreate(sqLiteDatabase);
    }

    public boolean addWordtoDB(String word, String difi){
        if(getReadableDatabase().rawQuery("SELECT * FROM "+WORD_TABLE + " WHERE " + T_WORD + " = '" + word+"'", null).getCount() > 0){
            return false;
        }
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(T_WORD, word);
        mContentValues.put(T_DIFINITION, difi);
        getWritableDatabase().insert(WORD_TABLE, null, mContentValues);
        return true;
    }

    public Cursor getWords(){
        return getReadableDatabase().rawQuery("SELECT * FROM "+WORD_TABLE, null);
    }

    public int deleteWord(int i){
        return getWritableDatabase().delete(WORD_TABLE,T_ID+" =? ",new String[]{String.valueOf(i)});
    }

    public int recordsNum(){
        return (int) DatabaseUtils.queryNumEntries(getReadableDatabase(), WORD_TABLE);
    }
}
