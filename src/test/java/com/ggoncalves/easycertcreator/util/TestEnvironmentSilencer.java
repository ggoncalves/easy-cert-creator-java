package com.ggoncalves.easycertcreator.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public interface TestEnvironmentSilencer {

  ThreadLocal<ByteArrayOutputStream> OUT_CONTENT = ThreadLocal.withInitial(ByteArrayOutputStream::new);
  ThreadLocal<ByteArrayOutputStream> ERR_CONTENT = ThreadLocal.withInitial(ByteArrayOutputStream::new);
  ThreadLocal<PrintStream> ORIGINAL_OUT = ThreadLocal.withInitial(() -> System.out);
  ThreadLocal<PrintStream> ORIGINAL_ERR = ThreadLocal.withInitial(() -> System.err);

  @BeforeEach
  default void setUpStreams() {
    ORIGINAL_OUT.set(System.out);
    ORIGINAL_ERR.set(System.err);
    System.setOut(new PrintStream(OUT_CONTENT.get()));
    System.setErr(new PrintStream(ERR_CONTENT.get()));
  }

  @AfterEach
  default void restoreStreams() {
    System.setOut(ORIGINAL_OUT.get());
    System.setErr(ORIGINAL_ERR.get());
    OUT_CONTENT.get().reset();
    ERR_CONTENT.get().reset();
  }
}