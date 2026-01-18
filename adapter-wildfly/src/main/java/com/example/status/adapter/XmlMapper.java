package com.example.status.adapter;

import com.example.status.core.StatusCommand;
import com.example.status.core.StatusResult;
import com.example.status.dto.StatusRequestXml;
import com.example.status.dto.StatusResponseXml;

public class XmlMapper {

  public StatusCommand toCommand(StatusRequestXml req, String correlationId) {
    return new StatusCommand(req.playerId, req.reason, correlationId);
  }

  public StatusResponseXml toResponse(StatusResult result) {
    return StatusResponseXml.ok(result.playerId(), result.status(), result.correlationId());
  }
}
