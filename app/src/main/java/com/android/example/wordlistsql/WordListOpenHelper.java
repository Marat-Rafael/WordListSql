package com.android.example.wordlistsql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class WordListOpenHelper extends SQLiteOpenHelper {

    //  variable para log
    private static final String TAG = WordListOpenHelper.class.getSimpleName();

    // version tiene que empezar con 1
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wordlist";
    private static final String WORD_LIST_TABLE = "word_entries";

    // sacamos variable con datos fuera del metodo para poder acceder
    String[] words = {
            "Android",
            "Adapter",
            "ListView",
            "AsyncTask",
            "Android Studio",
            "SQLiteDatabase",
            "SQLOpenHelper",
            "Data model",
            "ViewHolder",
            "Android Performance",
            "OnClickListener",
            "PRUEBA",
            "PRUEBA2",
            "PRUEBA3"};
    // para reflejar datos actualizados hay que limpiar BBDD en el dispositivo setting - apps - <nombre> - storage y chache - clear

    // columnas
    public static final String KEY_ID = "_id";
    public static final String KEY_WORD = "word";

    // creamos array de columnas
    private static final String[] COLUMNS = {KEY_ID, KEY_WORD};


    // comando sql para crear tabla
    private static final String WORD_LIST_TABLE_CREATE =
            "CREATE TABLE " + WORD_LIST_TABLE + " (" +
                    // ID tendra autoincrement si no pasamos valor
                    KEY_ID + " INTEGER PRIMARY KEY, " +
                    KEY_WORD + " TEXT);";

    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;


    // CONSTRUCTOR
    public WordListOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Creamos WordListOpenHelper");
    }

    /**
     * on create creamos la tabla ejecutando comando SQL declarado antes
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(WORD_LIST_TABLE_CREATE);

        // despues de crear DDBB llamamos metodo
        fillDatabaseWithData(db);
    }

    /**
     * @param db
     */
    public void fillDatabaseWithData(SQLiteDatabase db) {


        // creamos contenedor para datos
        ContentValues values = new ContentValues();

        // recorremos array y llenamos datos
        for (int i = 0; i < words.length; i++) {
            values.put(KEY_WORD, words[i]);
            db.insert(WORD_LIST_TABLE, null, values);
        }

    }

    /**
     * Metodo que tiene lo consulta de BBDD
     *
     * @param position
     * @return
     */
    @SuppressLint("Range")
    public WordItem query(int position) {
        // consulta  que devulve todas filas, pero muestra solo una
        String query = "SELECT * FROM " + WORD_LIST_TABLE +
                " ORDER BY " + KEY_WORD + " ASC " +
                " LIMIT " + position + ",1";

        // creamos cursor
        Cursor cursor = null;

        // creamos un objeto de WordItem
        WordItem entry = new WordItem();

        try {
            // si no tenemos DB la creamos
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);
            cursor.moveToFirst();
            entry.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            entry.setWord(cursor.getString(cursor.getColumnIndex(KEY_WORD)));

        } catch (Exception e) {
            Log.d(TAG, "Query EXCEPTION! " + e.getMessage());
        } finally {
            cursor.close();
            return entry;
        }
    } // fin query


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WordListOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " +
                        newVersion + " , lo que destrue toda veja informacion");
        // ejecutamos comando SQL
        db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);

        onCreate(db);
    } // fin onUpgrade

    /**
     * Metodo para insertar una nueva fila en la BBDD
     *
     * @param word palabra que insertamos
     * @return
     */
    public long insert(String word) {
        // si operacion falla metodo devuelve 0
        long newId = 0;

        // creamos variable para guardar fila
        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word);
        // usamos try-catch
        try {
            // si no esta BBDD, creamos una
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(WORD_LIST_TABLE, null, values);

        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPCION! " + e.getMessage());
        }
        return newId;
    } // fin insert

    /**
     * Metodo que devuelve numero de filas que hay en baseDatos
     *
     * @return
     */
    public long count() {
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, WORD_LIST_TABLE);
    } // fin count

    /**
     * Metodo para borrar una fila de la BBDD
     * // para despues restaurar limpiamos storage del dispositivo
     *
     * @param id
     * @return
     */
    public int delete(int id) {

        // variable para guardar resultado
        int deleted = 0;

        try {
            // cojemos  BBDD writable si esta null, la creamos
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            deleted = mWritableDB.delete(
                    WORD_LIST_TABLE, // tabla donde boramos
                    KEY_ID + " =?", // where key_id = ? . interogante especificamos en siquente linea
                    new String[]{String.valueOf(id)} // aqui se especifica que es interrogante
            );

        } catch (Exception e) {
            Log.d(TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
        return deleted;
    }// fin delete . hay que implementarlo en WordListAdapter


    /**
     * Actualiza la fila indicando id y texto nuevo
     *
     * @param id   id de la palabra
     * @param word nuevo string
     * @return devuelve numerode filas
     */
    public int update(int id, String word) {
        // iniciamos numero de filas como -1
        int mNumberOfRowsUpdated = -1;
        try {
            // si no hay BBDD creamos una nueva
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            //  creamos una instancia de ContentValues
            ContentValues values = new ContentValues();
            // insertamos valores
            values.put(KEY_WORD, word);

            mNumberOfRowsUpdated = mWritableDB.update(WORD_LIST_TABLE, // tabla donge hacemos cambio
                    values, // nuevo valor para insertar
                    KEY_ID + " = ?", // where id columna
                    new String[]{String.valueOf(id)}); // tiene que ser array list de argumentos que usamos con interogante una linea  arriba

        } catch (Exception e) {
            Log.d(TAG, "UPDATE EXCEPTION! " + e.getMessage());
        }
        // devolvemos numero de filas afectadas, si no hay cambios devolvemos -1 declarado antes
        return mNumberOfRowsUpdated;
    }


//                    Cursor query (  String table, // The table to query
//                                    String[] columns, // The columns to return
//                                    String selection, // WHERE statement
//                                    String[] selectionArgs, // Arguments to WHERE
//                                    String groupBy, // Grouping filter. Not used.
//                                    String having, // Additional condition filter. Not used.
//                                    String orderBy) // Ordering. Setting to null uses default.

    /**
     * Metodo para hacer busqueda en la base de datos
     */
    public Cursor search(String searchString) {
        String[] columns = new String[]{KEY_WORD};
        String where = KEY_WORD + " LIKE ?";
        searchString = "%" + searchString + "%";
        String[] whereArgs = new String[]{searchString};

        Cursor cursor = null;
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.query(WORD_LIST_TABLE, columns, where, whereArgs, null, null, null);
        } catch (Exception e) {
            Log.d(TAG, "SEARCH EXCEPTION! " + e);
        }
        return cursor;
    }// fin search



}
