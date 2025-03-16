package com.ggoncalves.easycertcreator.core;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.InputStream;
import java.util.HashMap;

public class GeradorCertificado {
  public static void main(String[] args) {
    try {
      // Carrega o arquivo .jasper compilado
//      String jasperFile = "tmp/Blank_A4_Landscape.jasper";
//      InputStream jasperStream = GeradorCertificado.class.getClassLoader().getResourceAsStream("jasper/target" +
//          "/cert-orinnova-v01.jasper");

      try (InputStream jasperStream = GeradorCertificado.class.getClassLoader()
          .getResourceAsStream("jasper/target/cert-orinnova-v01.jasper")) {
        if (jasperStream == null) {
          throw new RuntimeException("Arquivo jasper não encontrado!");
        }

        // Lista de alunos para gerar certificados
        String[] alunos = {"João Silva", "Maria Santos", "Pedro Oliveira"};

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
        for (String aluno : alunos) {
          // Parâmetros para o relatório
          HashMap<String, Object> parameters = new HashMap<>();
          parameters.put("studentName", aluno);
          parameters.put("programName", "Orinnova Lego Serious Play");
          parameters.put("durationHours", "10 horas");
          parameters.put("programDate", "24 de Novembro de 2024");

          // Gera o relatório
          JasperPrint jasperPrint = JasperFillManager.fillReport(
              jasperReport,
              parameters,
              new JREmptyDataSource()
          );

          // Exporta para PDF
          String outputFile = "tmp/certificado_" + aluno.replaceAll("\\s+", "_") + ".pdf";
          JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);

          System.out.println("Certificado gerado para: " + aluno);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

