package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.Constants;
import au.qut.edu.eresearch.serverlesssearch.model.QueryRequest;
import au.qut.edu.eresearch.serverlesssearch.model.SearchResults;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class SearchHandler {

    @Inject
    protected IndexService indexService;

    public static final String QUERY_STRING_QUERY_PARAM = "q";

    public static final String QUERY_STRING_FROM_PARAM = "from";

    public static final String QUERY_STRING_SIZE_PARAM = "size";

    @GET
    @Path("/{index}/_search")
    @RolesAllowed({"search/all", "search/get"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults search(@PathParam("index") String index,
                                @Context UriInfo uriInfo, QueryRequest queryRequest) {


        MultivaluedMap<String, String> params =
                uriInfo.getQueryParameters();

        if (params.containsKey(QUERY_STRING_QUERY_PARAM)) {
            String q = params.getFirst(QUERY_STRING_QUERY_PARAM);
            Integer from = params.containsKey(QUERY_STRING_FROM_PARAM) ? Integer.valueOf(params.getFirst(QUERY_STRING_FROM_PARAM)) : null;
            Integer size = params.containsKey(QUERY_STRING_SIZE_PARAM) ? Integer.valueOf(params.getFirst(QUERY_STRING_SIZE_PARAM)) : null;
            return indexService.search(
                    new QueryRequest()
                            .setFrom(from)
                            .setFrom(size)
                            .setIndex(index)
                            .setQuery(Constants.Query.QUERY_STRING_QUERY_MAP.apply(q)));
        }

        return indexService.search(queryRequest.setIndex(index));
    }


}
