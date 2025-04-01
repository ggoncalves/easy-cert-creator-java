package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;
import com.ggoncalves.ggutils.console.validation.FilePathValidator;
import lombok.Data;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import javax.inject.Inject;

@Data
public class CommandListArgsProcessor {

  private CommandProcessor commandProcessor;

  @Inject
  public CommandListArgsProcessor(FilePathValidator filePathValidator) {
    this.commandProcessor = new CommandProcessor(filePathValidator);
    this.init();
  }


  public void init() {
    commandProcessor
        .addRequiredOption("c", "jasperfile", true, "Compiled Jasper Template File")
        .addRequiredOption("i", "certinfofile", true, "Certification File Info (.txt file)")
        .addRequiredOption("o", "output", true, "Output directory to save .pdf files");
  }


  public void process(String[] args) {
    try {
      CommandLine cmd = commandProcessor.parseArgs(args);
      validateFiles(cmd);
    }
    catch (ParseException | InvalidFileException | FilePermissionException e) {
      System.err.println("EasyCertCreator error found: " + e.getMessage());
      commandProcessor.printHelp("EasyCertCreator");
    }
  }

  protected void validateFiles(CommandLine cmd) {
    commandProcessor.validateInputFile(cmd.getOptionValue("c"), "Jasper template");
    commandProcessor.validateInputFile(cmd.getOptionValue("i"), "Certificate info");
    commandProcessor.validateOutputDir(cmd.getOptionValue("o"));
  }
}