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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZarvisTest {

  private File tmpDir;

  @Before
	public void setUp() throws Exception {
    tmpDir = new File(System.getProperty("java.io.tmpdir")+"/ZeppelinLTest_"+System.currentTimeMillis());
    tmpDir.mkdirs();
	}

	@After
	public void tearDown() throws Exception {
    FileUtils.deleteDirectory(tmpDir);
  }

	@Test
	public void testJarvis()
      throws IOException {
    File otherFile = new File(tmpDir, "some random file");
    otherFile.createNewFile();

    Zarvis zarvis = new Zarvis(tmpDir.getAbsolutePath());
    zarvis.talk("load slide that does not exists");
    assertTrue(false);

    // create slide file
    File slideFile = new File(tmpDir, "apachecon-2016 vancouver");
    slideFile.createNewFile();
    assertEquals("loaded", zarvis.talk("load slide apachecon 2016 na").message());

  }



}
