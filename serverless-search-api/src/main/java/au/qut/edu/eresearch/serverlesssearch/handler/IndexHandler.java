package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.model.Document;
import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;
import au.qut.edu.eresearch.serverlesssearch.service.IndexUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;

@Path("/")
public class IndexHandler {

    @Inject
    SqsClient sqs;

    @ConfigProperty(name = "queue.url")
    String queueUrl;

    @Inject
    protected IndexService indexService;

    private static final ObjectWriter INDEX_REQUEST_WRITER = new ObjectMapper().writerFor(IndexRequest.class);

    @PUT
    @Path("/{index}/_doc/{id}")
    @RolesAllowed({"index/all", "index/put"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Document updateDocument(@PathParam("index") String index, @PathParam("id") String id, Map<String, Object> document) throws Exception {

        // Indexing is async we check the index name prior to submitting
        IndexUtils.validateIndexName(index);

        IndexRequest indexRequest = new IndexRequest().setIndex(index).setId(id).setDocument(document);
        String message = INDEX_REQUEST_WRITER.writeValueAsString(indexRequest);
        sqs.sendMessage(m -> m.queueUrl(queueUrl).messageBody(message).messageGroupId(index)
                .messageDeduplicationId(String.format("%s:%d", index, indexRequest.hashCode())));
        return Document.builder().id(id).index(index).build();
    }

    @POST
    @Path("/{index}/_doc/{id}")
    @RolesAllowed({"index/all", "index/post"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Document updateDocumentPost(Map<String, Object> document, @PathParam("index") String index, @PathParam("id") String id) throws Exception {
        return updateDocument(index, id, document);
    }

    @POST
    @Path("/{index}/_doc")
    @RolesAllowed({"index/all", "index/post"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Document addDocument(Map<String, Object> document, @PathParam("index") String index) throws Exception {
        String id = UUID.randomUUID().toString();
        return updateDocument(index, id, document);
    }

    @DELETE
    @Path("/{index}")
    @RolesAllowed({"index/all", "index/delete"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteIndex(@PathParam("index") String index) {
        indexService.deleteIndex(index);
        return Response.ok().build();
    }

    @GET
    @Path("/{index}/_doc/{id}")
    @RolesAllowed({"index/all", "index/get"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Document getDocument(@PathParam("index") String index, @PathParam("id") String id) {
        return indexService.getDocument(index, id);
    }

    @HEAD
    @Path("/{index}/_doc/{id}")
    @RolesAllowed({"index/all", "index/get"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response hasDocument(@PathParam("index") String index, @PathParam("id") String id) {
        return indexService.hasDocument(index, id) ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

}
