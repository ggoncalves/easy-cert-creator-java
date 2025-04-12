package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.core.CertificateCreator;
import com.ggoncalves.easycertcreator.core.logic.CertificateFileConfiguration;
import com.ggoncalves.easycertcreator.di.AppComponent;
import com.ggoncalves.ggutils.console.exception.ExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EasyCertCreatorMainTest {
  @Mock
  private CertificateArgumentsProcessor commandListArgsProcessor;

  @Mock
  private ExceptionHandler exceptionHandler;

  @InjectMocks
  @Spy
  private EasyCertCreatorMain main;

  @Mock
  private CertificateCreator certificateCreator;

  @Mock
  private AppComponent appComponent;

  @Mock
  private CertificateFileConfiguration certificateFileConfiguration;

  @DisplayName("Should Validade and Process Arguments Successfully")
  @Test
  void shouldValidateAndProcessArgumentsSuccessfully() {
    // Given
    Optional<CertificateFileConfiguration> fileLocationsOptional = Optional.of(certificateFileConfiguration);
    doReturn(fileLocationsOptional).when(commandListArgsProcessor).process(any());
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};
    main = newInstance(args);

    // When
    main.execute();

    // Then
    verify(commandListArgsProcessor).process(args);
    verify(certificateCreator).create(certificateFileConfiguration);
    verifyNoInteractions(exceptionHandler);
  }

  @DisplayName("Should do nothing when unable to parse file locations")
  @Test
  void shouldDoNothingWhenUnableToParseFileLocations() {
    // Given
    Optional<CertificateFileConfiguration> fileLocationsOptional = Optional.empty();
    doReturn(fileLocationsOptional).when(commandListArgsProcessor).process(any());
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};
    main = newInstance(args);

    // When
    main.execute();

    // Then
    verify(commandListArgsProcessor).process(args);
    verifyNoInteractions(certificateCreator);
    verifyNoInteractions(exceptionHandler);
  }

  @DisplayName("Should call exception handler ")
  @ParameterizedTest
  @MethodSource("com.ggoncalves.easycertcreator.util.TestUtilFactory#allFileExceptionsProvider")
  void shouldCallExceptionHandler(Exception exception) {
    // Given
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};

    doThrow(exception).when(commandListArgsProcessor).process(args);
    main = newInstance(args);

    // When
    main.execute();

    // Then
    verify(commandListArgsProcessor).process(args);
    verifyNoInteractions(certificateCreator);
    verify(exceptionHandler).handle(exception);
  }

  @DisplayName("Should execute main properly")
  @Test
  void shouldExecuteMainProperly() {
    String[] testArgs = {"arg1", "arg2"};

    try (MockedStatic<EasyCertCreatorMain> mockedStatic = mockStatic(EasyCertCreatorMain.class)) {
      // Given
      mockedStatic.when(() -> EasyCertCreatorMain.createAppComponent(testArgs))
          .thenReturn(appComponent);
      mockedStatic.when(() -> EasyCertCreatorMain.main(testArgs))
          .thenCallRealMethod();
      when(appComponent.getMainApp()).thenReturn(main);

      // When
      EasyCertCreatorMain.main(testArgs);

      // Then
      verify(appComponent).getMainApp();
      verify(main).execute();
    }
  }

  private EasyCertCreatorMain newInstance(String[] args) {
    return new EasyCertCreatorMain(args, commandListArgsProcessor, certificateCreator, exceptionHandler);
  }
}