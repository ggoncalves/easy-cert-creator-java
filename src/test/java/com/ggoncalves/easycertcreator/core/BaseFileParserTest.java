package com.ggoncalves.easycertcreator.core;

import com.ggoncalves.easycertcreator.util.TestUtilFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.ggoncalves.easycertcreator.util.TestUtilFactory.createListFileContent;
import static com.ggoncalves.easycertcreator.util.TestUtilFactory.createListFileContentNoHeaders;
import static com.ggoncalves.easycertcreator.util.TestUtilFactory.createListFileContentStartingBlank;
import static com.ggoncalves.easycertcreator.util.TestUtilFactory.getResultFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class BaseFileParserTest {

  @TempDir
  Path tempDir;

  @Spy
  private TestBaseFileParser testBaseFileParser = new TestBaseFileParser();

  @DisplayName("Should read all lines from a Non Empty file")
  @SneakyThrows
  @Test
  void shouldReadAllLinesFromANonEmptyFile() {
    // Given
    TestUtilFactory.ResultFileContent resultFileContent = getResultFileContent(tempDir, "file.txt", "line1", "line2",
        "#line3");

    // When
    List<String> lines = testBaseFileParser.readAllLines(resultFileContent.getFile().toString());

    // Then
    assertThat(lines)
        .isNotEmpty()
        .hasSize(3)
        .containsExactly("line1", "line2", "#line3");
  }

  @Test
  @DisplayName("Should parse Headers")
  void shouldParseHeaders() {
    // Given
    List<String> fileContent = createListFileContent();

    // When
    assertThat(testBaseFileParser.headers).isEmpty();
    testBaseFileParser.parseHeaders(fileContent);

    // Then
    assertThat(testBaseFileParser.headers)
        .isNotEmpty()
        .hasSize(3);

    assertThat(testBaseFileParser.headers)
        .containsAllEntriesOf(Map.of(
            "COMMON_FIELDS", "eventName,date,workload,location",
            "FIELDS", "studentName,email,studentId",
            "SEPARATOR", ";"
                                    ));
  }

  @Test
  @DisplayName("Should parse Headers Starting Blank")
  void shouldParseHeadersStartingBlank() {
    // Given
    List<String> fileContent = createListFileContentStartingBlank();

    // When
    assertThat(testBaseFileParser.headers).isEmpty();
    testBaseFileParser.parseHeaders(fileContent);

    // Then
    assertThat(testBaseFileParser.headers)
        .isNotEmpty()
        .hasSize(3);

    assertThat(testBaseFileParser.headers)
        .containsAllEntriesOf(Map.of(
            "COMMON_FIELDS", "eventName,date,workload,location",
            "FIELDS", "studentName,email,studentId",
            "SEPARATOR", ";"
                                    ));
  }

  @Test
  @DisplayName("Should parse Empty headers")
  void shouldParseEmptyHeaders() {
    // Given
    List<String> fileContent = createListFileContentNoHeaders();

    // When
    assertThat(testBaseFileParser.headers).isEmpty();
    testBaseFileParser.parseHeaders(fileContent);
    assertThat(testBaseFileParser.headers).isEmpty();
  }

  @DisplayName("Should consider true for all valid headers")
  @ParameterizedTest(name = "Header \"{0}\" should be valid")
  @ValueSource(strings = {
      "#VALID:something",
      "#valid:SOMETHING",
      "#vALID:Something",
      "#V:s",
      "#header:valor",
      "#a:b"
  })
  void shouldConsiderTrueForAllValidHeaders(String headerLine) {
    assertThat(testBaseFileParser.isHeaderLine(headerLine)).isTrue();
  }

  @DisplayName("Should consider false for invalid headers")
  @ParameterizedTest(name = "Text \"{0}\" should not be a header")
  @ValueSource(strings = {
      "#",
      " #valid:SOMETHING",
      "#:Something",
      "#s",
      "anything",
      "a:b",
      "    ",
      ""
  })
  void shouldConsiderFalseForInvalidHeaders(String headerLine) {
    assertThat(testBaseFileParser.isHeaderLine(headerLine)).isFalse();
  }

  @DisplayName("Should get Header items for composed Header")
  @Test
  void shouldGetHeaderItemsForComposedHeader() {
    // Given
    testBaseFileParser.parseHeaders(createListFileContent());

    // When
    List<String> headerItems = testBaseFileParser.getHeaderElementsFor("FIELDS");

    // Then
    assertThat(headerItems)
        .isNotNull()
        .hasSize(3)
        .containsExactly("studentName", "email", "studentId");
  }

  @DisplayName("Should get Header items for simple Header")
  @Test
  void shouldGetHeaderItemsForSimpleHeader() {
    // Given
    testBaseFileParser.parseHeaders(createListFileContent());

    // When
    List<String> headerItems = testBaseFileParser.getHeaderElementsFor("SEPARATOR");

    // Then
    assertThat(headerItems)
        .isNotNull()
        .hasSize(1)
        .containsExactly(";");
  }

  @DisplayName("Should throw NPE for header not found")
  @Test
  void shouldThrowNPEForHeaderNotFound() {
    // Given
    testBaseFileParser.parseHeaders(createListFileContent());

    // When
    assertThatThrownBy(() -> testBaseFileParser.getHeaderElementsFor("invalid_header"))
        .isInstanceOf(NullPointerException.class);
  }

  @DisplayName("Should return false when does not contains header item")
  @Test
  void shouldReturnFalseWhenDoesNotContainsHeaderItem() {
    // Given
    testBaseFileParser.parseHeaders(createListFileContent());

    // When
    assertThat(testBaseFileParser.containsHeader("invalid_header")).isFalse();
  }

  @DisplayName("Should return true when contains header item")
  @Test
  void shouldReturnTrueWhenContainsHeaderItem() {
    // Given
    testBaseFileParser.parseHeaders(createListFileContent());

    // When
    assertThat(testBaseFileParser.containsHeader("SEPARATOR")).isTrue();
  }

  record TestContent(Map<String, Object> stringToObjectMap) implements Content {
  }

  static class TestBaseFileParser extends BaseFileParser<TestContent> {

    @Override
    public TestContent parse(String filePath) {
      return new TestContent(Map.of());
    }
  }
}