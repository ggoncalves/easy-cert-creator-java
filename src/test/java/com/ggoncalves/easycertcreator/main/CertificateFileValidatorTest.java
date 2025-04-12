package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.core.exception.InvalidFilenameConfiguration;
import com.ggoncalves.easycertcreator.core.logic.CertificateFileConfiguration;
import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CertificateFileValidatorTest {

  @Mock
  private CommandProcessor commandProcessor;

  @Mock
  private CommandLine commandLine;

  @InjectMocks
  private CertificateFileValidator validator;

  private final static String VALID_INPUT_FILE_C = "validInputFileC";
  private final static String VALID_INPUT_FILE_I = "validInputFileI";
  private final static String VALID_OUTPUT_DIR = "validOutputDir";

  @AfterEach
  void afterEach() {
    reset(commandProcessor);
  }

  @DisplayName("Should have inject constructor")
  @Test
  void shouldHaveInjectConstructor() {
    CertificateFileValidator validator = new CertificateFileValidator(commandProcessor);

    assertThat(validator).isNotNull();
    assertThat(validator.getCommandProcessor()).isEqualTo(commandProcessor);
  }

  @DisplayName("Should validate files present in a command line successfully")
  @Test
  void shouldValidateAndRetrieveCertificateFilesPresentInACommandLineSuccessfully() {
    // Given
    prepareCommandLineMock();

    // When
    CertificateFileConfiguration fileLocations = validator.validateAndRetrieveCertificateFiles(commandLine);

    // Then
    assertThat(fileLocations).isNotNull();
    assertThat(fileLocations.jasperTemplateFilePath()).isEqualTo(VALID_INPUT_FILE_C);
    assertThat(fileLocations.certificateInfoFilePath()).isEqualTo(VALID_INPUT_FILE_I);
    assertThat(fileLocations.outputDir()).isEqualTo(VALID_OUTPUT_DIR);

    verify(commandLine).getOptionValue(eq("c"));
    verify(commandLine).getOptionValue(eq("i"));
    verify(commandLine).getOptionValue(eq("o"));
    verify(commandLine).getOptionValue(eq("name"));
    verifyNoMoreInteractions(commandLine);

    verify(commandProcessor).validateInputFile(eq(VALID_INPUT_FILE_C), anyString());
    verify(commandProcessor).validateInputFile(eq(VALID_INPUT_FILE_I), anyString());
    verify(commandProcessor).validateOutputDir(eq(VALID_OUTPUT_DIR));
    verifyNoMoreInteractions(commandProcessor);
  }

  @DisplayName("Should throws InvalidFileException and Stop")
  @Test
  void shouldThrowsInvalidFileExceptionAndStop() {
    prepareCommandLineMock();
    // Given
    doThrow(new InvalidFileException(""))
        .when(commandProcessor)
        .validateInputFile(eq(VALID_INPUT_FILE_C), anyString());

    // When
    assertThatThrownBy(() ->
        validator.validateAndRetrieveCertificateFiles(commandLine)).isInstanceOf(InvalidFileException.class);

    // Then
    assertThat(validator).isNotNull();

    verify(commandLine).getOptionValue(eq("c"));
    verify(commandLine).getOptionValue(eq("i"));
    verify(commandLine).getOptionValue(eq("o"));

    verify(commandProcessor).validateInputFile(eq(VALID_INPUT_FILE_C), anyString());
    verifyNoMoreInteractions(commandProcessor);
  }

  @DisplayName("Should throws FilePermissionException and Stop")
  @Test
  void shouldThrowsFilePermissionExceptionAndStop() {
    prepareCommandLineMock();
    // Given
    doThrow(new FilePermissionException(""))
        .when(commandProcessor)
        .validateOutputDir(eq(VALID_OUTPUT_DIR));

    // When
    assertThatThrownBy(() ->
        validator.validateAndRetrieveCertificateFiles(commandLine)).isInstanceOf(FilePermissionException.class);

    // Then
    assertThat(validator).isNotNull();

    verify(commandLine).getOptionValue(eq("c"));
    verify(commandLine).getOptionValue(eq("i"));
    verify(commandLine).getOptionValue(eq("o"));
    verify(commandLine).getOptionValue(eq("name"));
    verifyNoMoreInteractions(commandLine);

    verify(commandProcessor).validateInputFile(eq(VALID_INPUT_FILE_C), anyString());
    verify(commandProcessor).validateInputFile(eq(VALID_INPUT_FILE_I), anyString());
    verify(commandProcessor).validateOutputDir(eq(VALID_OUTPUT_DIR));
    verifyNoMoreInteractions(commandProcessor);
  }

  @DisplayName("Should validate a null certificate name")
  @Test
  void shouldValidateANullCertificateName() {
    validator.validateCertificateFilename(new CertificateFileConfiguration("jasper",
        "info", "dir", null));
  }

  @DisplayName("Should validate a certificate name")
  @ParameterizedTest
  @ValueSource(strings = {"valid_name", "valid-name_123", "v", "v$a", "$student$last", "Feição_just_$do"})
  void shouldValidateACertificateName(String validName) {
    validator.validateCertificateFilename(new CertificateFileConfiguration("jasper",
        "info", "dir", validName));
  }

  @DisplayName("Should throws InvalidNameConfiguration")
  @ParameterizedTest
  @ValueSource(strings = {
      // Contains invalid characters
      "file:name",         // Contains colon
      "file/name",         // Contains forward slash
      "file\\name",        // Contains backslash
      "file*name",         // Contains asterisk
      "file?name",         // Contains question mark
      "file\"name",        // Contains quote
      "file<name",         // Contains less than
      "file>name",         // Contains greater than
      "file|name"         // Contains pipe
  })
  void shouldThrowsInvalidNameConfiguration(String invalidName) {
    assertThatThrownBy(() -> validator.validateCertificateFilename(new CertificateFileConfiguration("jasper",
        "info", "dir", invalidName))).isInstanceOf(InvalidFilenameConfiguration.class);

  }

  private void prepareCommandLineMock() {
    lenient().doReturn(VALID_INPUT_FILE_C).when(commandLine).getOptionValue("c");
    lenient().doReturn(VALID_INPUT_FILE_I).when(commandLine).getOptionValue("i");
    lenient().doReturn(VALID_OUTPUT_DIR).when(commandLine).getOptionValue("o");
  }
}