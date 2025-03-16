package com.ggoncalves.easycertcreator.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class CertificationInfo {

  private String programName;
  private Integer durationHours;
  private Date programDate;
  private List<String> students;

}
