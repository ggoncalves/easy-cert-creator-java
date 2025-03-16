package com.ggoncalves.easycertcreator.core.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class InvalidInfoFileException extends PrintableErrorException {

  private List<String> errors;

  @Override
  public void printException() {
    StringBuilder sb = new StringBuilder();
    sb.append("Erros encontrados: ").append(System.lineSeparator());
    String result = errors.stream()
        .collect(Collectors.joining(System.lineSeparator()));
    sb.append(result);
    System.err.println(sb);
    printUsage();
  }

  private void printUsage() {
    String expectedFormat = "Formato esperado: " + System.lineSeparator() +
        "<Nome do Programa> " + System.lineSeparator() +
        "<Duração do Programa em horas (apenas números))> " + System.lineSeparator() +
        "<Data de Conclusão do Programa (dia/mes/ano))> " + System.lineSeparator() +
        "<Nome do Aluno 1> " + System.lineSeparator() +
        "<Nome do Aluno 2> " + System.lineSeparator() +
        "<Nome do Aluno 3> " + System.lineSeparator() +
        "<Nome do Aluno N> " + System.lineSeparator() +
        "======= " + System.lineSeparator() +
        "Exemplo: " + System.lineSeparator() +
        "Orinnova Lego Serious Play" + System.lineSeparator() +
        "10" + System.lineSeparator() +
        "24/11/2024" + System.lineSeparator() +
        "João Silva" + System.lineSeparator() +
        "Maria Santos" + System.lineSeparator() +
        "Pedro Oliveira" + System.lineSeparator();
    System.out.println(expectedFormat);
  }
}
