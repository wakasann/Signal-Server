/*
 * Copyright (C) 2013 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.regions.Regions; //new
import com.amazonaws.regions.Region; //new

import java.net.URL;
import java.util.Date;

public class UrlSigner {

  private static final long   DURATION = 60 * 60 * 1000;

  private final AWSCredentials credentials;
  private final String bucket;

  public UrlSigner(String accessKey, String accessSecret, String bucket) {
    this.credentials = new BasicAWSCredentials(accessKey, accessSecret);
    this.bucket      = bucket;
  }

  public URL getPreSignedUrl(long attachmentId, HttpMethod method, boolean unaccelerated) {
    AmazonS3                    client  = new AmazonS3Client(credentials);
    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, String.valueOf(attachmentId), method);
    
    //Region usEast1 = Region.getRegion(Regions.US_EAST_1);
    //client.setRegion(usEast1);
    // change the main
    client.setEndpoint("http://192.168.1.112:9000");
    
    request.setExpiration(new Date(System.currentTimeMillis() + DURATION));
    request.setContentType("application/octet-stream");

    if (unaccelerated) {
      client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
    } else {
      client.setS3ClientOptions(S3ClientOptions.builder().setAccelerateModeEnabled(true).build());
    }

    return client.generatePresignedUrl(request);
  }

}
