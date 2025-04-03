package com.ggoncalves.easycertcreator.util;

import com.ggoncalves.ggutils.console.exception.FilePermissionException;
import com.ggoncalves.ggutils.console.exception.InvalidFileException;

import java.util.stream.Stream;

public class TestUtilFactory {

  static Stream<Exception> allFileExceptionsProvider() {
    return Stream.of(
        new InvalidFileException("InvalidFileException"),
        new FilePermissionException("FilePermissionException")
                    );
  }
}
