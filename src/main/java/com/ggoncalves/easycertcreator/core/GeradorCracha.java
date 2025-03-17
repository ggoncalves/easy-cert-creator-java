package com.ggoncalves.easycertcreator.core;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.InputStream;
import java.util.HashMap;

public class GeradorCracha {
  public static void main(String[] args) {
    try {
      // Carrega o arquivo .jasper compilado
//      String jasperFile = "tmp/Blank_A4_Landscape.jasper";
//      InputStream jasperStream = GeradorCertificado.class.getClassLoader().getResourceAsStream("jasper/target" +
//          "/cert-orinnova-v01.jasper");

      try (InputStream jasperStream = GeradorCracha.class.getClassLoader()
          .getResourceAsStream("jasper/target/Cracha.jasper")) {
        if (jasperStream == null) {
          throw new RuntimeException("Arquivo jasper não encontrado!");
        }

        // Lista de alunas e talentos para gerar crachás
        String[] alunosTalentos = {
            "Milena Assis-Analítico,Autoafirmação,Foco,Crença,Realização",
            "Elisangela Avila Batista-Imparcialidade,Estudioso,Inclusão,Prudência,Analítico",
            "Sean Basso-Analítico,Competição,Foco,Estratégico,Contexto",
            "Natan Bini-Relacionamento,Ideativo,Intelecção,Futurista,Adaptabilidade",
            "Guilherme Caldoncelli Rodrigues-Organização,Excelência,Futurista,Analítico,Responsabilidade",
            "Marillia Camargo de Souza-Empatia,Responsabilidade,Desenvolvimento,Harmonia,Inclusão",
            "Hiago Camargo-Relacionamento,Autoafirmação,Significância,Responsabilidade,Restauração",
            "Anderson Carlos Do Valle-Inclusão,Positivo,Desenvolvimento,Conexão,Ativação",
            "Jessica Carvalho-Carisma,Conexão,Inclusão,Individualização,Comunicação",
            "Eveline Cavalari-Responsabilidade,Crença,Empatia,Restauração,Harmonia",
            "Cidania Coelho-Crença,Conexão,Empatia,Responsabilidade,Estudioso",
            "Vanessa da Silva Oliveira-Realização,Individualização,Restauração,Relacionamento,Positivo",
            "Michele da Silva-Responsabilidade,Imparcialidade,Relacionamento,Organização,Analítico",
            "Ivania de Sousa Lameke-Positivo,Estudioso,Desenvolvimento,Futurista,Responsabilidade",
            "Wanderson dos Santos Mendes-Responsabilidade,Desenvolvimento,Ideativo,Inclusão,Analítico",
            "Jayana Flores Costa-Desenvolvimento,Imparcialidade,Responsabilidade,Analítico,Positivo",
            "Cassio Garcia Cadamuro-Analítico,Contexto,Responsabilidade,Estudioso,Organização",
            "Leticia Gregorio de Oliveira-Comunicação,Crença,Responsabilidade,Desenvolvimento,Empatia",
            "Daniel Haas de Souza-Contexto,Responsabilidade,Realização,Analítico,Crença",
            "Vitor José Lacerda da Silva-Analítico,Desenvolvimento,Significância,Organização,Competição",
            "Lucas Mateus-Realização,Carisma,Organização,Responsabilidade,Adaptabilidade",
            "Jonas Nascimento da Silva-Relacionamento,Responsabilidade,Estudioso,Input,Significância",
            "Diego Nicolato-Realização,Restauração,Ideativo,Estudioso,Competição",
            "Camila Niedoszetko-Carisma,Positivo,Responsabilidade,Organização,Inclusão",
            "Denise Oliveira-Imparcialidade,Prudência,Inclusão,Responsabilidade,Relacionamento",
            "João Novitzki-Relacionamento,Realização,Conexão,Futurista,Adaptabilidade",
            "Camila Nunes de Freitas-Relacionamento,Individualização,Estratégico,Disciplina,Responsabilidade",
            "Callyra Nunes-Harmonia,Desenvolvimento,Contexto,Inclusão,Positivo",
            "Geovane Ribeiro-Prudência,Harmonia,Imparcialidade,Relacionamento,Empatia",
            "Fernanda Santos-Imparcialidade,Desenvolvimento,Empatia,Positivo,Inclusão",
            "Mayara Schmoekel-Relacionamento,Futurista,Significância,Estratégico,Organização",
            "Gisele Sieben-Imparcialidade,Responsabilidade,Harmonia,Inclusão,Disciplina",
            "Isabella Staron-Responsabilidade,Organização,Realização,Crença,Empatia",
            "Daniel Van der Broocke-Empatia,Carisma,Desenvolvimento,Positivo,Realização",
            "Daiana Vermudt-Restauração,Futurista,Empatia,Positivo,Organização"
            };

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
        for (String alunoTalento : alunosTalentos) {
          // Parâmetros para o relatório
          HashMap<String, Object> parameters = new HashMap<>();
          String name = alunoTalento.split("-")[0];
          parameters.put("firstName", name.split(" ")[0]);
          parameters.put("lastName", name.substring(name.indexOf(" ") + 1));

          String[] talents = alunoTalento.split("-")[1].split(",");
          parameters.put("talent1", talents[0]);
          parameters.put("talent2", talents[1]);
          parameters.put("talent3", talents[2]);
          parameters.put("talent4", talents[3]);
          parameters.put("talent5", talents[4]);

          // Gera o relatório
          JasperPrint jasperPrint = JasperFillManager.fillReport(
              jasperReport,
              parameters,
              new JREmptyDataSource()
                                                                );

          // Exporta para PDF
          String outputFile = "tmp/cracha_" + name.replaceAll("\\s+", "_") + ".pdf";
          JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);

          System.out.println("Crachá gerado para: " + name);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

