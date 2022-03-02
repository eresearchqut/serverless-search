package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.ApiKey;
import au.qut.edu.eresearch.serverlesssearch.model.ApiKeyRequest;
import au.qut.edu.eresearch.serverlesssearch.service.ApiKeyService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
public class ApiKeyHandler {

    @Inject
    protected ApiKeyService apiKeyService;

    @POST
    @Path("/key")
    @RolesAllowed({"key/all", "key/post"})
    @Produces( MediaType.APPLICATION_JSON )
    public ApiKey createKey(ApiKeyRequest apiKeyRequest) {
        return apiKeyService.createKey(apiKeyRequest);
    }


    @GET
    @Path("/key/{clientId}")
    @RolesAllowed({"key/all", "key/get"})
    @Produces( MediaType.APPLICATION_JSON )
    public ApiKey getKey(@PathParam("clientId") String clientId) {
        return apiKeyService.getKey(clientId);
    }

    @DELETE
    @Path("/key/{clientId}")
    @RolesAllowed({"key/all", "key/delete"})
    @Produces( MediaType.APPLICATION_JSON )
    public Response deleteKey(@PathParam("clientId") String clientId) {
        return Response.ok().build();
    }

    @GET
    @Path("/key")
    @RolesAllowed({"key/all", "key/list"})
    @Produces( MediaType.APPLICATION_JSON )
    public List<ApiKey> listKeys() {
        return apiKeyService.listKeys();
    }


}
