package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.core.CertificateCreator;
import com.ggoncalves.easycertcreator.di.AppComponent;
import com.ggoncalves.easycertcreator.di.AppModule;
import com.ggoncalves.easycertcreator.di.DaggerAppComponent;
import com.ggoncalves.ggutils.console.exception.ExceptionHandler;
import com.google.common.annotations.VisibleForTesting;

import javax.inject.Inject;

public class EasyCertCreatorMain {

  private final String[] args;
  private final CertificateArgumentsProcessor commandListArgsProcessor;
  private final CertificateCreator certificateCreator;
  private final ExceptionHandler exceptionHandler;

  @Inject
  public EasyCertCreatorMain(String[] args, 
                            CertificateArgumentsProcessor commandListArgsProcessor,
                            CertificateCreator certificateCreator,
                            ExceptionHandler exceptionHandler) {
    this.args = args;
    this.commandListArgsProcessor = commandListArgsProcessor;
    this.certificateCreator = certificateCreator;
    this.exceptionHandler = exceptionHandler;
  }

  void execute() {
    try {
      commandListArgsProcessor.process(args)
          .ifPresent(certificateCreator::create);
    }
    catch (Throwable e) {
      exceptionHandler.handle(e);
    }
  }

  @VisibleForTesting
  static AppComponent createAppComponent(String[] args) {
    return DaggerAppComponent.builder()
        .appModule(new AppModule(args))
        .build();
  }

  public static void main(String[] args) {
    AppComponent appComponent = createAppComponent(args);
    EasyCertCreatorMain main = appComponent.getMainApp();
    main.execute();
  }
}
