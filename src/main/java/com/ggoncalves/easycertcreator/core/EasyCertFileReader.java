package com.ggoncalves.easycertcreator.core;

import com.ggoncalves.easycertcreator.core.exception.PrintableErrorException;

public interface EasyCertFileReader<T, E extends PrintableErrorException> {

  public T read() throws E;
}
