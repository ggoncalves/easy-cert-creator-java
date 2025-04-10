package com.ggoncalves.easycertcreator.core.logic;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TableContent implements Content {

  // Names defined on headers
  private final List<String> metadataNames;
  private final List<String> columnNames;

  // Metadata - The common variables to all rows
  // Eg: Course name; Credits; Start Date
  private final Map<String, String> metadataNameToValueMap;

  // Rows - All information
  // Eg: Student name; email; age
  private final List<Map<String, String>> listOfColumnNamesToValuesMap;
}
