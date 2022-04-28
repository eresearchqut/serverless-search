package au.qut.edu.eresearch.serverlesssearch.client;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
@TestProfile(au.qut.edu.eresearch.serverlesssearch.TestProfile.class)
public class SolrClientTest {

    @Inject
    SolrClient solrClient;

    @Test
    public void initialise()  {
        Assert.assertNotNull(solrClient);
    }
}
