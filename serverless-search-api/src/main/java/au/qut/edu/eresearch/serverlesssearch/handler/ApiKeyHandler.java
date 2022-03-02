package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.ApiKey;
import au.qut.edu.eresearch.serverlesssearch.model.ApiKeyRequest;
import au.qut.edu.eresearch.serverlesssearch.service.ApiKeyService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ApiKeyHandler {

    @Inject
    protected ApiKeyService apiKeyService;

    @POST
    @Path("/key")
    @RolesAllowed({"key/all", "key/post"})
    @Produces( MediaType.APPLICATION_JSON )
    public ApiKey search(ApiKeyRequest apiKeyRequest) {
        return apiKeyService.createKey(apiKeyRequest);
    }






}
