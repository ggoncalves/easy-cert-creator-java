package com.ggoncalves.easycertcreator.core;


import com.ggoncalves.easycertcreator.core.exception.InvalidInfoFileException;
import com.ggoncalves.easycertcreator.model.CertificationInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CertificationInfoFileReaderTest {

  @Spy
  CertificationInfoFileReader spyReader = CertificationInfoFileReader.builder().build();

  @Test
  void testPrerequisiteValidCertInfoFileExists() {
    File file = getTestValidCertInfoFile();
    assertThat(file).exists();
    assertThat(file).canRead();
  }

  @Test
  void readValidCertInfoFile() throws InvalidInfoFileException {
    File certInfoFile = getTestValidCertInfoFile();

    EasyCertFileReader<CertificationInfo, InvalidInfoFileException> certificationInfoFileReader = CertificationInfoFileReader.builder().file(certInfoFile).build();

    // then
    CertificationInfo certificationInfo = certificationInfoFileReader.read();

    // verify
    assertThat(certificationInfo).isNotNull();
    assertThat(certificationInfo.getProgramName()).isEqualTo("Orinnova Lego Serious Play");
    assertThat(certificationInfo.getDurationHours()).isEqualTo(10);
    assertThat(certificationInfo.getProgramDate()).isEqualTo("2024-11-24");
    assertThat(certificationInfo.getStudents()).hasSize(3);
    assertThat(certificationInfo.getStudents()).containsExactlyInAnyOrder("João Silva", "Maria Santos", "Pedro " + "Oliveira");
  }

  @Test
  void readValidCertInfoFileUsingSpy() throws InvalidInfoFileException {
    doReturn(List.of("a", "1", "25/11/2014", "d", "e", "f")).when(spyReader).readLines();

    // then
    CertificationInfo certificationInfo = spyReader.read();

    // verify
    assertThat(certificationInfo).isNotNull();
    assertThat(certificationInfo.getProgramName()).isEqualTo("a");
    assertThat(certificationInfo.getDurationHours()).isEqualTo(1);
    assertThat(certificationInfo.getProgramDate()).isEqualTo("2014-11-25");
    assertThat(certificationInfo.getStudents()).hasSize(3);
    assertThat(certificationInfo.getStudents()).containsExactlyInAnyOrder("d", "e", "f");
  }

  @Test
  void readEmptyCertInfoFile() throws InvalidInfoFileException {
    doReturn(List.of()).when(spyReader).readLines();

    // then
    assertInvalidInfoFileExceptionThrown("O arquivo está vazio");
  }

  private void assertInvalidInfoFileExceptionThrown(String ... expectedMessage) {
    assertThatThrownBy(() -> {
      spyReader.read();
    }).isInstanceOf(InvalidInfoFileException.class).satisfies(e -> {

      InvalidInfoFileException exception = (InvalidInfoFileException) e;
      assertThat(exception.getErrors()).hasSize(expectedMessage.length);
      IntStream.range(0, expectedMessage.length)
          .forEach(i -> assertThat(exception.getErrors().get(i)).isEqualTo(expectedMessage[i]));
    });
  }

  private File getTestValidCertInfoFile() {
    ClassLoader classLoader = getClass().getClassLoader();
    return new File(Objects.requireNonNull(classLoader.getResource("valid_cert_info.txt")).getFile());
  }
}