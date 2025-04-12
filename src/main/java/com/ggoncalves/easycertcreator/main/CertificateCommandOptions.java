package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Data
@Singleton
public class CertificateCommandOptions {

  private CommandProcessor commandProcessor;

  @Inject
  public CertificateCommandOptions(@NotNull CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
    this.configureOptions();
  }

  private void configureOptions() {
    commandProcessor
        .addRequiredOption("c", "jasperfile", true, "Compiled Jasper Template File")
        .addRequiredOption("i", "infofile", true, "File with data in supported format (LIST or SIMPLE structure, auto-detected)")
        .addRequiredOption("o", "output", true, "Output directory to save .pdf files")
        .addOption("name", "filename", true, "File name to use for generated .pdf files");
  }
}