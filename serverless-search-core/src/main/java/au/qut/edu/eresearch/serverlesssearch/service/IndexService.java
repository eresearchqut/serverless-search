package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.index.AllField;
import au.qut.edu.eresearch.serverlesssearch.index.FieldMapper;
import au.qut.edu.eresearch.serverlesssearch.index.SourceField;
import au.qut.edu.eresearch.serverlesssearch.model.*;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class IndexService {

    public static final String ID_TERM = "_id";

    @ConfigProperty(name = "index.mount")
    String indexMount;

    private static final Logger LOGGER = Logger.getLogger(IndexService.class);


    public List<IndexResult> index(List<IndexRequest> indexRequests) {
        Map<String, IndexWriter> writerMap = new HashMap<>();
        List<IndexResult> indexResults = new ArrayList<>();
        for (IndexRequest indexRequest : indexRequests) {
            IndexWriter writer;
            if (writerMap.containsKey(indexRequest.getIndex())) {
                writer = writerMap.get(indexRequest.getIndex());
            } else {
                writer = IndexUtils.getIndexWriter(indexMount, indexRequest.getIndex(), new IndexWriterConfig(new StandardAnalyzer())
                        .setOpenMode(IndexWriterConfig.OpenMode.CREATE));
                writerMap.put(indexRequest.getIndex(), writer);
            }
            Document document = new Document();
            FieldMapper.FIELDS.apply(indexRequest.getDocument()).forEach(document::add);
            String id = Optional.ofNullable(indexRequest.getId()).orElse(UUID.randomUUID().toString());
            try {
                if (indexRequest.getId() != null) {
                    document.add(new StringField(ID_TERM, indexRequest.getId(), Field.Store.YES));
                    writer.updateDocument(new Term(ID_TERM, indexRequest.getId()), document);
                } else {
                    document.add(new StringField(ID_TERM, id, Field.Store.YES));
                    writer.addDocument(document);
                }
                indexResults.add(new IndexResult().setIndex(indexRequest.getIndex()).setId(id));
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        for (IndexWriter writer : writerMap.values()) {
            try {
                writer.commit();
                writer.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        return indexResults;
    }


    public void deleteIndex(String indexName) {
        if (!IndexUtils.indexPathExists(indexMount, indexName)) {
            throw new IndexNotFoundException(indexName);
        }
        IndexUtils.deleteIndex(indexMount, indexName);
    }


    public SearchResults search(SearchRequest searchRequest) {
        QueryParser qp = new QueryParser(AllField.FIELD_NAME, new StandardAnalyzer());
        SearchResults searchResults = new SearchResults();
        try {
            Query query = qp.parse(searchRequest.getQuery());
            IndexSearcher searcher = IndexUtils.getIndexSearcher(indexMount, searchRequest.getIndexName());
            long start = System.currentTimeMillis();
            TopDocs topDocs = searcher.search(query, 10);
            long end = System.currentTimeMillis();
            for (ScoreDoc scoreDocs : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDocs.doc);
                String sourceField = document.get(SourceField.FIELD_NAME);
                Map<String, Object> source = JsonUnflattener.unflattenAsMap(sourceField);
                searchResults.getHits().getHits().add(new Hit()
                        .setSource(source)
                        .setScore(scoreDocs.score)
                        .setIndex(searchRequest.getIndexName())
                        .setId(document.get(ID_TERM))
                );
            }
            searchResults
                    .setTook(end - start)
                    .getHits().getTotal()
                    .setValue(topDocs.totalHits.value)
                    .setRelation(topDocs.totalHits.relation == TotalHits.Relation.EQUAL_TO ? "eq" : "gte");
            return searchResults;
        } catch (ParseException | IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    public GetResult getDocument(GetRequest getRequest) {

        try {
            GetResult result = new GetResult().setIndex(getRequest.getIndex()).setId(getRequest.getId());
            IndexSearcher searcher = IndexUtils.getIndexSearcher(indexMount, getRequest.getIndex());
            TopDocs topDocs = searcher.search(new TermQuery(new Term(ID_TERM, getRequest.getId())), 1);
            if (topDocs.totalHits.value > 0) {
                Document document = searcher.doc(topDocs.scoreDocs[0].doc);
                String sourceField = document.get(SourceField.FIELD_NAME);
                Map<String, Object> source = JsonUnflattener.unflattenAsMap(sourceField);
                result.setFound(true).setSource(source);
            }
            return result;
        } catch (IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }


}
