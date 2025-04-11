package com.ggoncalves.easycertcreator.di;

import com.ggoncalves.easycertcreator.core.CertificateCreator;
import com.ggoncalves.easycertcreator.core.parser.TableContentFileParser;
import com.ggoncalves.easycertcreator.main.CertificateArgumentsProcessor;
import com.ggoncalves.easycertcreator.main.CertificateCommandOptions;
import com.ggoncalves.easycertcreator.main.CertificateFileValidator;
import com.ggoncalves.ggutils.console.cli.CommandProcessor;
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
  public CommandProcessor provideCommandProcessor(FilePathValidator filePathValidator) {
    return new CommandProcessor(filePathValidator);
  }

  @Provides
  @Singleton
  public TableContentFileParser provideTableContentFileParser() {
    return new TableContentFileParser();
  }

  @Provides
  @Singleton
  public CertificateCreator provideReportExporter(TableContentFileParser tableContentFileParser) {
    return new CertificateCreator(tableContentFileParser);
  }

  @Provides
  @Singleton
  public CertificateArgumentsProcessor provideCommandListArgsProcessor(CertificateCommandOptions certificateCommandOptions
      , CertificateFileValidator certificateFileValidator) {
    return new CertificateArgumentsProcessor(certificateCommandOptions, certificateFileValidator);
  }
}
