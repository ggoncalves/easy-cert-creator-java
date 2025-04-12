package com.ggoncalves.easycertcreator.core;

import com.ggoncalves.easycertcreator.core.logic.CertificateFileConfiguration;
import com.ggoncalves.easycertcreator.core.logic.TableContent;
import com.ggoncalves.easycertcreator.core.parser.TableContentFileParser;
import com.google.common.annotations.VisibleForTesting;
import lombok.Data;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Data
public class CertificateCreator {

  private static final String DEFAULT_FILENAME = "cert_created";
  private final TableContentFileParser tableContentFileParser;

  @Inject
  public CertificateCreator(TableContentFileParser tableContentFileParser) {
    this.tableContentFileParser = tableContentFileParser;
  }

  public CertificateCreator() {
    this(new TableContentFileParser());
  }

  public void create(@NotNull CertificateFileConfiguration certificateFileConfiguration) {

    try (InputStream jasperStream = createJasperInputStream(certificateFileConfiguration.jasperTemplateFilePath())) {

      JasperReport jasperReport = loadJasperReportByStream(jasperStream);
      TableContent tableContent = readTableContentFromFile(certificateFileConfiguration.certificateInfoFilePath());
      fillAndExportReports(jasperReport, tableContent, certificateFileConfiguration);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void fillAndExportReports(JasperReport jasperReport, TableContent tableContent,
                                    CertificateFileConfiguration certificateFileConfiguration) {

    Map<String, Integer> filenameToOccurrenciesMap = new HashMap<>();

    tableContent.getListOfColumnNamesToValuesMap().forEach(
        columnNamesToValuesMap -> {
          HashMap<String, Object> reportParametersMap = new HashMap<>();
          reportParametersMap.putAll(tableContent.getMetadataNameToValueMap());
          reportParametersMap.putAll(columnNamesToValuesMap);
          try {
            JasperPrint jasperPrint = createJasperPrint(jasperReport, reportParametersMap);
            exportToPdf(jasperPrint, buildOutputFilePath(certificateFileConfiguration, reportParametersMap, filenameToOccurrenciesMap));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  private TableContent readTableContentFromFile(@NotNull String filePath) throws IOException {
    return tableContentFileParser.parse(filePath);
  }

  private boolean isFileNameWithVariable(@NotNull String filename) {
    return filename.contains("$");
  }

  private String replaceVariablesInFilename(String filename, Map<String, Object> reportParametersMap) {
    for (String key : reportParametersMap.keySet()) {
      if (filename.contains("$" + key)) {
        filename = filename.replaceAll("\\$" + key, reportParametersMap.get(key).toString());
      }
    }
    filename = filename.replaceAll("\\$", "");
    filename = filename.replaceAll("\\s", "_");
    return filename;
  }

  private String generateUniqueFilename(Map<String, Integer> filenameToOccurrenciesMap, String filename) {
    // Get current count or insert 0, then increment the value
    Integer occurrence = filenameToOccurrenciesMap.compute(filename, (key, count) -> count == null ? 1 : count + 1);

    // For first occurrence (1), keep the original filename
    // For subsequent occurrences (>1), append _count suffix
    return occurrence == 1 ? filename : filename + "_" + occurrence;
  }

  @VisibleForTesting
  String buildOutputFilePath(CertificateFileConfiguration certificateFileConfiguration,
                             Map<String, Object> reportParametersMap,
                             Map<String, Integer> filenameToOccurrenciesMap) {

    String filename = StringUtils.defaultIfBlank(certificateFileConfiguration.certificateFileName(), DEFAULT_FILENAME);

    if (isFileNameWithVariable(filename)) {
      filename = replaceVariablesInFilename(filename, reportParametersMap);
    }

    filename = generateUniqueFilename(filenameToOccurrenciesMap, filename);

    return generateFilePath(certificateFileConfiguration, filename);
  }

  private String generateFilePath(CertificateFileConfiguration certificateFileConfiguration, String filename) {
    Path directory = Paths.get(certificateFileConfiguration.outputDir());
    Path filePath = directory.resolve(filename + ".pdf");
    return filePath.toString();
  }

  @VisibleForTesting
  JasperReport loadJasperReportByStream(InputStream jasperStream) throws JRException {
    return (JasperReport) JRLoader.loadObject(jasperStream);
  }

  @VisibleForTesting
  InputStream createJasperInputStream(String filePath) throws IOException {
    Path path = Paths.get(filePath);
    return Files.newInputStream(path);
  }

  @VisibleForTesting
  JasperPrint createJasperPrint(JasperReport jasperReport, HashMap<String, Object> reportParametersMap) throws JRException {
    return JasperFillManager.fillReport(
        jasperReport,
        reportParametersMap,
        new JREmptyDataSource());
  }

  @VisibleForTesting
  void exportToPdf(JasperPrint jasperPrint, String outputFile) throws JRException {
    JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
    System.out.println("Certificado gerado: " + outputFile);
  }
}
