package com.ggoncalves.easycertcreator;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EasyCertCreatorMainTest {
  @Mock
  private ArgumentsValidator validator;

  @InjectMocks
  private EasyCertCreatorMain main;

  @Test
  void shouldValidateArguments() {
    String[] args = {"-c", "file.jasper", "-i", "info.txt", "-o", "dir"};
    main.execute(args);
    verify(validator).isValidArguments(args);
  }
}