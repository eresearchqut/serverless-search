package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.index.QueryStringQueryBuilder;
import au.qut.edu.eresearch.serverlesssearch.index.TermQueryBuilder;
import au.qut.edu.eresearch.serverlesssearch.model.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@QuarkusTest
@TestProfile(ServiceTestProfile.class)
public class IndexServiceTest {

    @Inject
    IndexService indexService;

    @Test
    public void indexAndQueryNoId() {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = List.of(
                new IndexRequest()
                        .setIndex(index)
                        .setDocument(
                                Map.of("firstName", "James",
                                        "lastName", "Cagney")
                        ),
                new IndexRequest()
                        .setIndex(index)
                        .setDocument(
                                Map.of("firstName", "James",
                                        "lastName", "Cagney")
                        )

        );
        indexService.index(indexRequests);

        // when
        SearchResults results = indexService
                .search(index, new QueryStringQueryBuilder("lastName:Cagney"));

        // then
        Assertions.assertEquals(
                List.of(
                        Hit.builder().source(
                                        Map.of("firstName", "James",
                                                "lastName", "Cagney"))
                                .index(index)
                                .id(results.getHits().getHits().get(0).getId())
                                .score(0.082873434f).build(),
                        Hit.builder().source(
                                        Map.of("firstName", "James",
                                                "lastName", "Cagney"))
                                .index(index)
                                .id(results.getHits().getHits().get(1).getId())
                                .score(0.082873434f).build()
                ),
                results.getHits().getHits());

    }

    @Test
    public void indexAndQueryWithId() {

        // given
        String index = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();

        indexService.index(List.of(
                new IndexRequest()
                        .setIndex(index)
                        .setDocument(
                                Map.of("firstName", "James",
                                        "lastName", "Cagney")
                        )
                        .setId(id),
                new IndexRequest()
                        .setIndex(index)
                        .setDocument(
                                Map.of("firstName", "James",
                                        "lastName", "Dean")
                        )
                        .setId(id)
        ));

        // when
        SearchResults results = indexService
                .search(index, new QueryStringQueryBuilder("firstName:James"));

        // then
        Assertions.assertEquals(
                Hits.builder().total(Total.builder().value(1).relation("eq").build())
                        .hits(List.of(
                                Hit.builder().source(
                                                Map.of("firstName", "James",
                                                        "lastName", "Dean"))
                                        .index(index)
                                        .score(0.13076457f)
                                        .id(id)
                                        .build()
                        )).build(),
                results.getHits());


    }


    @Test
    public void indexAndQueryAll() {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = List.of(
                new IndexRequest()
                        .setIndex(index)
                        .setId("dt")
                        .setDocument(
                                Map.of("firstName", "Donald",
                                        "lastName", "Trump")
                        ),
                new IndexRequest()
                        .setIndex(index)
                        .setId("dd")
                        .setDocument(
                                Map.of("firstName", "Donald",
                                        "lastName", "Duck")
                        )

        );
        indexService.index(indexRequests);


        // when
        SearchResults results = indexService
                .search(index, new QueryStringQueryBuilder("donald"));


        // then
        Assertions.assertEquals(
                Hits.builder()
                        .total(Total.builder().value(2).relation("eq").build())
                        .hits(
                                List.of(
                                        Hit.builder().source(
                                                        Map.of("firstName", "Donald",
                                                                "lastName", "Trump"))
                                                .index(index)
                                                .id("dt")
                                                .score(0.082873434f).build(),
                                        Hit.builder().source(
                                                        Map.of("firstName", "Donald",
                                                                "lastName", "Duck"))
                                                .index(index)
                                                .id("dd")
                                                .score(0.082873434f).build()
                                )).build(),
                results.getHits());


    }

    @Test
    public void queryIndexNotFound() {

        // given
        String index = UUID.randomUUID().toString();


        // when
        Exception exception = Assertions.assertThrows(
                IndexNotFoundException.class,
                () -> indexService
                        .search(index, new QueryStringQueryBuilder("lastName:Cagney")));


        // then
        Assertions.assertEquals(String.format("no such index [%s]", index), exception.getMessage());


    }

