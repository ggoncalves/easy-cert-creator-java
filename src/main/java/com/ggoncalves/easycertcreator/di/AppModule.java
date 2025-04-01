package com.ggoncalves.easycertcreator.di;

import com.ggoncalves.easycertcreator.main.CommandListArgsProcessor;
import com.ggoncalves.ggutils.console.exception.ExceptionHandler;
import com.ggoncalves.ggutils.console.validation.FilePathValidator;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class AppModule {

  private final String[] args;

  public AppModule(String[] args) {
    this.args = args;
  }

  @Provides
  public String[] provideArgs() {
    return args;
  }

  @Provides
  @Singleton
  public FilePathValidator provideFilePathValidator() {
    return new FilePathValidator();
  }

  @Provides
  @Singleton
  public ExceptionHandler provideExceptionHandler() {
    return new ExceptionHandler();
  }

  @Provides
  @Singleton
  public CommandListArgsProcessor provideCommandListArgsProcessor(FilePathValidator filePathValidator) {
    return new CommandListArgsProcessor(filePathValidator);
  }
}
