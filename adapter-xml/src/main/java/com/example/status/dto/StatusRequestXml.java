package com.example.status.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "statusRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatusRequestXml {

  @XmlElement(required = true)
  public String playerId;

  @XmlElement(required = false)
  public String reason;
}
