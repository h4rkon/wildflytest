package com.example.status.core;

public class StatusUseCase {

  public StatusResult execute(StatusCommand cmd) {
    // Pretend business logic + DB reads happen here.
    // Keep it boring: deterministic output.
    String status = (cmd.playerId() != null && cmd.playerId().startsWith("9"))
        ? "BLOCKED"
        : "OK";

    return new StatusResult(cmd.playerId(), status, cmd.correlationId());
  }
}
