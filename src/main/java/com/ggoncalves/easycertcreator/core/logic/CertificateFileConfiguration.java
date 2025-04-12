package com.ggoncalves.easycertcreator.core.logic;

public record CertificateFileConfiguration(String jasperTemplateFilePath, String certificateInfoFilePath,
                                           String outputDir, String certificateFileName) {
}
