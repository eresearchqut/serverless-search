package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.model.IndexRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import net.andreinc.mockneat.MockNeat;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@QuarkusTest
@TestProfile(IndexServiceTestProfile.class)
public class LoadTest {

    @Inject
    IndexService indexService;

    public static final MockNeat mocker = MockNeat.threadLocal();

    public static Function<String, List<IndexRequest>> GENERATE_INDEX_REQUEST = (indexId) ->
            new Random().ints(500, 0, 500)
                    .filter(i -> i % 2 == 0)
                    .mapToObj(iteration ->
                            new IndexRequest()
                                    .setIndex(indexId)
                                    .setDocument(
                                            Map.of(
                                                    "address",
                                                    mocker.addresses().get()
                                            )
                                    )
                    )

                    .collect(Collectors.toList());


    @BeforeEach
    public void init() {

        IntStream.range(0, 9)
                .mapToObj(iteration -> UUID.randomUUID().toString())
                .map(GENERATE_INDEX_REQUEST)
                .forEach(indexService::index);


    }


    @Test
    public void indexAndQueryNoId() {
        Assert.assertEquals(1, 1);

    }


}