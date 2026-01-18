package com.example.status.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusUseCaseTest {

  @Test
  void returnsBlocked_whenPlayerIdStartsWith9() {
    var useCase = new StatusUseCase();

    var cmd = new StatusCommand("9123", "demo", "corr-123");
    var result = useCase.execute(cmd);

    assertEquals("9123", result.playerId());
    assertEquals("BLOCKED", result.status());
    assertEquals("corr-123", result.correlationId());
  }

  @Test
  void returnsOk_whenPlayerIdDoesNotStartWith9() {
    var useCase = new StatusUseCase();

    var cmd = new StatusCommand("1234", null, "corr-xyz");
    var result = useCase.execute(cmd);

    assertEquals("OK", result.status());
  }

  @Test
  void doesNotCrash_onNullReason() {
    var useCase = new StatusUseCase();

    var cmd = new StatusCommand("1234", null, "corr-1");
    assertDoesNotThrow(() -> useCase.execute(cmd));
  }
}
