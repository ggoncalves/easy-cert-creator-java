package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.ggutils.console.cli.CommandProcessor;
import com.ggoncalves.ggutils.console.validation.FilePathValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CertificateCommandOptionsTest {

  @Mock
  private FilePathValidator filePathValidator;

  @Mock
  private CommandProcessor commandProcessor;

  @BeforeEach
  void beforeEach() {
    doReturn(commandProcessor)
        .when(commandProcessor)
        .addRequiredOption(anyString(), anyString(), anyBoolean(), anyString());
  }

  @AfterEach
  void afterEach() {
    reset(commandProcessor);
  }

  @DisplayName("Should have inject constructor")
  @Test
  void shouldHaveInjectConstructor() {
    CertificateCommandOptions certificateCommandOptions = new CertificateCommandOptions(commandProcessor);

    assertThat(certificateCommandOptions).isNotNull();
    assertThat(certificateCommandOptions.getCommandProcessor()).isEqualTo(commandProcessor);
  }

  @DisplayName("Should add Required Options on Constructor")
  @Test
  void shouldAddRequiredOptionsOnConstructor() {
    // Given
    CommandProcessor commandProcessor = spy(new CommandProcessor(filePathValidator));

    // When
    assertThat(new CertificateCommandOptions(commandProcessor)).isNotNull();

    // Then
    verify(commandProcessor).addRequiredOption(eq("c"), anyString(), eq(true), anyString());
    verify(commandProcessor).addRequiredOption(eq("i"), anyString(), eq(true), anyString());
    verify(commandProcessor).addRequiredOption(eq("o"), anyString(), eq(true), anyString());
    verifyNoMoreInteractions(commandProcessor);
  }
}