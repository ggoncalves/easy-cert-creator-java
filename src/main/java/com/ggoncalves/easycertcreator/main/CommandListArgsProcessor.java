package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;
import com.ggoncalves.ggutils.console.validation.FilePathValidator;
import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommandListArgsProcessor {

  @Builder.Default
  private CommandProcessor commandProcessor = new CommandProcessor(new FilePathValidator());

  @Builder.Default
  private String jasperFileOption = "c";

  @Builder.Default
  private String certInfoFileOption = "i";

  @Builder.Default
  private String outputOption = "o";

  @Builder.Default
  private String appName = "EasyCertCreator";

  public void init() {
    commandProcessor
        .addRequiredOption(jasperFileOption, "jasperfile", true, "Compiled Jasper Template File")
        .addRequiredOption(certInfoFileOption, "certinfofile", true, "Certification File Info (.txt file)")
        .addRequiredOption(outputOption, "output", true, "Output directory to save .pdf files");
  }


  public void process(String[] args) {
    try {
      if (!hasOptions()) {
        init();
      }

      CommandLine cmd = commandProcessor.parseArgs(args);
      validateFiles(cmd);
    }
    catch (ParseException | InvalidFileException | FilePermissionException e) {
      System.err.println(appName + " error found: " + e.getMessage());
      commandProcessor.printHelp(appName);
    }
  }

  protected void validateFiles(CommandLine cmd) {
    commandProcessor.validateInputFile(cmd.getOptionValue(jasperFileOption), "Jasper template");
    commandProcessor.validateInputFile(cmd.getOptionValue(certInfoFileOption), "Certificate info");
    commandProcessor.validateOutputDir(cmd.getOptionValue(outputOption));
  }

  @VisibleForTesting
  boolean hasOptions() {
    try {
      commandProcessor.parseArgs(new String[]{});
      return true;
    }
    catch (ParseException e) {
      return e.getMessage().contains("Missing required option");
    }
  }
}
