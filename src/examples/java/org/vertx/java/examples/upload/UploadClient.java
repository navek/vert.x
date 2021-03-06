/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vertx.java.examples.upload;

import org.vertx.java.core.CompletionHandler;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.SimpleHandler;
import org.vertx.java.core.app.VertxApp;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.file.FileSystem;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.streams.Pump;

import java.nio.file.Files;
import java.nio.file.Paths;

public class UploadClient implements VertxApp {

  private HttpClient client;

  public void start() throws Exception {

    client = new HttpClient().setPort(8080).setHost("localhost");

    final HttpClientRequest req = client.put("/some-url", new Handler<HttpClientResponse>() {
      public void handle(HttpClientResponse response) {
        System.out.println("File uploaded " + response.statusCode);
      }
    });

    String filename = "upload/upload.txt";

    // For a non-chunked upload you need to specify size of upload up-front
    req.putHeader("Content-Length", Files.size(Paths.get(filename)));

    // For a chunked upload you don't need to specify size, just do:
    // req.setChunked(true);

    FileSystem.instance.open(filename).handler(new CompletionHandler<AsyncFile>() {
      public void handle(Future<AsyncFile> completion) {
        final AsyncFile file = completion.result();
        Pump pump = new Pump(file.getReadStream(), req);
        pump.start();

        file.getReadStream().endHandler(new SimpleHandler() {
          public void handle() {

            file.close().handler(new CompletionHandler<Void>() {
              public void handle(Future<Void> completion) {
                if (completion.succeeded()) {
                  req.end();
                  System.out.println("Sent request");
                } else {
                  completion.exception().printStackTrace(System.err);
                }
              }
            });
          }
        });
      }

      public void onException(Exception e) {
        e.printStackTrace();
      }
    });

  }

  public void stop() {
    client.close();
  }
}
