package com.neliry.banancheg.videonotes.mvvm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.neliry.banancheg.videonotes.mvvm.repository.FirebaseDatabaseRepository;
import com.neliry.banancheg.videonotes.mvvm.repository.ThemeRepository;

import java.util.List;

public class ThemeViewModel extends ViewModel {

private MutableLiveData<List<Theme>> themes;
private ThemeRepository repository = new ThemeRepository();

public LiveData<List<Theme>> getThemes() {
    if (themes == null) {
        themes = new MutableLiveData<>();
        loadThemes();
    }
    return themes;
}

@Override
protected void onCleared() {
    repository.removeListener();
}

private void loadThemes() {
    repository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Theme>() {
        @Override
        public void onSuccess(List<Theme> result) {
            themes.setValue(result);
        }

        @Override
        public void onError(Exception e) {
            themes.setValue(null);
        }
    });
}
}
