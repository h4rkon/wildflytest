package com.example.status.adapter;

import jakarta.ws.rs.core.HttpHeaders;
import java.util.Optional;

public class HeaderValidator {

  public record HeaderInfo(String apiVersion, String user, String correlationId) {}

  public HeaderInfo validate(HttpHeaders headers) {
    String apiVersion = first(headers, "X-Api-Version")
        .orElseThrow(() -> new IllegalArgumentException("Missing header X-Api-Version"));

    String user = first(headers, "X-User")
        .orElseThrow(() -> new IllegalArgumentException("Missing header X-User"));

    // Optional: generate if missing
    String corr = first(headers, "X-Correlation-Id").orElse(java.util.UUID.randomUUID().toString());

    return new HeaderInfo(apiVersion, user, corr);
  }

  private Optional<String> first(HttpHeaders headers, String name) {
    var values = headers.getRequestHeader(name);
    if (values == null || values.isEmpty()) return Optional.empty();
    return Optional.ofNullable(values.get(0)).filter(v -> !v.isBlank());
  }
}
