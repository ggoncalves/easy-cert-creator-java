package com.ggoncalves.easycertcreator.main;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EasyCertCreatorMain {

  private CommandListArgsProcessor validator;

  public EasyCertCreatorMain(CommandListArgsProcessor validator) {
    this.validator = validator;
  }

  void execute(String[] args) {
    validator.process(args);
  }

  public static void main(String[] args) {
    EasyCertCreatorMain main = new EasyCertCreatorMain(CommandListArgsProcessor.builder().build());
    main.execute(args);
  }
}
