package com.ggoncalves.easycertcreator.core;

import com.ggoncalves.easycertcreator.core.exception.InvalidInfoFileException;
import com.ggoncalves.easycertcreator.model.CertificationInfo;
import com.google.common.annotations.VisibleForTesting;
import lombok.Builder;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
public class CertificationInfoFileReader implements EasyCertFileReader<CertificationInfo, InvalidInfoFileException> {
  public static final String DATE_FORMAT_ddMMyyyy = "dd/MM/yyyy";

  private File file;

  @Builder.Default
  private List<String> errors = new ArrayList<>();

  @Override
  public CertificationInfo read() throws InvalidInfoFileException {
    List<String> lines = readLines();
    if (lines.isEmpty()) {
      errors.add("O arquivo está vazio");
      throw InvalidInfoFileException.builder().errors(errors).build();
    }
    return CertificationInfo.builder()
        .programName(readProgramName(lines))
        .durationHours(readDurationHours(lines))
        .programDate(readProgramDate(lines))
        .students(readStudents(lines))
        .build();
  }

  private String readProgramName(List<String> lines) {
    return lines.get(0);
  }

  private Integer readDurationHours(List<String> lines) {
    return Integer.valueOf(lines.get(1));
  }

  @SneakyThrows
  private Date readProgramDate(List<String> lines) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_ddMMyyyy);
    return dateFormat.parse(lines.get(2));
  }

  private List<String> readStudents(List<String> lines) {
    return lines.subList(3, lines.size());
  }

  @VisibleForTesting
  List<String> readLines() {
    try {
      return Files.readAllLines(Path.of(file.getPath()), StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
