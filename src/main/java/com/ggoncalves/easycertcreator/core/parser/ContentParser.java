package com.ggoncalves.easycertcreator.core.parser;

import com.ggoncalves.easycertcreator.core.logic.Content;

import java.io.IOException;

public interface ContentParser<T extends Content> {
  
  T parse(String filePath) throws IOException;
  
}