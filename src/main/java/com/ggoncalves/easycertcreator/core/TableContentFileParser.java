package com.ggoncalves.easycertcreator.core;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Parser for files with LIST structure containing common fields and a list of items.
 * <p>
 * Example format:
 * #COMMON_FIELDS:eventName,date,workload,location
 * #FIELDS:studentName,email,studentId
 * #SEPARATOR:;
 * <p>
 * Java Workshop;05/15/2023;40 hours;San Francisco
 * <p>
 * John Smith;john@email.com;12345
 * Jane Doe;jane@email.com;67890
 */
@Slf4j
public class TableContentFileParser extends BaseFileParser<TableContent> {

  @Override
  public TableContent parse(String filePath) throws IOException {
    List<String> lines = readAllLines(filePath);
    parseHeaders(lines);

//    if (!"LIST".equals(headers.get("STRUCTURE"))) {
//      throw new IllegalArgumentException("File is not in LIST structure format");
//    }

    // Parse metadata and column names fields
    String[] metadataNames = new String[0];
    String metadataFieldLine = null;
    int itemStartLine = 0;

    if (getHeaders().get("COMMON_FIELDS") != null) {
      metadataNames = getHeaders().get("COMMON_FIELDS").split(",");
      // Find the first non-header, non-empty line (common fields)

      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i).trim();
        if (!line.isEmpty() && !line.startsWith("#")) {
          if (metadataFieldLine == null) {
            metadataFieldLine = line;
            itemStartLine = i + 1;
          }
          else {
            break;
          }
        }
      }
    }

    String[] columnNames = getHeaders().get("FIELDS").split(",");

    Map<String, String> metadataNameToValuesMap = new HashMap<>();

    if (metadataFieldLine != null) {
      // Parse common fields
      String[] metadataValues = metadataFieldLine.split(getSeparator());
      if (metadataValues.length != metadataNames.length) {
        throw new IllegalArgumentException("Common fields count mismatch");
      }

      for (int i = 0; i < metadataNames.length; i++) {
        metadataNameToValuesMap.put(metadataNames[i].trim(), metadataValues[i].trim());
      }
    }

    // Parse column items
    List<Map<String, String>> columnNameToValuesMapList = new LinkedList<>();

    for (int i = itemStartLine; i < lines.size(); i++) {
      String line = lines.get(i).trim();
      if (line.isEmpty() || line.startsWith("#")) {
        continue;
      }

      String[] columnValues = line.split(getSeparator());
      if (columnValues.length != columnNames.length) {
        log.warn("Skipping line with incorrect field count: {}", line);
        continue;
      }

      Map<String, String> columnNameToValuesMap = new HashMap<>();
      for (int j = 0; j < columnNames.length; j++) {
        columnNameToValuesMap.put(columnNames[j].trim(), columnValues[j].trim());
      }

      columnNameToValuesMapList.add(columnNameToValuesMap);
    }

    return new TableContent(
        containsHeader("COMMON_FIELDS") ? getHeaderElementsFor("COMMON_FIELDS") : List.of(),
        getHeaderElementsFor("FIELDS"),
        metadataNameToValuesMap,
        columnNameToValuesMapList
    );
  }
}