package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.util.TestEnvironmentSilencer;
import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;
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

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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

  private CertificateArgumentsProcessor certificateArgumentsProcessor;

  @Mock
  private CertificateCommandOptions certificateCommandOptions;

  @BeforeEach
  void beforeEach() {
    doReturn(commandProcessor).when(certificateCommandOptions).getCommandProcessor();

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
    certificateArgumentsProcessor.process(args);

    // Then
    verify(commandProcessor).parseArgs(args);
    verify(certificateFileValidator).validateFiles(commandLine);
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
    certificateArgumentsProcessor.process(args);

    // Then
    verify(commandProcessor).parseArgs(args);
    verify(commandProcessor).printHelp("EasyCertCreator");
  }

  @SneakyThrows
  @DisplayName("Should throw InvalidFileException during validation and print message")
  @ParameterizedTest
  @MethodSource("exceptionProvider")
  void shouldThrowInvalidFileExceptionDuringValidationAndPrintMessage(Exception exception) {
    // Given
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};
    CommandLine commandLine = mock(CommandLine.class);
    doReturn(commandLine).when(commandProcessor).parseArgs(args);

    doThrow(exception)
        .when(certificateFileValidator)
        .validateFiles(commandLine);

    // When
    certificateArgumentsProcessor.process(args);

    // Then
    verify(commandProcessor).parseArgs(args);
    verify(certificateFileValidator).validateFiles(commandLine);
    verify(commandProcessor).printHelp("EasyCertCreator");
  }

  static Stream<Exception> exceptionProvider() {
    return Stream.of(
        new InvalidFileException("InvalidFileException"),
        new FilePermissionException("FilePermissionException")
                    );
  }

}