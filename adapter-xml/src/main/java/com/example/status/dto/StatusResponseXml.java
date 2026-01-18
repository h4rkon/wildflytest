package com.example.status.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "statusResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatusResponseXml {

  @XmlElement
  public String playerId;

  @XmlElement
  public String status;

  @XmlElement
  public String correlationId;

  @XmlElement
  public String message;

  public static StatusResponseXml ok(String playerId, String status, String correlationId) {
    var r = new StatusResponseXml();
    r.playerId = playerId;
    r.status = status;
    r.correlationId = correlationId;
    r.message = "OK";
    return r;
  }

  public static StatusResponseXml error(String correlationId, String message) {
    var r = new StatusResponseXml();
    r.correlationId = correlationId;
    r.message = message;
    r.status = "ERROR";
    return r;
  }
}
