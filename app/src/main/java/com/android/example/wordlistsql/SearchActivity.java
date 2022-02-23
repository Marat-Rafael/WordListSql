package com.android.example.wordlistsql;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
// comprobamos que actividad esta en manifest

/**
 * Clase para pantalla de la busqueda
 */
public class SearchActivity extends AppCompatActivity {

    // variable que usamos con Log
    private static final String TAG = EditWordActivity.class.getSimpleName();

    // declaramos variables
    private TextView mTextView; // aqui mostramos resultado
    private EditText mEditWordView; // aqui cojemos string que buscamos
    private WordListOpenHelper mDB; // nuestro BBDD

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // asociamos con xml
        mEditWordView = ((EditText) findViewById(R.id.search_word));
        mTextView = ((TextView) findViewById(R.id.textView_search_result));
        mDB = new WordListOpenHelper(this);
    }

    /**
     * Metodo para mostrar resultado
     * @param view
     */
    public void showResult(View view) {
        // variable donde guardamos escrito por usuario
        String word = mEditWordView.getText().toString();
        mTextView.setText("Result for " + word + ":\n\n");

        // buscamos en la base de datos con el cursor
        Cursor cursor = mDB.search(word);
        // colocamos cursor en la primera fila
        cursor.moveToFirst();

        // comprobamos que cursor no sea null y hay filas
        if (cursor != null & cursor.getCount() > 0) {
            // variable index
            int index;

            // variable del resultado
            String result;

            do {
                // Don't guess at the column index.
                // Get the index for the named column.
                index = cursor.getColumnIndex(WordListOpenHelper.KEY_WORD);
                // Get the value from the column for the current cursor.
                result = cursor.getString(index);
                // añadimos resultado y salto de linea
                mTextView.append(result + "\n");
            } while (cursor.moveToNext());
            // cerramos cursor
            cursor.close();
        } else {
            // en caso de no tener resultado
            Toast.makeText(this, "no hay resultado", Toast.LENGTH_SHORT).show();
        }
    }
}