package com.example.re_solomio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelActivity extends ViewModel {

    private final MutableLiveData<String> nick = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();

    public LiveData<String> getNick(){
        return nick;
    }
    public void setNick(String newNick){
        nick.setValue(newNick);
    }
    public LiveData<String> getEmail(){
        return email;
    }
    public void setEmail(String newEmail){
        email.setValue(newEmail);
    }


}
