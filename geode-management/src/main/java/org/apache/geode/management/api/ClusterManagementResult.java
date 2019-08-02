/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.api;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.geode.annotations.Experimental;

/**
 * This base class provides the common attributes returned from all {@link ClusterManagementService}
 * methods
 */
@Experimental
public class ClusterManagementResult {
  /**
   * these status codes generally have a one-to-one mapping to the http status code returned by the
   * REST controller
   */
  public enum StatusCode {
    /** configuration failed validation */
    ILLEGAL_ARGUMENT,
    /** user is not authenticated */
    UNAUTHENTICATED,
    /** user is not authorized to do this operation */
    UNAUTHORIZED,
    /** entity you are trying to create already exists */
    ENTITY_EXISTS,
    /** entity you are trying to modify/delete is not found */
    ENTITY_NOT_FOUND,
    /**
     * operation not successful, this includes precondition is not met (either service is not
     * running or no servers available, or the configuration encountered some error when trying to
     * be realized on one member (configuration is not fully realized on all applicable members).
     */
    ERROR,
    /**
     * configuration is realized on members, but encountered some error when persisting the
     * configuration. the operation is still deemed unsuccessful.
     */
    FAIL_TO_PERSIST,
    /** async operation launched successfully */
    ACCEPTED,
    /** async operation has not yet completed */
    IN_PROGRESS,
    /** operation is successful, configuration is realized and persisted */
    OK
  }

  // we will always have statusCode when the object is created
  protected StatusCode statusCode = StatusCode.OK;
  private String statusMessage;
  private String uri;

  /**
   * for internal use only
   */
  public ClusterManagementResult() {}

  /**
   * for internal use only
   */
  public ClusterManagementResult(StatusCode statusCode, String message) {
    setStatus(statusCode, message);
  }

  /**
   * for internal use only
   */
  public ClusterManagementResult(ClusterManagementResult copyFrom) {
    this.statusCode = copyFrom.statusCode;
    this.statusMessage = copyFrom.statusMessage;
    this.uri = copyFrom.uri;
  }

  /**
   * for internal use only
   */
  public void setStatus(StatusCode statusCode, String message) {
    this.statusCode = statusCode;
    this.statusMessage = formatErrorMessage(message);
  }

  /**
   * Capitalized the first letter and adds a period, if needed.
   * This helps the CMS API give a consistent format to error messages, even when they sometimes
   * come from parts of the system beyond our control.
   */
  private static String formatErrorMessage(String message) {
    if (isBlank(message)) {
      return message;
    }
    message = message.trim();

    // capitalize the first letter
    char firstChar = message.charAt(0);
    char newFirstChar = Character.toUpperCase(firstChar);
    if (newFirstChar != firstChar) {
      message = newFirstChar + message.substring(1);
    }

    // add a period if the sentence is not already punctuated
    if (!message.endsWith(".") && !message.endsWith("!") && !message.endsWith("?")) {
      message += ".";
    }

    return message;
  }

  /**
   * Returns an optional message to accompany {@link #getStatusCode()}
   */
  public String getStatusMessage() {
    return statusMessage;
  }

  /**
   * Returns the full path (not including http://server:port) by which this result can be referenced
   * via REST
   */
  public String getUri() {
    return uri;
  }

  /**
   * for internal use only
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * Returns true if {@link #getStatusCode()} has a non-error value
   */
  @JsonIgnore
  public boolean isSuccessful() {
    return statusCode == StatusCode.OK || statusCode == StatusCode.ACCEPTED
        || statusCode == StatusCode.IN_PROGRESS;
  }

  /**
   * Returns the {@link StatusCode} for this request, such as ERROR or OK.
   */
  public StatusCode getStatusCode() {
    return statusCode;
  }

  /**
   * Returns the status code and message
   */
  @Override
  public String toString() {
    if (isBlank(getStatusMessage())) {
      return getStatusCode().toString();
    } else {
      return getStatusCode() + ": " + getStatusMessage();
    }
  }
}
