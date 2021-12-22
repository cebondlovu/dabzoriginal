package com.softcrypt.deepkeysmusic.di.components;


import com.softcrypt.deepkeysmusic.di.modules.ContextModule;
import com.softcrypt.deepkeysmusic.di.modules.NetworkModule;
import com.softcrypt.deepkeysmusic.ui.MainActivity;
import com.softcrypt.deepkeysmusic.ui.auth.LoginAct;
import com.softcrypt.deepkeysmusic.ui.auth.RegisterAct;
import com.softcrypt.deepkeysmusic.ui.comment.CommentAct;
import com.softcrypt.deepkeysmusic.ui.home.fragment.HomeFrag;
import com.softcrypt.deepkeysmusic.ui.post.ImagePostAct;
import com.softcrypt.deepkeysmusic.ui.profile.SetupProfileAct;
import com.softcrypt.deepkeysmusic.ui.profile.fragment.ProfileFrag;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NetworkModule.class, ContextModule.class})
public interface AppComponent {

    void injectLoginAct(LoginAct loginAct);
    void injectRegisterAct(RegisterAct registerAct);
    void injectMainAct(MainActivity mainActivity);
    void injectSetupProfileAct(SetupProfileAct setupProfileAct);
    void injectProfileFrag(ProfileFrag profileFrag);
    void injectImagePostAct(ImagePostAct imagePostAct);
    void injectHomeFrag(HomeFrag homeFrag);
    void injectCommentAct(CommentAct commentAct);
}
