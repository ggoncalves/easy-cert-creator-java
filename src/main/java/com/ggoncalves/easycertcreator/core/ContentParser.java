package com.ggoncalves.easycertcreator.core;

import java.io.IOException;

public interface ContentParser<T extends Content> {
  
  T parse(String filePath) throws IOException;
  
}