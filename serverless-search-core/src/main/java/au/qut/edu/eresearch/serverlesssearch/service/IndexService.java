package au.qut.edu.eresearch.serverlesssearch.service;

import au.qut.edu.eresearch.serverlesssearch.index.AllField;
import au.qut.edu.eresearch.serverlesssearch.index.DocumentMapper;
import au.qut.edu.eresearch.serverlesssearch.index.IdField;
import au.qut.edu.eresearch.serverlesssearch.model.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@ApplicationScoped
public class IndexService {






    @ConfigProperty(name = "index.mount")
    String indexMount;

    private static final Logger LOGGER = Logger.getLogger(IndexService.class);


    public void index(List<IndexRequest> indexRequests) {
        Map<String, IndexWriter> writerMap = new HashMap<>();
        for (IndexRequest indexRequest : indexRequests) {
            IndexWriter writer;
            if (writerMap.containsKey(indexRequest.getIndex())) {
                writer = writerMap.get(indexRequest.getIndex());
            } else {
                writer = IndexUtils.getIndexWriter(indexMount, indexRequest.getIndex(), new IndexWriterConfig(new StandardAnalyzer())
                        .setOpenMode(IndexWriterConfig.OpenMode.CREATE));
                writerMap.put(indexRequest.getIndex(), writer);
            }
            try {
                if (Optional.ofNullable(indexRequest.getId()).isPresent()) {
                    writer.updateDocument(new Term(IdField.FIELD_NAME, indexRequest.getId()),
                            DocumentMapper.MAP_DOCUMENT.apply(indexRequest.getId(), indexRequest.getDocument()));
                } else {
                    writer.addDocument(DocumentMapper.MAP_DOCUMENT.apply(UUID.randomUUID().toString(), indexRequest.getDocument()));
                }
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
    }


    public void deleteIndex(String indexName) {
        if (!IndexUtils.indexPathExists(indexMount, indexName)) {
            throw new IndexNotFoundException(indexName);
        }
        IndexUtils.deleteIndex(indexMount, indexName);
    }




    public SearchResults search(String index, String queryString) {
        try {
            QueryParser qp = new QueryParser(AllField.FIELD_NAME, new StandardAnalyzer());
            Query query = qp.parse(queryString);
            IndexSearcher searcher = IndexUtils.getIndexSearcher(indexMount, index);
            long start = System.currentTimeMillis();
            TopDocs topDocs = searcher.search(query, 10);
            long end = System.currentTimeMillis();
            return SearchResults.builder()
                    .took(end - start)
                    .hits(Hits.builder()
                            .total(Total
                                    .builder()
                                    .value(topDocs.totalHits.value)
                                    .relation(topDocs.totalHits.relation == TotalHits.Relation.EQUAL_TO ? "eq" : "gte")
                                    .build())
                            .hits(Arrays.stream(topDocs.scoreDocs).sequential().map(scoreDoc ->
                                    Hit.builder()
                                            .index(index)
                                            .id(DocumentMapper.GET_ID.apply(IndexUtils.getDocument(searcher, scoreDoc)))
                                            .source(DocumentMapper.GET_SOURCE.apply(IndexUtils.getDocument(searcher, scoreDoc)))
                                            .score(scoreDoc.score).build()).collect(Collectors.toList()))
                            .build())
                    .build();
        } catch (ParseException | IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    public GetDocumentResult getDocument(String index, String id) {
        try {
            IndexSearcher searcher = IndexUtils.getIndexSearcher(indexMount, index);
            TopDocs topDocs = searcher.search(new TermQuery(new Term(IdField.FIELD_NAME, id)), 1);
            if (topDocs.totalHits.value > 0) {
                return GetDocumentResult.builder()
                        .index(index)
                        .id(id)
                        .source(DocumentMapper.GET_SOURCE.apply(IndexUtils.getDocument(searcher, topDocs.scoreDocs[0])))
                        .found(Boolean.TRUE)
                        .build();
            }
            return GetDocumentResult.builder()
                    .index(index)
                    .id(id)
                    .build();
        } catch (IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    public boolean hasDocument(String index, String id) {
        try {
            IndexSearcher searcher = IndexUtils.getIndexSearcher(indexMount, index);
            TopDocs topDocs = searcher.search(new TermQuery(new Term(IdField.FIELD_NAME, id)), 1);
            if (topDocs.totalHits.value > 0) {
                return true;
            }
            return false;
        } catch (IOException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }


}
