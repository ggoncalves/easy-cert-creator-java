package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Data
public class CertificateCommandOptions {

  private CommandProcessor commandProcessor;
  private CertificateFileValidator certificateFileValidator;

  @Inject
  public CertificateCommandOptions(@NotNull CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
    this.configureOptions();
  }

  private void configureOptions() {
    commandProcessor
        .addRequiredOption("c", "jasperfile", true, "Compiled Jasper Template File")
        .addRequiredOption("i", "certinfofile", true, "Certification File Info (.txt file)")
        .addRequiredOption("o", "output", true, "Output directory to save .pdf files");
  }
}