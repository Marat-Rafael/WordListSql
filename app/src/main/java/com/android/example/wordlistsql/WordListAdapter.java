/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.wordlistsql;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Implements a simple Adapter for a RecyclerView.
 * Demonstrates how to add a click handler for each item in the ViewHolder.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {



    /**
     * Clase interna
     *  Custom view holder with a text view and two buttons.
     */
    class WordViewHolder extends RecyclerView.ViewHolder {
        public final TextView wordItemView;
        Button delete_button;
        Button edit_button;

        public WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = (TextView) itemView.findViewById(R.id.word);
            delete_button = (Button)itemView.findViewById(R.id.delete_button);
            edit_button = (Button)itemView.findViewById(R.id.edit_button);
        }
    } // fin clase interna

    private static final String TAG = WordListAdapter.class.getSimpleName();

    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_WORD = "WORD";
    // nueva variable
    public static final String EXTRA_POSITION = "POSITION";

    // declaramos un objeto de baseDatos
    WordListOpenHelper mDB;

    private final LayoutInflater mInflater;
    Context mContext;

    // modificamos constructor , le pasamos tambien WordListOpenHelper objeto
    public WordListAdapter(Context context, WordListOpenHelper db) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDB = db;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.wordlist_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {

        // instancia del WordItem
        WordItem current = mDB.query(position);

        // colocamos datos del lista
        holder.wordItemView.setText(current.getWord());


        // Mantenga una referencia al titular de la vista para el oyente de clics
        final WordViewHolder h = holder; // tiene que ser final

        //-----------------------------------
        // añadimos listener al boton_delete
        holder.delete_button.setOnClickListener(
                // nuevo clase de MyButtonOnClickListener
                new MyButtonOnClickListener(current.getId(), null)  {

                    @Override
                    public void onClick(View v ) {
                        int deleted = mDB.delete(id);
                        if (deleted >= 0)
                            notifyItemRemoved(h.getAdapterPosition());
                    }
                });
        //------------------------------- fin añadir al boton delete

        //--------------------------------------------
        // añadimos on clic listener al boton editar

        holder.edit_button.setOnClickListener(new MyButtonOnClickListener(
                current.getId(), current.getWord()) {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, EditWordActivity.class);

                intent.putExtra(EXTRA_ID, id);

                intent.putExtra(EXTRA_POSITION, h.getAdapterPosition());
                intent.putExtra(EXTRA_WORD, word);

                // Start an empty edit activity.
                ((Activity) mContext).startActivityForResult(
                        intent, MainActivity.WORD_EDIT);
            }
        });

    }

    @Override
    public int getItemCount() {
        // Placeholder so we can see some mock data.

        //return mDB.words.length;
        return (int) mDB.count();
    }
}


