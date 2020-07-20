package com.cannshine.Fortune.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.cannshine.Fortune.Entities.Hexegram;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Database extends SQLiteOpenHelper {

    static String DB_PATH = "data/data/com.cannshine.Fortune/databases/";
    static String DB_NAME = "FortuneDB.sqlite";

    SQLiteDatabase db;
    private final Context mContext;

    public Database(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void createDB(){
        boolean dbExist = checkDB();
        if (dbExist) {

        } else {
            this.getReadableDatabase();
            this.close();
            try {
                copyDB();
            } catch (Exception e) {
                throw new Error("Error copying DB");

            }

        }
    }

    private void copyDB() throws IOException {
        InputStream dbInput = mContext.getAssets().open(DB_NAME);
        String outFile = DB_PATH + DB_NAME;
        OutputStream dbOutput = new FileOutputStream(outFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = dbInput.read(buffer))>0) {
            dbOutput.write(buffer,0,length);
        }

        dbOutput.flush();
        dbOutput.close();
        dbInput.close();

    }

    private boolean checkDB() {
        SQLiteDatabase check = null;
        try {
            String dbPath = DB_PATH+DB_NAME;
            check = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (check!=null) {
            check.close();
        }

        return check != null ? true : false;
    }

//    public void openDB(){
//        String dbPath = DB_PATH+DB_NAME;
//        db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
//    }

    public synchronized void close(){
        if(db != null)
            db.close();
        super.close();
    }

    public Hexegram getValues(String idHexegram){
        Hexegram dataxam= new Hexegram();
        String query="SELECT * FROM ft_hexagram where h_ID ='" +idHexegram+"'";
        db= this.getWritableDatabase();
        Cursor coCursor=db.rawQuery(query,null);
        coCursor.moveToFirst();

        dataxam.setH_ID(coCursor.getString(0));
        dataxam.setNumber(coCursor.getInt(1));
        dataxam.setH_name(coCursor.getString(2));
        dataxam.setH_mean(coCursor.getString(3));
        dataxam.setH_description(coCursor.getString(4));
        dataxam.setH_content(coCursor.getString(5));
        dataxam.setH_wao1(coCursor.getString(6));
        dataxam.setH_wao2(coCursor.getString(7));
        dataxam.setH_wao3(coCursor.getString(8));
        dataxam.setH_wao4(coCursor.getString(9));
        dataxam.setH_wao5(coCursor.getString(10));
        dataxam.setH_wao6(coCursor.getString(11));

        return dataxam;
    }

}
