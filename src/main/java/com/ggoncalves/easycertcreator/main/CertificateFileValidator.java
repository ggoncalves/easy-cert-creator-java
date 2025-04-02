package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;
import lombok.Data;
import org.apache.commons.cli.CommandLine;

import javax.inject.Inject;

@Data
public class CertificateFileValidator {

  private final CommandProcessor commandProcessor;

  @Inject
  public CertificateFileValidator(CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
  }

  public void validateFiles(CommandLine cmd) throws InvalidFileException, FilePermissionException {
    commandProcessor.validateInputFile(cmd.getOptionValue("c"), "Jasper template");
    commandProcessor.validateInputFile(cmd.getOptionValue("i"), "Certificate info");
    commandProcessor.validateOutputDir(cmd.getOptionValue("o"));
  }
}