    @Test
    public void deleteIndexNotFound() {

        // given
        String index = UUID.randomUUID().toString();


        // when
        Exception exception = Assertions.assertThrows(
                IndexNotFoundException.class,
                () -> indexService
                        .deleteIndex(index));


        // then
        Assertions.assertEquals(String.format("no such index [%s]", index), exception.getMessage());

    }


    @Test
    public void deleteIndex() {

        // given
        String index = UUID.randomUUID().toString();

        // when
        indexService.index(List.of(
                new IndexRequest()
                        .setIndex(index)
                        .setId("or")
                        .setDocument(
                                Map.of("firstName", "O'Doyle",
                                        "lastName", "Rules")
                        )));
        indexService.deleteIndex(index);

        // then
        Assertions.assertThrows(IndexNotFoundException.class, () -> indexService.deleteIndex(index));

    }


    @Test
    public void indexAndQueryNested() {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> iIndexRequests = List.of(
                new IndexRequest()
                        .setIndex(index)
                        .setId("coolridge")
                        .setDocument(
                                Map.of("person", Map.of("firstName", "Calvin", "lastName", "Coolridge"))
                        ),
                new IndexRequest()
                        .setIndex(index)
                        .setId("harrison")
                        .setDocument(
                                Map.of("person", Map.of("firstName", "William", "lastName", "Harrison"))
                        )
        );
        indexService.index(iIndexRequests);


        // when
        SearchResults results = indexService
                .search(index, new QueryStringQueryBuilder("person.firstName:Calvin"));


        // then
        Assertions.assertEquals(
                Hits.builder()
                        .total(Total.builder().value(1).relation("eq").build())
                        .hits(
                                List.of(
                                        Hit.builder().source(
                                                        Map.of("person", Map.of("firstName", "Calvin", "lastName", "Coolridge")))
                                                .index(index)
                                                .id("coolridge")
                                                .score(0.31506687f).build()
                                )).build(),
                results.getHits());

    }

    @Test
    public void indexAndGetDocument() {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = List.of(new IndexRequest()
                .setIndex(index)
                .setId("calvin-and-hobbs")
                .setDocument(Map.of("firstName", "Calvin", "lastName", "Hobbs")));
        indexService.index(indexRequests);

        // when
        GetDocumentResult document = indexService
                .getDocument(index, "calvin-and-hobbs");

        // then
        Assertions.assertEquals("calvin-and-hobbs", document.getId());
        Assertions.assertEquals(index, document.getIndex());
        Assertions.assertTrue(document.isFound());
        Assertions.assertEquals(Map.of("firstName", "Calvin", "lastName", "Hobbs"), document.getSource());

        document = indexService
                .getDocument(index, "hobbs-and-calvin");

        // then
        Assertions.assertEquals("hobbs-and-calvin", document.getId());
        Assertions.assertEquals(index, document.getIndex());
        Assertions.assertFalse(document.isFound());
        Assertions.assertNull(document.getSource());

    }

    @Test
    public void termMatch() {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = List.of(new IndexRequest()
                .setIndex(index)
                .setId("i-am-a-term")
                .setDocument(Map.of("firstName", "The", "lastName", "Terminator")));
        indexService.index(indexRequests);

        // when
        SearchResults results = indexService
                .search(index, new TermQueryBuilder("_id", "i-am-a-term"));

        // then
        Assertions.assertEquals(
                Hits.builder()
                        .total(Total.builder().value(1).relation("eq").build())
                        .hits(
                                List.of(
                                        Hit.builder().source(
                                                        Map.of("firstName", "The", "lastName", "Terminator"))
                                                .index(index)
                                                .id("i-am-a-term")
                                                .score(0.13076457f).build()
                                )).build(),
                results.getHits());

    }

}