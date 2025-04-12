package com.ggoncalves.easycertcreator.core;

import com.ggoncalves.easycertcreator.core.logic.CertificateFileConfiguration;
import com.ggoncalves.easycertcreator.core.logic.TableContent;
import com.ggoncalves.easycertcreator.core.parser.TableContentFileParser;
import com.google.common.collect.Maps;
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

  private CertificateFileConfiguration certificateFileConfiguration;

  private final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

  @SuppressWarnings("unchecked")
  private final ArgumentCaptor<HashMap<String, Object>> hashMapCaptor =
      ArgumentCaptor.forClass((Class<HashMap<String, Object>>) (Class<?>) HashMap.class);


  @SneakyThrows
  @BeforeEach
  void beforeEach() {
    certificateFileConfiguration = createCertificateFileConfiguration();
    certificateCreator = spy(new CertificateCreator(tableContentFileParser));

    lenient().doReturn(jasperPrint).when(certificateCreator).createJasperPrint(eq(jasperReport), any());
    lenient().doNothing().when(certificateCreator).exportToPdf(eq(jasperPrint), anyString());
  }

  @SneakyThrows
  @DisplayName("Should create successfully with default filename")
  @Test
  void shouldCreateSuccessfully() {
    // Given
    certificateFileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", null);

    doReturn(createTableContent()).when(tableContentFileParser)
        .parse(certificateFileConfiguration.certificateInfoFilePath());
    prepareMocks();
    // When
    certificateCreator.create(certificateFileConfiguration);

    // Then
    verify(tableContentFileParser).parse(certificateFileConfiguration.certificateInfoFilePath());
    verify(certificateCreator, times(3)).createJasperPrint(eq(jasperReport), hashMapCaptor.capture());
    verify(certificateCreator, times(3)).exportToPdf(eq(jasperPrint), stringCaptor.capture());
    verifyParametersMap(hashMapCaptor.getAllValues());

    assertThat(stringCaptor.getAllValues()).contains(
        "outputDir/cert_created.pdf",
        "outputDir/cert_created_2.pdf",
        "outputDir/cert_created_3.pdf"
    );
  }

  @SneakyThrows
  @DisplayName("Should create successfully with custom filename")
  @Test
  void shouldCreateSuccessfullyWithCustomFilename() {
    // Given
    certificateFileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "Certificate_$studentName");

    doReturn(createTableContent()).when(tableContentFileParser)
        .parse(certificateFileConfiguration.certificateInfoFilePath());
    prepareMocks();
    // When
    certificateCreator.create(certificateFileConfiguration);

    // Then
    verify(tableContentFileParser).parse(certificateFileConfiguration.certificateInfoFilePath());
    verify(certificateCreator, times(3)).createJasperPrint(eq(jasperReport), hashMapCaptor.capture());
    verify(certificateCreator, times(3)).exportToPdf(eq(jasperPrint), stringCaptor.capture());
    verifyParametersMap(hashMapCaptor.getAllValues());

    assertThat(stringCaptor.getAllValues()).contains(
        "outputDir/Certificate_Bill_Gates.pdf",
        "outputDir/Certificate_Linus_Torvalds.pdf",
        "outputDir/Certificate_Kurt_Cobain.pdf"
    );
  }

  @DisplayName("Should build output file path with default name")
  @Test
  void shouldBuildOutputFilePathWithDefaultName() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", null);

    // When
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createStudentMap(), new HashMap<>());

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/cert_created.pdf");
  }

  @DisplayName("Should build output file path with custom name")
  @Test
  void shouldBuildOutputFilePathWithCustomName() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "custom_name");

    // When
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createStudentMap(), new HashMap<>());

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/custom_name.pdf");
  }

  @DisplayName("Should build output file path with default name with duplicated name")
  @Test
  void shouldBuildOutputFilePathWithDefaultNameWithDuplicatedName() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", null);

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    filenameToOccurrenciesMap.put("cert_created", 1);
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createStudentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/cert_created_2.pdf");
  }

  @DisplayName("Should build output file path with custom name duplicated")
  @Test
  void shouldBuildOutputFilePathWithCustomNameDuplicated() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "custom_name");

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    filenameToOccurrenciesMap.put("custom_name", 3);
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createStudentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/custom_name_4.pdf");
  }

  @DisplayName("Should build output file path with non existent variable")
  @Test
  void shouldBuildOutputFilePathWithNonExistentVariable() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "custom_name$NotExists");

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createStudentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/custom_nameNotExists.pdf");
  }

  @DisplayName("Should build output file path with non existent variable duplicated")
  @Test
  void shouldBuildOutputFilePathWithNonExistentVariableDuplicated() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "custom_name$NotExists");

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    filenameToOccurrenciesMap.put("custom_nameNotExists", 2);
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createStudentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/custom_nameNotExists_3.pdf");
  }

  @DisplayName("Should build output file path with existent variable")
  @Test
  void shouldBuildOutputFileWithExistentVariable() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "$studentName");

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createStudentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/Linus_Torvalds.pdf");
  }

  @DisplayName("Should build output file path with existent variable duplicated")
  @Test
  void shouldBuildOutputFileWithExistentVariableDuplicated() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "$studentName");

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    filenameToOccurrenciesMap.put("Linus_Torvalds", 1);
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createStudentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/Linus_Torvalds_2.pdf");
  }

  @DisplayName("Should build output file path with two existent variables")
  @Test
  void shouldBuildOutputFileWithTwoExistentVariables() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "$firstName$lastName");

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createBadgeTalentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/KurtCobain.pdf");
  }

  @DisplayName("Should build output file path with two existent variables 2")
  @Test
  void shouldBuildOutputFileWithTwoExistentVariables2() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "$firstName_$lastName");

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createBadgeTalentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/Kurt_Cobain.pdf");
  }

  @DisplayName("Should build output file path with single existent variables")
  @Test
  void shouldBuildOutputFileWithSingleExistentVariables() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "file_$firstName_$lostName");

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createBadgeTalentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/file_Kurt_lostName.pdf");
  }

  @DisplayName("Should build output file path with existent variable without $")
  @Test
  void shouldBuildOutputFileWithExistentVariableWithoutDollar() {
    CertificateFileConfiguration fileConfiguration = new CertificateFileConfiguration("jasperFilePath",
        "certificationInfoFile", "outputDir", "studentName");

    // When
    Map<String, Integer> filenameToOccurrenciesMap = Maps.newHashMap();
    String outputFilePath = certificateCreator.buildOutputFilePath(fileConfiguration,
        createStudentMap(), filenameToOccurrenciesMap);

    // Then
    assertThat(outputFilePath).isEqualTo("outputDir/studentName.pdf");
  }

  @SneakyThrows
  @DisplayName("Should create successfully without metadata names")
  @Test
  void shouldCreateSuccessfullyWithoutMetadataNames() {
    // Given
    TableContent tableContent = createTableContentWithoutMetadataNames();
    prepareMocks();
    doReturn(tableContent).when(tableContentFileParser)
        .parse(certificateFileConfiguration.certificateInfoFilePath());

    // When
    certificateCreator.create(certificateFileConfiguration);

    // Then
    verify(tableContentFileParser).parse(certificateFileConfiguration.certificateInfoFilePath());
    verify(certificateCreator, times(3)).createJasperPrint(any(), hashMapCaptor.capture());
    verify(certificateCreator, times(3)).exportToPdf(eq(jasperPrint), stringCaptor.capture());

    verifyParametersMapWithoutMetadataNames(hashMapCaptor.getAllValues());

    assertThat(stringCaptor.getAllValues()).contains(
        "outputDir/certfilename.pdf",
        "outputDir/certfilename_2.pdf",
        "outputDir/certfilename_3.pdf"
    );
  }

  private CertificateFileConfiguration createCertificateFileConfiguration() {
    return new CertificateFileConfiguration("jasperFilePath",
        "certificateInfoFilePath", "outputDir", "certfilename");
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
        .createJasperInputStream(certificateFileConfiguration.jasperTemplateFilePath());
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

  private Map<String, Object> createStudentMap() {
    return Map.of(
        "programName", "Potencialize com a Metodologia CliftonStrengths",
        "durationHours", "8 horas",
        "programDate", "20 de Março de 2025",
        "studentName", "Linus Torvalds"
    );
  }

  private Map<String, Object> createBadgeTalentMap() {
    return Map.of(
        "firstName", "Kurt",
        "lastName", "Cobain",
        "talent1", "Grunge",
        "talent2", "Nirvana",
        "talent3", "Punk",
        "talent4", "Himself",
        "talent5", "Not one else"
    );
  }
}