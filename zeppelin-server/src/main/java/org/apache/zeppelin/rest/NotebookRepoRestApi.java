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

package org.apache.zeppelin.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.zeppelin.notebook.repo.NotebookRepoSync;
import org.apache.zeppelin.rest.message.NewNotebookRepoLoadRequest;
import org.apache.zeppelin.server.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Rest api endpoint for the Notebook Repo.
 */
@Path("/notebookRepo")
@Produces("application/json")
public class NotebookRepoRestApi {
  private static final Logger logger = LoggerFactory.getLogger(NotebookRepoRestApi.class);
  private NotebookRepoSync notebookRepoSync;
  Gson gson = new Gson();

  public NotebookRepoSync(NotebookRepoSync notebookRepoSync) {
    this.notebookRepoSync = notebookRepoSync;
  }

  /**
   * Add new Load Notebook Repo
   * @param message
   * @return
   * @throws IOException, Exception
   */
  @POST
  @Path("load")
  public Response newLoad(String message)
    throws IOException, Exception {
    NewNotebookRepoLoadRequest request = gson.fromJson(message,
      NewNotebookRepoLoadRequest.class);
    JsonObject jsonRepository = new JsonParser().parse(message).
      getAsJsonObject().getAsJsonObject("repository");

    if (request != null) {
      try {
        if (jsonRepository == null) {
          notebookRepoSync.loadDynamicNoteBookStorage(request.getArtifact(),
            request.getClassName());
          logger.info("Notebook Repository is successfully loaded without Maven Repository");

          return new JsonResponse(Response.Status.CREATED, "200 OK", message).build();
        } else {
          logger.info("Notebook Repository is successfully loaded with Maven Repository");
          notebookRepoSync.loadDynamicNoteBookStorage(request.getArtifact(),
            request.getClassName(),
            request.getRepository().getUrl(), request.getRepository().getSnapshot());

          return new JsonResponse(Response.Status.CREATED, "200 OK", message).build();
        }
      } catch (Exception e) {
        return new JsonResponse(Response.Status.INTERNAL_SERVER_ERROR, "500", message).build();
        logger.info("Notebook Repository Loading Failed");
      }
    }
    return new JsonResponse(Response.Status.NOT_FOUND, "Null Request", message).build();
  }
}
