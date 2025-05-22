package com.example.roomwordssample;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private WordRepository repository;
    private LiveData<List<Word>> allWords;

    public WordViewModel(Application application) {
        super(application);
        WordDao wordDao = WordRoomDatabase.getDatabase(application).wordDao();
        repository = new WordRepository(wordDao);
        allWords = repository.getAllWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return allWords;
    }

    public void insert(Word word) {
        repository.insert(word);
    }
} 