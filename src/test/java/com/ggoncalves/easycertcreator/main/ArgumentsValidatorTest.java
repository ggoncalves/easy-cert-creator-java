package com.ggoncalves.easycertcreator.main;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ArgumentsValidatorTest {

  @Spy
  private ArgumentsValidator argumentsValidator;

  // System.err and out replacements
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private static final PrintStream ORIGINAL_OUT = System.out;
  private static final PrintStream ORIGINAL_ERR = System.err;

  private static final String VALID_JASPER = "template.jasper";
  private static final String VALID_INFO = "info.txt";
  private static final String VALID_OUTPUT = "/tmp/output";

  private static final String EXPECTED_DOES_NOT_EXIST_MESSAGE = "does not exist";
  private static final String EXPECTED_MUST_BE_A_DIRECTORY_MESSAGE = "must be a directory";
  private static final String EXPECTED_CANNOT_WRITE_TO_DIRECTORY = "Cannot write to the output directory";
  private static final String EXPECTED_CANNOT_READ_FILE = "Cannot read the";
  private static final String EXPECTED_DOES_NOT_A_FILE_MESSAGE = "must be a file";
  private static final String EXPECTED_USAGE_MESSAGE = "usage: EasyCertCreator";
  private static final String EXPECTED_UNRECOGNIZED_OPTION = "Unrecognized option:";
  private static final String EXPECTED_MISSING_REQUIRED_OPTION = "Missing required option:";

  @BeforeEach
  void setup() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @AfterAll
  static void resetPrintStream() {
    System.setOut(ORIGINAL_OUT);
    System.setErr(ORIGINAL_ERR);
  }

  @Test
  void testValidArguments() {
    String[] args = {
        "-c", VALID_JASPER,
        "-i", VALID_INFO,
        "-o", VALID_OUTPUT
    };
    File mockFileValid = prepareValidFile();
    File mockDirectoryValid = prepareValidDirectory();

    prepareArgumentsValidatorReturns(mockFileValid, mockFileValid, mockDirectoryValid);

    assertThat(argumentsValidator.isValidArguments(args)).isTrue();
    assertThat(outContent.toString()).isEmpty();
  }

  @Test
  void testValidArgumentsFileNotFoundJasper() {
    String[] args = {
        "-c", VALID_JASPER,
        "-i", VALID_INFO,
        "-o", VALID_OUTPUT
    };
    File mockFileNotFound = prepareMockFile(false, false, false);
    File mockFileValid = prepareValidFile();
    File mockDirectoryValid = prepareValidDirectory();

    prepareArgumentsValidatorReturns(mockFileNotFound, mockFileValid, mockDirectoryValid);
    assertThat(argumentsValidator.isValidArguments(args)).isFalse();
    assertError(EXPECTED_DOES_NOT_EXIST_MESSAGE);
  }

  @Test
  void testValidArgumentsNotAnActualFileValidInfo() {
    String[] args = {
        "-c", VALID_JASPER,
        "-i", VALID_INFO,
        "-o", VALID_OUTPUT
    };
    File mockFileNotAnActualFile = prepareMockFile(true, false, true);
    File mockFileValid = prepareValidFile();
    File mockDirectoryValid = prepareValidDirectory();

    prepareArgumentsValidatorReturns(mockFileValid, mockFileNotAnActualFile, mockDirectoryValid);
    assertThat(argumentsValidator.isValidArguments(args)).isFalse();
    assertError(EXPECTED_DOES_NOT_A_FILE_MESSAGE);
  }

  @Test
  void testValidArgumentsFileCannotRead() {
    String[] args = {
        "-c", VALID_JASPER,
        "-i", VALID_INFO,
        "-o", VALID_OUTPUT
    };
    File mockFileNotAnActualFile = prepareMockFile(true, true, false);
    File mockFileValid = prepareValidFile();
    File mockDirectoryValid = prepareValidDirectory();

    prepareArgumentsValidatorReturns(mockFileValid, mockFileNotAnActualFile, mockDirectoryValid);
    assertThat(argumentsValidator.isValidArguments(args)).isFalse();
    assertError(EXPECTED_CANNOT_READ_FILE);
  }

  @Test
  void testValidArgumentsOutputDirectoryNotFound() {
    String[] args = {
        "-c", VALID_JASPER,
        "-i", VALID_INFO,
        "-o", VALID_OUTPUT
    };
    File mockFileValid = prepareValidFile();
    File mockDirectoryNotExists = prepareMockDirectory(false, true, true);

    prepareArgumentsValidatorReturns(mockFileValid, mockFileValid, mockDirectoryNotExists);
    assertThat(argumentsValidator.isValidArguments(args)).isFalse();
    assertError(EXPECTED_DOES_NOT_EXIST_MESSAGE);
  }

  @Test
  void testValidArgumentsOutputDirectoryNotAnActualDirectory() {
    String[] args = {
        "-c", VALID_JASPER,
        "-i", VALID_INFO,
        "-o", VALID_OUTPUT
    };
    File mockFileValid = prepareValidFile();
    File mockDirectoryNotAnActualDirectory = prepareMockDirectory(true, false, true);

    prepareArgumentsValidatorReturns(mockFileValid, mockFileValid, mockDirectoryNotAnActualDirectory);
    assertThat(argumentsValidator.isValidArguments(args)).isFalse();
    assertError(EXPECTED_MUST_BE_A_DIRECTORY_MESSAGE);
  }

  @Test
  void testValidArgumentsOutputDirectoryCannotWrite() {
    String[] args = {
        "-c", VALID_JASPER,
        "-i", VALID_INFO,
        "-o", VALID_OUTPUT
    };
    File mockFileValid = prepareValidFile();
    File mockDirectoryNotAnActualDirectory = prepareMockDirectory(true, true, false);

    prepareArgumentsValidatorReturns(mockFileValid, mockFileValid, mockDirectoryNotAnActualDirectory);
    assertThat(argumentsValidator.isValidArguments(args)).isFalse();
    assertError(EXPECTED_CANNOT_WRITE_TO_DIRECTORY);
  }

  @Test
  void testMissingRequiredArgument() {
    String[] args = {
        "-c", VALID_JASPER,
        "-i", VALID_INFO
        // missing output argument
    };
    assertThat(argumentsValidator.isValidArguments(args)).isFalse();
    assertError(EXPECTED_MISSING_REQUIRED_OPTION);
  }

  @Test
  void testInvalidOptionName() {
    String[] args = {
        "-x", VALID_JASPER,
        "-i", VALID_INFO,
        "-o", VALID_OUTPUT
    };
    assertThat(argumentsValidator.isValidArguments(args)).isFalse();
    assertError(EXPECTED_UNRECOGNIZED_OPTION);
  }

  private File prepareValidFile() {
    return prepareMockFile(true, true, true);
  }

  private File prepareValidDirectory() {
    return prepareMockDirectory(true, true, true);
  }

  private File prepareMockFile(Boolean fileExists, Boolean isFile, Boolean canRead) {
    File mockedFile = mock(File.class);
    lenient().when(mockedFile.exists()).thenReturn(fileExists);
    lenient().when(mockedFile.isFile()).thenReturn(isFile);
    lenient().when(mockedFile.canRead()).thenReturn(canRead);
    return mockedFile;
  }

  private File prepareMockDirectory(Boolean fileExists, Boolean isDirectory, Boolean canWrite) {
    File mockedFile = mock(File.class);
    lenient().when(mockedFile.exists()).thenReturn(fileExists);
    lenient().when(mockedFile.isDirectory()).thenReturn(isDirectory);
    lenient().when(mockedFile.canWrite()).thenReturn(canWrite);
    return mockedFile;
  }

  private void prepareArgumentsValidatorReturns(File jasperFile, File infoFile, File outputDirectory) {
    lenient().doReturn(jasperFile).when(argumentsValidator).getFileForPath(VALID_JASPER);
    lenient().doReturn(infoFile).when(argumentsValidator).getFileForPath(VALID_INFO);
    lenient().doReturn(outputDirectory).when(argumentsValidator).getFileForPath(VALID_OUTPUT);
  }

  private void assertError(String expectedErrorContent) {
    assertThat(outContent.toString()).isNotEmpty();
    assertThat(outContent.toString()).contains(EXPECTED_USAGE_MESSAGE);
    assertThat(errContent.toString()).contains(expectedErrorContent);
  }
}