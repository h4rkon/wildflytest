package com.example.status;

import com.example.status.adapter.HeaderValidator;
import com.example.status.adapter.XmlMapper;
import com.example.status.core.StatusUseCase;
import com.example.status.dto.StatusRequestXml;
import com.example.status.dto.StatusResponseXml;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/status")
public class StatusResource {

  // For the repro, keep wiring explicit (no CDI magic needed).
  private final HeaderValidator headerValidator = new HeaderValidator();
  private final XmlMapper mapper = new XmlMapper();
  private final StatusUseCase useCase = new StatusUseCase();

  @POST
  @Path("/player")
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public Response getPlayerStatus(@Context HttpHeaders headers, StatusRequestXml requestXml) {
    try {
      var headerInfo = headerValidator.validate(headers);

      // Transport -> domain
       var cmd = mapper.toCommand(requestXml, headerInfo.correlationId());

      // One call only
      var result = useCase.execute(cmd);

      // Domain -> transport
      var responseXml = mapper.toResponse(result);

      return Response.ok(responseXml).build();

    } catch (IllegalArgumentException badRequest) {
      // For XML API v1 you might still return 200 (legacy); here we do 400 for sanity
      var corr = java.util.UUID.randomUUID().toString();
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(StatusResponseXml.error(corr, badRequest.getMessage()))
          .build();

    } catch (Exception unexpected) {
      var corr = java.util.UUID.randomUUID().toString();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StatusResponseXml.error(corr, "Internal error"))
          .build();
    }
  }
}
