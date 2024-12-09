package com.ggoncalves.easycertcreator;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EasyCertCreatorMain {

  private ArgumentsValidator validator;

  public EasyCertCreatorMain(ArgumentsValidator validator) {
    this.validator = validator;
  }

  void execute(String[] args) {
    validator.isValidArguments(args);
  }

  public static void main(String[] args) {
    EasyCertCreatorMain main = new EasyCertCreatorMain(ArgumentsValidator.builder().build());
    main.execute(args);
  }
}
