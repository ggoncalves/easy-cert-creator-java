package com.ggoncalves.easycertcreator.core.parser;

import com.ggoncalves.easycertcreator.core.logic.TableContent;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ggoncalves.easycertcreator.core.parser.FileHeaderField.HEADER_CHAR;
import static com.ggoncalves.easycertcreator.core.parser.TableContentField.COMMON_FIELDS;
import static com.ggoncalves.easycertcreator.core.parser.TableContentField.FIELDS;

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
    return parseContent(lines);
  }

  private record CommonFieldsParserResult(List<String> metadataNamesList, Integer itemStartLine) {
  }

  @NotNull
  private List<Map<String, String>> parseColumnMapList(List<String> lines, int itemStartLine, List<String> columnNamesList) {
    List<Map<String, String>> columnNameToValuesMapList = new LinkedList<>();

    for (int i = itemStartLine; i < lines.size(); i++) {
      String line = lines.get(i).trim();
      if (isHeaderLine(line)) continue;

      String[] columnValues = line.split(getSeparator());
      if (columnValues.length != columnNamesList.size()) {
        log.warn("Skipping line with incorrect field count: {}", line);
        continue;
      }

      Map<String, String> columnNameToValuesMap = new HashMap<>();

      for (int j = 0; j < columnNamesList.size(); j++) {
        columnNameToValuesMap.put(columnNamesList.get(j).trim(), columnValues[j].trim());
      }
      columnNameToValuesMapList.add(columnNameToValuesMap);
    }
    return columnNameToValuesMapList;
  }

  @NotNull
  private CommonFieldsParserResult parseCommonFields(List<String> lines, List<String> metadataNamesList, Map<String, String> metadataNameToValuesMap) {
    Integer itemStartLine = 0;
    if (containsHeader(COMMON_FIELDS.getValue())) {
      String metadataFieldLine = null;
      // Metadata fields names
      metadataNamesList = getHeaderElementsFor(COMMON_FIELDS.getValue())
        .orElse(Collections.emptyList());
      Optional<Pair<Integer, String>> nextLine = readNextNonHeaderLine(lines);
      if (nextLine.isPresent()) {
        itemStartLine = nextLine.get().getFirst();
        metadataFieldLine = nextLine.get().getSecond();
      }

      // Metadata fields values
      if (metadataFieldLine != null) {
        // Parse common fields
        String[] metadataValues = metadataFieldLine.split(getSeparator());
        if (metadataValues.length != metadataNamesList.size()) {
          throw new IllegalArgumentException("Common fields count mismatch");
        }

        for (int i = 0; i < metadataNamesList.size(); i++) {
          metadataNameToValuesMap.put(metadataNamesList.get(i).trim(), metadataValues[i].trim());
        }
      }
    }
    return new CommonFieldsParserResult(metadataNamesList, itemStartLine);
  }

  @NotNull
  private TableContent parseContent(List<String> lines) {
    List<String> metadataNamesList = List.of();
    Map<String, String> metadataNameToValuesMap = new HashMap<>();

    // Parse metadata names fields
    CommonFieldsParserResult commonFieldsParserResult = parseCommonFields(lines, metadataNamesList, metadataNameToValuesMap);

    // Parse column items
    List<String> columnNamesList = getHeaderElementsFor(FIELDS.getValue())
        .orElse(Collections.emptyList());

    List<Map<String, String>> columnNameToValuesMapList = parseColumnMapList(lines, commonFieldsParserResult.itemStartLine(), columnNamesList);

    return new TableContent(
        commonFieldsParserResult.metadataNamesList(),
        columnNamesList,
        metadataNameToValuesMap,
        columnNameToValuesMapList
    );
  }

  private Optional<Pair<Integer, String>> readNextNonHeaderLine(List<String> lines) {
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i).trim();
      if (!line.isEmpty() && !line.startsWith(HEADER_CHAR.getValue())) {
        return Optional.of(new Pair<>(i, line));
      }
    }
    return Optional.empty();
  }
}