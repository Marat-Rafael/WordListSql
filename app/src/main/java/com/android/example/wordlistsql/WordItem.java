package com.android.example.wordlistsql;

/**
 * Clase que representa modelo de datos
 */
public class WordItem {
    // atributos
    private int mId;
    private String mWord;

    // constructor vacio
    public WordItem() {
    }

    // getter y setter
    public int getId() {
        return this.mId;
    }

    public void setId(int Id) {
        this.mId = Id;
    }

    public String getWord() {
        return this.mWord;
    }

    public void setWord(String Word) {
        this.mWord = Word;
    }
}
