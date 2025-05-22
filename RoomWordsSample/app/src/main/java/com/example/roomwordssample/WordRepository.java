package com.example.roomwordssample;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import java.util.List;

public class WordRepository {
    private WordDao wordDao;
    private LiveData<List<Word>> allWords;

    public WordRepository(WordDao wordDao) {
        this.wordDao = wordDao;
        this.allWords = wordDao.getAlphabetizedWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return allWords;
    }

    @WorkerThread
    public void insert(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            wordDao.insert(word);
        });
    }
} 