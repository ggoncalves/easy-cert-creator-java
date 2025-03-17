package com.ggoncalves.easycertcreator.core;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
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
          .getResourceAsStream("jasper/target/Blank_A4_Landscape.jasper")) {
        if (jasperStream == null) {
          throw new RuntimeException("Arquivo jasper não encontrado!");
        }

        // Lista de alunos para gerar certificados
        String[] alunos = {
            "Anderson Carlos Gomes Do Valle",
//            "Callyra Maria Da Silva Nunes",
//            "Camila De Carvalho Niedoszetko",
//            "Camila Nunes De Freitas",
//            "Carolina Goncalves Stelzner",
//            "Caroline Costa E Silva",
//            "Cassio Garcia Cadamuro",
//            "Cidania Menegat Coelho",
//            "Claudineia Moises",
//            "Daiana Vermudt",
//            "Daiane Camargo Moreira",
//            "Daniel Haas De Souza",
//            "Daniel Van Der Broocke Campos De Figueiredo",
//            "Denise Noimann De Oliveira",
//            "Diego Da Fonseca Nicolato",
//            "Eduarda Bianca Duda Correa",
//            "Elisangela Avila Batista",
//            "Elton Alexandre Bueno Da Silva",
//            "Eveline Aquino Ribeiro Cavalari",
//            "Fernanda Regina De Siqueira Santos",
//            "Geovane Da Silva Ribeiro",
//            "Giovanna Bruna Pegorin",
//            "Giovanna Carmo Nalin",
//            "Gisele Aparecida Sieben",
//            "Guilherme Caldoncelli Rodrigues",
//            "Hiago Amarildo Camargo",
//            "Ivana De Sousa Lameke",
//            "Jayana Flores Costa",
//            "Jessica Romero Carvalho",
//            "Jhonatam Luiz Monteiro Ternes",
//            "Joao Pedro Do Nascimento Novitzki",
//            "Jonas Nascimento Da Silva",
//            "Leticia Gregorio De Oliveira",
//            "Lucas Mateus Dos Santos",
//            "Marilia Camargo De Souza",
//            "Mayara Volpi Schmoekel",
//            "Michele Santos Da Silva",
//            "Millena Assis De Almeida",
//            "Natan Vinicius Bini",
//            "Nathalia Batista Beijo",
//            "Sean Lucas Basso Da Silveira",
//            "Vanessa Da Silva Oliveira"
            };

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
        for (String aluno : alunos) {
          // Parâmetros para o relatório
          HashMap<String, Object> parameters = new HashMap<>();
          parameters.put("studentName", aluno);
          parameters.put("programName", "Potencialize com a Metodologia CliftonStrengths");
          parameters.put("durationHours", "8 horas");
          parameters.put("programDate", "20 de Março de 2025");

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

