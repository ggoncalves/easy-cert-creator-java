package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.core.logic.CertificateFileLocations;
import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    CertificateFileLocations fileLocations = validator.validateAndRetrieveCertificateFiles(commandLine);

    // Then
    assertThat(fileLocations).isNotNull();
    assertThat(fileLocations.jasperTemplateFilePath()).isEqualTo(VALID_INPUT_FILE_C);
    assertThat(fileLocations.certificateInfoFilePath()).isEqualTo(VALID_INPUT_FILE_I);
    assertThat(fileLocations.outputDir()).isEqualTo(VALID_OUTPUT_DIR);

    verify(commandLine).getOptionValue(eq("c"));
    verify(commandLine).getOptionValue(eq("i"));
    verify(commandLine).getOptionValue(eq("o"));
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
    verifyNoMoreInteractions(commandLine);

    verify(commandProcessor).validateInputFile(eq(VALID_INPUT_FILE_C), anyString());
    verify(commandProcessor).validateInputFile(eq(VALID_INPUT_FILE_I), anyString());
    verify(commandProcessor).validateOutputDir(eq(VALID_OUTPUT_DIR));
    verifyNoMoreInteractions(commandProcessor);
  }

  private void prepareCommandLineMock() {
    lenient().doReturn(VALID_INPUT_FILE_C).when(commandLine).getOptionValue("c");
    lenient().doReturn(VALID_INPUT_FILE_I).when(commandLine).getOptionValue("i");
    lenient().doReturn(VALID_OUTPUT_DIR).when(commandLine).getOptionValue("o");
  }
}