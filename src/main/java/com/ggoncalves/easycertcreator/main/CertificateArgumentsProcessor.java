package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;
import lombok.Data;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Data
public class CertificateArgumentsProcessor {

  private CommandProcessor commandProcessor;
  private CertificateFileValidator certificateFileValidator;

  @Inject
  public CertificateArgumentsProcessor(CertificateCommandOptions certificateCommandOptions, CertificateFileValidator certificateFileValidator) {
    this.commandProcessor = certificateCommandOptions.getCommandProcessor();
    this.certificateFileValidator = certificateFileValidator;
  }

  public void process(@NotNull String[] args) {
    try {
      CommandLine cmd = commandProcessor.parseArgs(args);
      certificateFileValidator.validateFiles(cmd);
    }
    catch (ParseException | InvalidFileException | FilePermissionException e) {
      System.err.println("EasyCertCreator error found: " + e.getMessage());
      commandProcessor.printHelp("EasyCertCreator");
    }
  }
}