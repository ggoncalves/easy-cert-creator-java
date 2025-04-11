package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.core.logic.CertificateFileLocations;
import com.ggoncalves.easycertcreator.util.TestEnvironmentSilencer;
import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import lombok.SneakyThrows;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CertificateArgumentsProcessorTest implements TestEnvironmentSilencer {

  @Mock
  private CommandProcessor commandProcessor;

  @Mock
  private CertificateFileValidator certificateFileValidator;

  @Mock
  private CertificateFileLocations certificateFileLocations;

  private CertificateArgumentsProcessor certificateArgumentsProcessor;

  @Mock
  private CertificateCommandOptions certificateCommandOptions;

  @BeforeEach
  void beforeEach() {
    doReturn(commandProcessor).when(certificateCommandOptions).getCommandProcessor();
    lenient().doReturn(certificateFileLocations).when(certificateFileValidator).validateAndRetrieveCertificateFiles(any(CommandLine.class));

    certificateArgumentsProcessor = spy(new CertificateArgumentsProcessor(
        certificateCommandOptions, certificateFileValidator));
  }

  @DisplayName("Should have inject constructor")
  @Test
  void shouldHaveInjectConstructor() {
    // When
    CertificateArgumentsProcessor certificateArgumentsProcessor = new CertificateArgumentsProcessor(
        certificateCommandOptions, certificateFileValidator);

    // Then
    assertThat(certificateArgumentsProcessor).isNotNull();
    assertThat(certificateArgumentsProcessor.getCommandProcessor()).isEqualTo(commandProcessor);
    assertThat(certificateArgumentsProcessor.getCertificateFileValidator()).isEqualTo(certificateFileValidator);
  }

  @SneakyThrows
  @DisplayName("Should process successfully")
  @Test
  void shouldProcessSuccessfully() {
    // Given
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};
    CommandLine commandLine = mock(CommandLine.class);
    doReturn(commandLine).when(commandProcessor).parseArgs(args);

    // When
    Optional<CertificateFileLocations> fileLocationsOptional = certificateArgumentsProcessor.process(args);

    // Then
    assertThat(fileLocationsOptional).isPresent();
    assertThat(fileLocationsOptional).get().isEqualTo(certificateFileLocations);

    verify(commandProcessor).parseArgs(args);
    verify(certificateFileValidator).validateAndRetrieveCertificateFiles(commandLine);
    verifyNoMoreInteractions(commandProcessor);
    verifyNoMoreInteractions(certificateFileValidator);
  }

  @SneakyThrows
  @DisplayName("Should throw ParseException and print message")
  @Test
  void shouldThrowParseExceptionAndPrintMessage() {
    // Given
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};
    doThrow(new ParseException("ParseException"))
        .when(commandProcessor)
        .parseArgs(args);

    // When
    Optional<CertificateFileLocations> fileLocationsOptional = certificateArgumentsProcessor.process(args);

    // Then
    assertThat(fileLocationsOptional).isNotPresent();
    verify(commandProcessor).parseArgs(args);
    verify(commandProcessor).printHelp("EasyCertCreator");
  }

  @SneakyThrows
  @DisplayName("Should throw InvalidFileException during validation and print message")
  @ParameterizedTest
  @MethodSource("com.ggoncalves.easycertcreator.util.TestUtilFactory#allFileExceptionsProvider")
  void shouldThrowInvalidFileExceptionDuringValidationAndPrintMessage(Exception exception) {
    // Given
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};
    CommandLine commandLine = mock(CommandLine.class);
    doReturn(commandLine).when(commandProcessor).parseArgs(args);

    doThrow(exception)
        .when(certificateFileValidator)
        .validateAndRetrieveCertificateFiles(commandLine);

    // When
    assertThatThrownBy(() ->
        certificateArgumentsProcessor.process(args)).isInstanceOf(exception.getClass());

    // Then
    verify(commandProcessor).parseArgs(args);
    verify(certificateFileValidator).validateAndRetrieveCertificateFiles(commandLine);
//    verify(commandProcessor).printHelp("EasyCertCreator");
  }

}