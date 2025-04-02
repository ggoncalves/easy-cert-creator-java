package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.easycertcreator.di.AppComponent;
import com.ggoncalves.ggutils.console.exception.ExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
  private AppComponent appComponent;

  @DisplayName("Should Validade and Process Arguments Successfully")
  @Test
  void shouldValidateAndProcessArgumentsSuccessfully() {
    // Given
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};
    main = newInstance(args);

    // When
    main.execute();

    // Then
    verify(commandListArgsProcessor).process(args);
    verifyNoInteractions(exceptionHandler);
  }

  @DisplayName("ShouldExecuteMainProperly")
  @Test
  void shouldExecuteMainProperly() {
    String[] testArgs = {"arg1", "arg2"};

    try (MockedStatic<EasyCertCreatorMain> mockedStatic = mockStatic(EasyCertCreatorMain.class)) {
      // Only mock the createAppComponent method, allowing the real main method to execute
      mockedStatic.when(() -> EasyCertCreatorMain.createAppComponent(testArgs))
          .thenReturn(appComponent);

      // Call the original method for main
      mockedStatic.when(() -> EasyCertCreatorMain.main(testArgs))
          .thenCallRealMethod();

      // Set up the AppComponent mock
      when(appComponent.getMainApp()).thenReturn(main);

      // When
      EasyCertCreatorMain.main(testArgs);

      // Then
      verify(appComponent).getMainApp();
      verify(main).execute();
    }
  }



  private EasyCertCreatorMain newInstance(String[] args) {
    return new EasyCertCreatorMain(args, commandListArgsProcessor, exceptionHandler);
  }
}