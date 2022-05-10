package au.qut.edu.eresearch.serverlesssearch.handler;

import au.qut.edu.eresearch.serverlesssearch.index.Constants;
import au.qut.edu.eresearch.serverlesssearch.model.QueryRequest;
import au.qut.edu.eresearch.serverlesssearch.model.SearchResults;
import au.qut.edu.eresearch.serverlesssearch.service.IndexService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class SearchHandler {

    @Inject
    protected IndexService indexService;

    @GET
    @Path("/{index}/_search")
    @RolesAllowed({"search/all", "search/get"})
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults search(@PathParam("index") String index,
                                @QueryParam("q") String query) {
        return indexService.search(
                new QueryRequest()
                        .setIndex(index)
                        .setQuery(Constants.Query.MAP_QUERY_STRING_QUERY.apply(query)));
    }


}
