package com.ggoncalves.easycertcreator.core;

import com.ggoncalves.easycertcreator.core.logic.CertificateFileLocations;
import com.ggoncalves.easycertcreator.core.logic.TableContent;
import com.ggoncalves.easycertcreator.core.parser.TableContentFileParser;
import com.google.common.annotations.VisibleForTesting;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(force = true)
@Data
public class CertificateCreator {

  private final TableContentFileParser tableContentFileParser;

  @Inject
  public CertificateCreator(TableContentFileParser tableContentFileParser) {
    this.tableContentFileParser = tableContentFileParser;
  }

  public void create(@NotNull CertificateFileLocations certificateFileLocations) {

    try (InputStream jasperStream = createJasperInputStream(certificateFileLocations.jasperTemplateFilePath())) {

      JasperReport jasperReport = loadJasperReportByStream(jasperStream);
      TableContent tableContent = readTableContentFromFile(certificateFileLocations.certificateInfoFilePath());
      fillAndExportReports(jasperReport, tableContent, certificateFileLocations.outputDir());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void fillAndExportReports(JasperReport jasperReport, TableContent tableContent, String outputDir) {
    AtomicInteger index = new AtomicInteger(1);

    tableContent.getListOfColumnNamesToValuesMap().forEach(
        columnNamesToValuesMap -> {
          HashMap<String, Object> reportParametersMap = new HashMap<>();
          reportParametersMap.putAll(tableContent.getMetadataNameToValueMap());
          reportParametersMap.putAll(columnNamesToValuesMap);
          try {
            JasperPrint jasperPrint = createJasperPrint(jasperReport, reportParametersMap);
            exportToPdf(jasperPrint, createNumberedFileName(outputDir, index.getAndIncrement()));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  private TableContent readTableContentFromFile(@NotNull String filePath) throws IOException {
    return tableContentFileParser.parse(filePath);
  }

  private String createNumberedFileName(String outputDir, Integer number) {
    Path directory = Paths.get(outputDir);
    String filename = "fileresult" + number + ".pdf";
    Path filePath = directory.resolve(filename);
    return filePath.toString();

  }

  @VisibleForTesting
  JasperReport loadJasperReportByStream(InputStream jasperStream) throws JRException {
     return (JasperReport)JRLoader.loadObject(jasperStream);
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
