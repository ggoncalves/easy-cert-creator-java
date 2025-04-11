package com.ggoncalves.easycertcreator.core;

import com.ggoncalves.easycertcreator.core.logic.CertificateFileLocations;
import com.ggoncalves.easycertcreator.core.logic.TableContent;
import com.ggoncalves.easycertcreator.core.parser.TableContentFileParser;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateCreatorTest {

  @Mock
  private TableContentFileParser tableContentFileParser;

  @Spy
  private CertificateCreator certificateCreator;

  @Mock
  private InputStream jasperStream;

  @Mock
  private JasperReport jasperReport;

  @Mock
  private JasperPrint jasperPrint;

  private CertificateFileLocations certificateFileLocations;

  private final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
  private final ArgumentCaptor<HashMap<String, Object>> hashMapCaptor = ArgumentCaptor.forClass(HashMap.class);

  @SneakyThrows
  @BeforeEach
  void beforeEach() {
    certificateFileLocations = createCertificateFileLocations();
    certificateCreator = spy(new CertificateCreator(tableContentFileParser));

    doReturn(jasperPrint).when(certificateCreator).createJasperPrint(eq(jasperReport), any());
    doNothing().when(certificateCreator).exportToPdf(eq(jasperPrint), anyString());
  }

  @SneakyThrows
  @DisplayName("Should create successfully")
  @Test
  void shouldCreateSuccessfully() {
    // Given
    doReturn(createTableContent()).when(tableContentFileParser)
        .parse(certificateFileLocations.certificateInfoFilePath());
    prepareMocks();
    // When
    certificateCreator.create(certificateFileLocations);

    // Then
    verify(tableContentFileParser).parse(certificateFileLocations.certificateInfoFilePath());
    verify(certificateCreator, times(3)).createJasperPrint(eq(jasperReport), hashMapCaptor.capture());
    verify(certificateCreator, times(3)).exportToPdf(eq(jasperPrint), stringCaptor.capture());
    verifyParametersMap(hashMapCaptor.getAllValues());

    assertThat(stringCaptor.getAllValues()).contains(
        "outputDir/fileresult1.pdf",
        "outputDir/fileresult2.pdf",
        "outputDir/fileresult3.pdf"
    );
  }

  @SneakyThrows
  @DisplayName("Should create successfully without metadata names")
  @Test
  void shouldCreateSuccessfullyWithoutMetadataNames() {
    // Given
    TableContent tableContent = createTableContentWithoutMetadataNames();
    prepareMocks();
    doReturn(tableContent).when(tableContentFileParser)
        .parse(certificateFileLocations.certificateInfoFilePath());

    // When
    certificateCreator.create(certificateFileLocations);

    // Then
    verify(tableContentFileParser).parse(certificateFileLocations.certificateInfoFilePath());
    verify(certificateCreator, times(3)).createJasperPrint(any(), hashMapCaptor.capture());
    verify(certificateCreator, times(3)).exportToPdf(eq(jasperPrint), stringCaptor.capture());

    verifyParametersMapWithoutMetadataNames(hashMapCaptor.getAllValues());

    assertThat(stringCaptor.getAllValues()).contains(
        "outputDir/fileresult1.pdf",
        "outputDir/fileresult2.pdf",
        "outputDir/fileresult3.pdf"
    );
  }

  private CertificateFileLocations createCertificateFileLocations() {
    return new CertificateFileLocations("jasperFilePath",
        "certificateInfoFilePath", "outputDir");
  }

  private TableContent createTableContent() {
    List<String> metadataNames = List.of("programName", "durationHours", "programDate");
    List<String> columnNames = List.of("studentName");
    Map<String, String> metadataNameToValueMap = Map.of(
        "programName", "Potencialize com a Metodologia CliftonStrengths",
        "durationHours", "8 horas",
        "programDate", "20 de Março de 2025");
    List<Map<String, String>> listOfColumnNamesToValuesMap = List.of(
        Map.of("studentName", "Bill Gates"),
        Map.of("studentName", "Linus Torvalds"),
        Map.of("studentName", "Kurt Cobain")
    );
    return new TableContent(metadataNames, columnNames, metadataNameToValueMap, listOfColumnNamesToValuesMap);
  }

  private TableContent createTableContentWithoutMetadataNames() {
    List<String> columnNames = List.of("firstName,lastName,talent1,talent2,talent3,talent4,talent5");
    List<Map<String, String>> listOfColumnNamesToValuesMap = List.of(
        Map.of(
            "firstName", "Bill",
            "lastName", "Gates",
            "talent1", "Analítico",
            "talent2", "Autoafirmação",
            "talent3", "Realização",
            "talent4", "Crença",
            "talent5", "Ideativo"
        ),
        Map.of(
            "firstName", "Andy",
            "lastName", "Jassy",
            "talent1", "Estudioso",
            "talent2", "Autoafirmação",
            "talent3", "Estudioso",
            "talent4", "Responsabilidade",
            "talent5", "Organização"
        ),
        Map.of(
            "firstName", "Kurt",
            "lastName", "Cobain",
            "talent1", "Grunge",
            "talent2", "Nirvana",
            "talent3", "Punk",
            "talent4", "Himself",
            "talent5", "Not one else"
        )
    );
    return new TableContent(List.of(), columnNames, Map.of(), listOfColumnNamesToValuesMap);
  }

  private void verifyParametersMap(List<HashMap<String, Object>> actualParametersMap) {
    assertThat(actualParametersMap).hasSize(3);

    Map<String, String> expectedMap0 = Map.of(
        "programName", "Potencialize com a Metodologia CliftonStrengths",
        "durationHours", "8 horas",
        "programDate", "20 de Março de 2025",
        "studentName", "Bill Gates"
    );

    Map<String, String> expectedMap1 = Map.of(
        "programName", "Potencialize com a Metodologia CliftonStrengths",
        "durationHours", "8 horas",
        "programDate", "20 de Março de 2025",
        "studentName", "Linus Torvalds"
    );

    Map<String, String> expectedMap2 = Map.of(
        "programName", "Potencialize com a Metodologia CliftonStrengths",
        "durationHours", "8 horas",
        "programDate", "20 de Março de 2025",
        "studentName", "Kurt Cobain"
    );

    // Verify all maps at once
    assertThat(actualParametersMap.get(0)).containsAllEntriesOf(expectedMap0);
    assertThat(actualParametersMap.get(1)).containsAllEntriesOf(expectedMap1);
    assertThat(actualParametersMap.get(2)).containsAllEntriesOf(expectedMap2);
  }

  @SneakyThrows
  private void prepareMocks() {
    doReturn(jasperStream)
        .when(certificateCreator)
        .createJasperInputStream(certificateFileLocations.jasperTemplateFilePath());
    doReturn(jasperReport).when(certificateCreator).loadJasperReportByStream(jasperStream);
  }

  private void verifyParametersMapWithoutMetadataNames(List<HashMap<String, Object>> actualParametersMap) {
    assertThat(actualParametersMap).hasSize(3);

    Map<String, String> expectedMap0 = Map.of(
        "firstName", "Bill",
        "lastName", "Gates",
        "talent1", "Analítico",
        "talent2", "Autoafirmação",
        "talent3", "Realização",
        "talent4", "Crença",
        "talent5", "Ideativo"
    );

    Map<String, String> expectedMap1 = Map.of(
        "firstName", "Andy",
        "lastName", "Jassy",
        "talent1", "Estudioso",
        "talent2", "Autoafirmação",
        "talent3", "Estudioso",
        "talent4", "Responsabilidade",
        "talent5", "Organização"
    );

    Map<String, String> expectedMap2 = Map.of(
        "firstName", "Kurt",
        "lastName", "Cobain",
        "talent1", "Grunge",
        "talent2", "Nirvana",
        "talent3", "Punk",
        "talent4", "Himself",
        "talent5", "Not one else"
    );

    // Verify all maps at once
    assertThat(actualParametersMap.get(0)).containsAllEntriesOf(expectedMap0);
    assertThat(actualParametersMap.get(1)).containsAllEntriesOf(expectedMap1);
    assertThat(actualParametersMap.get(2)).containsAllEntriesOf(expectedMap2);
  }
}