package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.di.AppComponent;
import com.ggoncalves.easycertcreator.di.AppModule;
import com.ggoncalves.easycertcreator.di.DaggerAppComponent;
import com.ggoncalves.ggutils.console.exception.ExceptionHandler;

import javax.inject.Inject;

public class EasyCertCreatorMain {

  private final String[] args;
  private final CommandListArgsProcessor commandProcessor;
  private final ExceptionHandler exceptionHandler;

  @Inject
  public EasyCertCreatorMain(String[] args, CommandListArgsProcessor validator, ExceptionHandler exceptionHandler) {
    this.args = args;
    this.commandProcessor = validator;
    this.exceptionHandler = exceptionHandler;
  }

  void execute() {
    commandProcessor.process(args);
  }

  public static void main(String[] args) {
    AppComponent appComponent = DaggerAppComponent.builder()
        .appModule(new AppModule(args))
        .build();
    EasyCertCreatorMain main = appComponent.getMainApp();
    main.execute();
  }
}
