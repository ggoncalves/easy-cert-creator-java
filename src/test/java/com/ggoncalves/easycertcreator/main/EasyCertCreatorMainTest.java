package com.ggoncalves.easycertcreator.main;

import com.ggoncalves.ggutils.console.exception.ExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class EasyCertCreatorMainTest {
  @Mock
  private CommandListArgsProcessor validator;

  @Mock
  private ExceptionHandler exceptionHandler;

  @InjectMocks
  private EasyCertCreatorMain main;

  @Test
  void shouldValidateArguments() {
    // Given
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};
    main = newInstance(args);

    // When
    main.execute();

    // Then
    verify(validator).process(args);
    verifyNoInteractions(exceptionHandler);
  }

  private EasyCertCreatorMain newInstance(String[] args) {
    return new EasyCertCreatorMain(args, validator, exceptionHandler);
  }
}