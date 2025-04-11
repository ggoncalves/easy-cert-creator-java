package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.core.logic.CertificateFileLocations;
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

  public CertificateFileLocations validateAndRetrieveCertificateFiles(CommandLine cmd) throws InvalidFileException, FilePermissionException {
    CertificateFileLocations certificateFileLocations = retrieveCertificateFileLocations(cmd);
    commandProcessor.validateInputFile(certificateFileLocations.jasperTemplateFilePath(), "Jasper template");
    commandProcessor.validateInputFile(certificateFileLocations.certificateInfoFilePath(), "Certificate info");
    commandProcessor.validateOutputDir(certificateFileLocations.outputDir());
    return certificateFileLocations;
  }

  private CertificateFileLocations retrieveCertificateFileLocations(CommandLine cmd) {
    return new CertificateFileLocations(
        cmd.getOptionValue("c"),
        cmd.getOptionValue("i"),
        cmd.getOptionValue("o"));
  }
}