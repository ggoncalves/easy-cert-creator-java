package com.ggoncalves.easycertcreator;

import com.google.common.annotations.VisibleForTesting;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.*;

import java.io.File;

@NoArgsConstructor
@Builder
public class ArgumentsValidator {

  boolean isValidArguments(String[] args) {
    Options options = buildOptions();
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine cmd = parser.parse(options, args);
      validateFiles(cmd);
    } catch (ParseException e) {
      System.err.println("EasyCertCreator error found: " + e.getMessage());
      new HelpFormatter().printHelp("EasyCertCreator", options, true);
      return false;
    }
    return true;
  }

  private Options buildOptions() {
    Options options = new Options();
    options.addRequiredOption("c", "jasperfile", true, "Compiled Jasper Template File");
    options.addRequiredOption("i", "certinfofile", true, "Certification File Info (.txt file)");
    options.addRequiredOption("o", "output", true, "Output directory to save .pdf files");
    return options;
  }

  private void validateFiles(CommandLine cmd) throws ParseException {
    validateInputFile(cmd.getOptionValue("jasperfile"), "Jasper template");
    validateInputFile(cmd.getOptionValue("certinfofile"), "Certificate info");
    validateOutputDir(cmd.getOptionValue("output"));
  }

  private void validateInputFile(String path, String fileType) throws ParseException {
    File file = getFileForPath(path);
    assertFileExists(path, file);
    assertIsFile(path, fileType, file);
    assertCanRead(path, fileType, file);
  }

  private static void assertCanRead(String path, String fileType, File file) throws ParseException {
    if (!file.canRead()) {
      throw new ParseException(String.format("Cannot read the %s file (check permissions): %s", fileType, path));
    }
  }

  private static void assertIsFile(String path, String fileType, File file) throws ParseException {
    if (!file.isFile()) {
      throw new ParseException(String.format("The %s path must be a file: %s", fileType, path));
    }
  }

  private void validateOutputDir(String path) throws ParseException {
    File dir = getFileForPath(path);
    assertFileExists(path, dir);
    assertIsDirectory(path, dir);
    assertCanWriteToDirectory(path, dir);
  }

  private static void assertCanWriteToDirectory(String path, File dir) throws ParseException {
    if (!dir.canWrite()) {
      throw new ParseException(String.format("Cannot write to the output directory (check permissions): %s", path));
    }
  }

  private static void assertIsDirectory(String path, File dir) throws ParseException {
    if (!dir.isDirectory()) {
      throw new ParseException(String.format("The output path must be a directory: %s", path));
    }
  }

  private static void assertFileExists(String path, File file) throws ParseException {
    if (!file.exists()) {
      throw new ParseException(String.format("The output directory does not exist: %s", path));
    }
  }

  @VisibleForTesting
  File getFileForPath(String path) {
    return new File(path);
  }
}
