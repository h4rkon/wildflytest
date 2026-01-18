package com.example.status.core;

public record StatusCommand(String playerId, String reason, String correlationId) {}
