package com.ggoncalves.easycertcreator.core.parser;

import com.ggoncalves.easycertcreator.core.logic.TableContent;
import com.ggoncalves.easycertcreator.util.TestUtilFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Map;

import static com.ggoncalves.easycertcreator.util.TestUtilFactory.createListFileContent;
import static com.ggoncalves.easycertcreator.util.TestUtilFactory.createSimpleFileContent;
import static com.ggoncalves.easycertcreator.util.TestUtilFactory.getResultFileContent;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TableContentFileParserTest {

  @TempDir
  Path tempDir;

  @Spy
  private TableContentFileParser tableContentFileParser = new TableContentFileParser();

  @SneakyThrows
  @DisplayName("Should parse content from file with list and common fields")
  @Test
  void shouldParseContentFromFileWithListAndCommonFields() {
    // Given
    TestUtilFactory.ResultFileContent resultFileContent = getResultFileContent(tempDir, "file.txt",
        createListFileContent());

    // When
    TableContent tableContent = tableContentFileParser.parse(resultFileContent.getFile().toString());

    // Then
    assertThat(tableContent).isNotNull();
    assertThat(tableContent.getMetadataNames()).containsExactly("eventName", "date", "workload", "location");
    assertThat(tableContent.getColumnNames()).containsExactly("studentName", "email", "studentId");
    assertThat(tableContent.getMetadataNameToValueMap()).containsAllEntriesOf(Map.of(
        "eventName", "Java Workshop",
        "date", "05/15/2023",
        "workload", "40 hours",
        "location", "San Francisco"
    ));
    assertThat(tableContent.getListOfColumnNamesToValuesMap()).hasSize(2);
    assertThat(tableContent.getListOfColumnNamesToValuesMap().get(0)).containsAllEntriesOf(Map.of(
        "studentName", "John Smith",
        "email", "john@email.com",
        "studentId", "12345"
    ));
    assertThat(tableContent.getListOfColumnNamesToValuesMap().get(1)).containsAllEntriesOf(Map.of(
        "studentName", "Jane Doe",
        "email", "jane@email.com",
        "studentId", "67890"
    ));
  }

  @SneakyThrows
  @DisplayName("Should parse content from file with list and no common fields")
  @Test
  void shouldParseContentFromFileWithListAndNoCommonFields() {
    // Given
    TestUtilFactory.ResultFileContent resultFileContent = getResultFileContent(tempDir, "file.txt",
        createSimpleFileContent());

    // When
    TableContent tableContent = tableContentFileParser.parse(resultFileContent.getFile().toString());

    // Then
    assertThat(tableContent).isNotNull();
    assertThat(tableContent.getMetadataNames()).isNotNull().isEmpty();
    assertThat(tableContent.getColumnNames()).containsExactly("name", "role", "company", "event", "date");
    assertThat(tableContent.getListOfColumnNamesToValuesMap()).hasSize(1);
    assertThat(tableContent.getListOfColumnNamesToValuesMap().get(0)).containsAllEntriesOf(Map.of(
        "name", "John Smith",
        "role", "Developer",
        "company", "TechCorp",
        "event", "Java Workshop",
        "date", "05/15/2023"
                                                                                                 ));
  }

}