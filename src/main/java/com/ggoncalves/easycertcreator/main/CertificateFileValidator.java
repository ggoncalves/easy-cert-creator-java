package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.core.exception.InvalidFilenameConfiguration;
import com.ggoncalves.easycertcreator.core.logic.CertificateFileConfiguration;
import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;
import com.google.common.annotations.VisibleForTesting;
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

  public CertificateFileConfiguration validateAndRetrieveCertificateFiles(CommandLine cmd) throws InvalidFileException, FilePermissionException {
    CertificateFileConfiguration certificateFileConfiguration = retrieveCertificateFileLocations(cmd);
    commandProcessor.validateInputFile(certificateFileConfiguration.jasperTemplateFilePath(), "Jasper template");
    commandProcessor.validateInputFile(certificateFileConfiguration.certificateInfoFilePath(), "Certificate info");
    commandProcessor.validateOutputDir(certificateFileConfiguration.outputDir());
    validateCertificateFilename(certificateFileConfiguration);
    return certificateFileConfiguration;
  }

  @VisibleForTesting
  void validateCertificateFilename(CertificateFileConfiguration certificateFileConfiguration) throws InvalidFilenameConfiguration {
    if (certificateFileConfiguration.certificateFileName() != null) {
      if (!certificateFileConfiguration.certificateFileName().matches("^[^\\\\/:*?\"<>|]+$")) {
        throw new InvalidFilenameConfiguration("Invalid filename: " + certificateFileConfiguration.certificateFileName());
      }
    }
  }

  private CertificateFileConfiguration retrieveCertificateFileLocations(CommandLine cmd) {
    return new CertificateFileConfiguration(
        cmd.getOptionValue("c"),
        cmd.getOptionValue("i"),
        cmd.getOptionValue("o"),
        cmd.getOptionValue("name"));
  }
}