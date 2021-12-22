package com.softcrypt.deepkeysmusic.di.modules;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.softcrypt.deepkeysmusic.di.ViewModelKey;
import com.softcrypt.deepkeysmusic.viewModels.ImagePostViewModel;
import com.softcrypt.deepkeysmusic.viewModels.LoginViewModel;
import com.softcrypt.deepkeysmusic.viewModels.MainViewModel;
import com.softcrypt.deepkeysmusic.viewModels.ProfileViewModel;
import com.softcrypt.deepkeysmusic.viewModels.RegisterViewModel;
import com.softcrypt.deepkeysmusic.viewModels.ViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel.class)
    abstract ViewModel bindRegisterViewModel(RegisterViewModel registerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel bindProfileViewModel(ProfileViewModel profileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ImagePostViewModel.class)
    abstract ViewModel bindImagePostViewModel(ImagePostViewModel imagePostViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindFactory(ViewModelFactory factory);
}
