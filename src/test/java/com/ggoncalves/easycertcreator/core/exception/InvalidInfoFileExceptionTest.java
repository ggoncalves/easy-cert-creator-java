package com.ggoncalves.easycertcreator.core.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

class InvalidInfoFileExceptionTest {

  @Test
  void printUsage() {
    List<String> errors = List.of("Data inválida: 45/22/2022", "Hora no formato inválido: 11.2");
    InvalidInfoFileException invalidInfoFileException = InvalidInfoFileException.builder()
        .errors(errors)
        .build();
    invalidInfoFileException.printException();
  }
}