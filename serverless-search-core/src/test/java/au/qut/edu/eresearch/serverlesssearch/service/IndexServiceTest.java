package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.model.*;
import au.qut.edu.eresearch.serverlesssearch.query.QueryMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.inject.Inject;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@QuarkusTest
@TestProfile(IndexServiceTestProfile.class)
public class IndexServiceTest {

    @Inject
    IndexService indexService;

    public static final MockNeat mocker = MockNeat.threadLocal();


    public static BiFunction<Integer, String, List<IndexRequest>> GENERATE_INDEX_REQUEST = (count, indexId) ->
            IntStream.range(0, count)
                    .mapToObj(iteration ->
                            new IndexRequest()
                                    .setIndex(indexId)
                                    .setDocument(
                                            Map.of(
                                                    "address",
                                                    mocker.addresses().get(),
                                                    "name",
                                                    mocker.names().get(),
                                                    "dob",
                                                    mocker.localDates().get().toString(),
                                                    "age",
                                                    mocker.ints().from(IntStream.range(0, 120).toArray()).get()
                                            )
                                    )
                    )
                    .collect(Collectors.toList());


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
                .search(new QueryRequest()
                        .setIndex(index)
                        .setQuery(QueryMapper.QUERY_STRING_QUERY_MAP.apply("lastName:Cagney")));


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
                .search(new QueryRequest()
                        .setIndex(index)
                        .setQuery(QueryMapper.QUERY_STRING_QUERY_MAP.apply("firstName:James")));

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
                .search(new QueryRequest().setIndex(index).setQuery(QueryMapper.QUERY_STRING_QUERY_MAP.apply("donald")));


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
                        .search(new QueryRequest().setIndex(index).setQuery(QueryMapper.QUERY_STRING_QUERY_MAP.apply("lastName:cagney"))));


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
                .search(new QueryRequest()
                        .setIndex(index)
                        .setQuery(QueryMapper.QUERY_STRING_QUERY_MAP.apply("person.firstName:Calvin")));


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
        SearchResults results = indexService.search(new QueryRequest().setIndex(index)
                .setQuery(QueryMapper.TERM_QUERY_MAP.apply("_id", "i-am-a-term")));

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


    @ParameterizedTest
    @ValueSource(ints = {1, 5, 9, 10, 11, 50, 51})
    public void pageFrom(int recordCount) {

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = GENERATE_INDEX_REQUEST.apply(recordCount, index);
        indexService.index(indexRequests);

        // when
        SearchResults results = indexService.search(new QueryRequest().setIndex(index)
                .setQuery(QueryMapper.MATCH_ALL_QUERY_MAP));


        // then
        long totalHits = results.getHits().getTotal().getValue();
        int from = 0;
        Set<String> ids = new HashSet<>();
        while (from < totalHits) {
            ids.addAll(results.getHits().getHits().stream().map(Hit::getId).collect(Collectors.toList()));
            from += results.getHits().getHits().size();
            results = indexService.search(new QueryRequest().setIndex(index).setFrom(from)
                    .setQuery(QueryMapper.MATCH_ALL_QUERY_MAP));
        }

        Assertions.assertEquals(totalHits, ids.size());

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 5, 10, 25, 100, 150, 200})
    public void pageSize(int size) {

        var indexSize = 198;

        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = GENERATE_INDEX_REQUEST.apply(indexSize, index);
        indexService.index(indexRequests);

        // when
        SearchResults results = indexService.search(new QueryRequest().setIndex(index).setSize(size)
                .setQuery(QueryMapper.MATCH_ALL_QUERY_MAP));


        // then
        long totalHits = results.getHits().getTotal().getValue();
        int paged = 0;
        int from = 0;

        Set<String> ids = new HashSet<>();
        while (from < totalHits) {
            paged += 1;
            ids.addAll(results.getHits().getHits().stream().map(Hit::getId).collect(Collectors.toList()));
            from += results.getHits().getHits().size();
            results = indexService.search(new QueryRequest().setIndex(index).setSize(size).setFrom(from)
                    .setQuery(QueryMapper.MATCH_ALL_QUERY_MAP));

        }

        Assertions.assertEquals(totalHits, ids.size());

        int expectedPages = indexSize / size + (indexSize % size == 0 ? 0 : 1);
        Assertions.assertEquals(expectedPages, paged);

    }


    @ParameterizedTest
    @ValueSource(strings = {"name", "dob", "age"})
    public void sortField(String sortField) {


        // given
        String index = UUID.randomUUID().toString();
        List<IndexRequest> indexRequests = GENERATE_INDEX_REQUEST.apply(30, index);
        indexService.index(indexRequests);

        // when
        SearchResults results = indexService.search(new QueryRequest().setIndex(index)
                .setSort(List.of(Map.of(sortField, (Map<String, String>)Collections.EMPTY_MAP)))
                .setQuery(QueryMapper.MATCH_ALL_QUERY_MAP));

        // then
        List<?> values = results.getHits().getHits().stream()
                .map(Hit::getSource)
                .map(source -> source.get(sortField))
                .collect(Collectors.toList());

        List<?> expected = List.copyOf(values).stream().sorted().collect(Collectors.toList());

        Assertions.assertEquals(expected, values);


    }

}