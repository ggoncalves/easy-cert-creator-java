package com.ggoncalves.easycertcreator.core.parser;

import lombok.Getter;

@Getter
public enum TableContentField {

  COMMON_FIELDS("COMMON_FIELDS"),
  FIELDS("FIELDS")
  ;

  private final String value;

  TableContentField(String value) {
    this.value = value;
  }
}
