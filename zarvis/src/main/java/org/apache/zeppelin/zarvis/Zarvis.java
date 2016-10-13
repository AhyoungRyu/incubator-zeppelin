/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.zarvis;

import org.apache.zeppelin.interpreter.InterpreterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Zarvis
 */
public class Zarvis {
  private final Logger logger = LoggerFactory.getLogger(Zarvis.class);
  private final String dataPath;
  private Slide slide;

  public Zarvis(String dataPath) {
    this.dataPath = dataPath;
  }

  public InterpreterResult talk(String message) throws IOException {
    if (message == null) {
      return new InterpreterResult(InterpreterResult.Code.ERROR, "Can you repeat?");
    }

    message = message.trim();
    if (message.length() == 0) {
      return new InterpreterResult(InterpreterResult.Code.ERROR, "Sorry?");
    }

    // split action and rest
    Pattern loadPattern = Pattern.compile("[\\s,]*load\\s+slide\\s+(.*)");
    Matcher loadPatternMatcher = loadPattern.matcher(message);
    if (loadPatternMatcher.matches()) {
      String filenameHint = loadPatternMatcher.group(1);
      File match = getMatchingFile(filenameHint.split("[-\\s_.]"));
      if (match == null) {
        return new InterpreterResult(InterpreterResult.Code.ERROR,
            "Slide " + filenameHint + " can not be found");
      }
      logger.info("Loading slide {}", match.getAbsolutePath());
      slide = new Slide(match);
      return new InterpreterResult(InterpreterResult.Code.SUCCESS,
          "Slide loaded, " + slide.size() + " pages");
    }

    Pattern nextSlidePattern = Pattern.compile("[\\s,]*next\\s*");
    Matcher nextSlidePatternMatcher = nextSlidePattern.matcher(message);
    if (nextSlidePatternMatcher.matches()) {
      return slide.next();
    }

    Pattern previousSlidePattern = Pattern.compile("[\\s,]*prev\\s*");
    Matcher previousSlidePatternMatcher = previousSlidePattern.matcher(message);
    if (previousSlidePatternMatcher.matches()) {
      return slide.previous();
    }

    Pattern reloadSlidePattern = Pattern.compile("[\\s,]*reload\\s*");
    Matcher reloadSlidePatternMatcher = reloadSlidePattern.matcher(message);
    if (reloadSlidePatternMatcher.matches()) {
      slide.refresh();
      return slide.get();
    }

    Pattern startSlidePattern = Pattern.compile("[\\s,]*start(\\s.*)?");
    Matcher startSlidePatternMatcher = startSlidePattern.matcher(message);
    if (startSlidePatternMatcher.matches()) {
      return slide.jumpToPage(0);
    }

    Pattern bringPagePattern = Pattern.compile("[\\s,]*bring\\s(.*)");
    Matcher bringPagePatternMatcher = bringPagePattern.matcher(message);
    if (bringPagePatternMatcher.matches()) {
      String pageHint = bringPagePatternMatcher.group(1);
      try {
        int pageIdx = Integer.parseInt(pageHint);
        return slide.jumpToPage(pageIdx);
      } catch (Exception e) {
        // ignore
      }
      new InterpreterResult(InterpreterResult.Code.ERROR,
          "I don't think the slide have page " + pageHint);
    }


    Pattern howareyouPattern = Pattern.compile("[\\s,]*how.are.you.*");
    Matcher howareyouPatternMatcher = howareyouPattern.matcher(message);
    if (howareyouPatternMatcher.matches()) {
      return new InterpreterResult(InterpreterResult.Code.SUCCESS,
          "I'm pretty good, moon. Thanks for asking");
    }

    return new InterpreterResult(InterpreterResult.Code.SUCCESS, "Well,");
  }


  private File getMatchingFile(String[] filenameHint) {
    Map<File, Integer> map = new HashMap<File, Integer>();

    File dir = new File(dataPath);
    for (File file : dir.listFiles()) {
      if (!file.isFile()) {
        continue;
      }

      String[] fileName = file.getName().split("[-\\s_.]");
      int match = 0;
      for (String hint : filenameHint) {
        for (String name : fileName) {
          if (hint.toLowerCase().equals(name.toLowerCase())) {
            match++;
          }
        }
      }

      map.put(file, match);
    }

    int maxMatch = 0;
    File largestMatch = null;
    for (File f : map.keySet()) {
      int match = map.get(f);
      if (match > maxMatch) {
        maxMatch = match;
        largestMatch = f;
      }
    }

    return largestMatch;
  }



}
