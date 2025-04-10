package com.ggoncalves.easycertcreator.core;

import com.google.common.annotations.VisibleForTesting;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.ggoncalves.easycertcreator.core.FileHeaderField.FIELDS_SEPARATOR;
import static com.ggoncalves.easycertcreator.core.FileHeaderField.HEADER_CHAR;
import static com.ggoncalves.easycertcreator.core.FileHeaderField.HEADER_ELEMENT_SEPARATOR;
import static com.ggoncalves.easycertcreator.core.FileHeaderField.HEADER_SEPARADOR;

@Data
abstract class BaseFileParser<T extends Content> implements ContentParser<T> {

  private String separator = ";";
  private Map<String, String> headers = new HashMap<>();

  // Define the pattern as a static final field for reuse and compilation
  private static final String HEADER_REGEX_PATTERN_STRING = "^#[^:]+:[^:]+$";
  private static final Pattern HEADER_PATTERN = Pattern.compile(HEADER_REGEX_PATTERN_STRING);

  protected List<String> readAllLines(String filePath) throws IOException {
    return Files.readAllLines(Path.of(filePath));
  }

  @VisibleForTesting
  boolean isHeaderLine(@NotNull String line) {
    // is header only if line starts with "#" and contains at least one char + ":" + plus at least one char
    if (line.startsWith(HEADER_CHAR.getValue())) {
      return HEADER_PATTERN.matcher(line).matches();
    }
    return false;
  }

  protected List<String> getHeaderElementsFor(String header) {
    return List.of(headers.get(header).split(HEADER_ELEMENT_SEPARATOR.getValue()));
  }

  private void parseHeaderLine(String line) {
    String[] parts = line.substring(1).split(HEADER_SEPARADOR.getValue(), 2);
    if (parts.length == 2) {
      addHeader(parts[0].trim(), parts[1].trim());
    }
  }

  private void addHeader(String header, String value) {
    headers.put(header, value);
    if (header.equals(FIELDS_SEPARATOR.getValue())) {
      this.setSeparator(value);
    }
  }

  protected void parseHeaders(List<String> lines) {
    lines.stream()
        .filter(this::isHeaderLine)
        .forEach(this::parseHeaderLine);
  }

  public boolean containsHeader(@NotNull String header) {
    return headers.containsKey(header);
  }
}