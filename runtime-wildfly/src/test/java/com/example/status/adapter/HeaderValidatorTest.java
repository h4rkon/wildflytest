package com.example.status.adapter;

import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HeaderValidatorTest {

  @Test
  void failsWhenApiVersionMissing() {
    var headers = mock(HttpHeaders.class);
    when(headers.getRequestHeader("X-Api-Version")).thenReturn(List.of());
    when(headers.getRequestHeader("X-User")).thenReturn(List.of("demo"));

    var v = new HeaderValidator();
    var ex = assertThrows(IllegalArgumentException.class, () -> v.validate(headers));
    assertTrue(ex.getMessage().contains("X-Api-Version"));
  }

  @Test
  void generatesCorrelationIdWhenMissing() {
    var headers = mock(HttpHeaders.class);
    when(headers.getRequestHeader("X-Api-Version")).thenReturn(List.of("1"));
    when(headers.getRequestHeader("X-User")).thenReturn(List.of("demo"));
    when(headers.getRequestHeader("X-Correlation-Id")).thenReturn(List.of());

    var v = new HeaderValidator();
    var info = v.validate(headers);

    assertEquals("1", info.apiVersion());
    assertEquals("demo", info.user());
    assertNotNull(info.correlationId());
    assertFalse(info.correlationId().isBlank());
  }
}
