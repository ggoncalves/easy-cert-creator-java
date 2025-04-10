package com.ggoncalves.easycertcreator.core.parser;

import lombok.Getter;

@Getter
public enum FileHeaderField {

  FIELDS_SEPARATOR("SEPARATOR"),
  HEADER_CHAR("#"),
  HEADER_SEPARADOR(":"),
  HEADER_ELEMENT_SEPARATOR(",")
  ;

  private final String value;

  FileHeaderField(String value) {
    this.value = value;
  }

}
