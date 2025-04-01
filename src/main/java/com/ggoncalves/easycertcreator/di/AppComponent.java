package com.ggoncalves.easycertcreator.di;

import com.ggoncalves.easycertcreator.main.EasyCertCreatorMain;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

  EasyCertCreatorMain getMainApp();

  @Component.Builder
  interface Builder {
    Builder appModule(AppModule appModule);
    AppComponent build();
  }
}
