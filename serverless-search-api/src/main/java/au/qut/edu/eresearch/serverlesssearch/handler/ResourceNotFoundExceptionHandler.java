package au.qut.edu.eresearch.serverlesssearch.handler;

import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundExceptionHandler implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}
