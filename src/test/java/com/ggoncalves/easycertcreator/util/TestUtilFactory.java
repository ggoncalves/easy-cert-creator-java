package com.ggoncalves.easycertcreator.util;

import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TestUtilFactory {

  @SuppressWarnings("unused")
  static Stream<Exception> allFileExceptionsProvider() {
    return Stream.of(
        new InvalidFileException("InvalidFileException"),
        new FilePermissionException("FilePermissionException")
                    );
  }

  @SneakyThrows
  public static ResultFileContent getResultFileContent(Path baseDir, String filename, String... lines) {
    return getResultFileContent(baseDir, filename, Arrays.asList(lines));
  }

  @SneakyThrows
  public static ResultFileContent getResultFileContent(Path baseDir, String filename, List<String> lines) {
    Path file = baseDir.resolve(filename);
    Files.write(file, lines);
    return new ResultFileContent(file, lines);
  }

  @Data
  @AllArgsConstructor
  public static class ResultFileContent {
    private Path file;
    private List<String> content;
  }

  public static List<String> createListFileContentStartingBlank() {
    List<String> lines = new ArrayList<>();
    lines.add("  ");
    lines.add("#COMMON_FIELDS:eventName,date,workload,location");
    lines.add("#FIELDS:studentName,email,studentId");
    lines.add("#SEPARATOR:;");
    lines.add("  ");
    lines.add("Java Workshop;05/15/2023;40 hours;San Francisco");
    lines.add("  ");
    lines.add("John Smith;john@email.com;12345");
    lines.add("Jane Doe;jane@email.com;67890");
    return lines;
  }

  public static List<String> createListFileContentNoHeaders() {
    List<String> lines = new ArrayList<>();
    lines.add("  ");
    lines.add("Java Workshop;05/15/2023;40 hours;San Francisco");
    lines.add("  ");
    lines.add("John Smith;john@email.com;12345");
    lines.add("Jane Doe;jane@email.com;67890");
    return lines;
  }

  public static List<String> createListFileContent() {
    List<String> lines = new ArrayList<>();
    lines.add("#COMMON_FIELDS:eventName,date,workload,location");
    lines.add("#FIELDS:studentName,email,studentId");
    lines.add("#SEPARATOR:;");
    lines.add("  ");
    lines.add("Java Workshop;05/15/2023;40 hours;San Francisco");
    lines.add("  ");
    lines.add("John Smith;john@email.com;12345");
    lines.add("Jane Doe;jane@email.com;67890");
    return lines;
  }

  public static List<String> createSimpleFileContent() {
    List<String> lines = new ArrayList<>();
    lines.add("#FIELDS:name,role,company,event,date");
    lines.add("#SEPARATOR:;");
    lines.add("  ");
    lines.add("John Smith;Developer;TechCorp;Java Workshop;05/15/2023");
    return lines;
  }
}
