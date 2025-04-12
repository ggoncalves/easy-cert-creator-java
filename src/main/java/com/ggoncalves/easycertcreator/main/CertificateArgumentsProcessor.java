package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.core.logic.CertificateFileConfiguration;
import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import lombok.Data;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Optional;

@Data
public class CertificateArgumentsProcessor {

  private CommandProcessor commandProcessor;
  private CertificateFileValidator certificateFileValidator;

  @Inject
  public CertificateArgumentsProcessor(CertificateCommandOptions certificateCommandOptions, CertificateFileValidator certificateFileValidator) {
    this.commandProcessor = certificateCommandOptions.getCommandProcessor();
    this.certificateFileValidator = certificateFileValidator;
  }

  public Optional<CertificateFileConfiguration> process(@NotNull String[] args) {
    try {
      CommandLine cmd = commandProcessor.parseArgs(args);
      return Optional.of(certificateFileValidator.validateAndRetrieveCertificateFiles(cmd));
    }
    catch (ParseException e) {
      System.err.println("EasyCertCreator error found: " + e.getMessage());
      commandProcessor.printHelp("EasyCertCreator");
    }
    return Optional.empty();
  }
}