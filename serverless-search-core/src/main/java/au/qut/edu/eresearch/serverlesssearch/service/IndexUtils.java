package au.qut.edu.eresearch.serverlesssearch.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class IndexUtils {

    private static final Logger LOGGER = Logger.getLogger(IndexUtils.class);

    private static Path getIndexPath(String indexMount, String indexName) {
        return Paths.get(indexMount, indexName);
    }

    static boolean indexPathExists(String indexMount, String indexName) {
        return Files.exists(getIndexPath(indexMount, indexName));
    }

    private static RuntimeException mapIndexException(String index, IOException e) {
        if (e instanceof org.apache.lucene.index.IndexNotFoundException) {
            return new IndexNotFoundException(index, e);
        }
        LOGGER.error(String.format("Unexpected error occurred for index %s", index), e);
        return new RuntimeException(String.format("Unexpected error occurred for index %s", index), e);
    }

    static IndexWriter getIndexWriter(String indexMount, String indexName, IndexWriterConfig indexWriterConfig) {
        try {
            return new IndexWriter(
                    FSDirectory.open(getIndexPath(indexMount, indexName)),
                    indexWriterConfig
            );
        } catch (IOException e) {
            throw mapIndexException(indexName, e);
        }
    }

    static IndexSearcher getIndexSearcher(String indexMount, String indexName) {
        try {
            DirectoryReader newDirectoryReader = DirectoryReader.open(FSDirectory.open(getIndexPath(indexMount, indexName)));
            return new IndexSearcher(newDirectoryReader);
        } catch (IOException e) {
            throw mapIndexException(indexName, e);
        }
    }

    static Document getDocument(IndexSearcher indexSearcher, ScoreDoc scoreDoc) {
        try {
            return indexSearcher.doc(scoreDoc.doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void deleteIndex(String indexMount, String indexName) {

        try {
            IndexWriter writer = new IndexWriter(
                    FSDirectory.open(getIndexPath(indexMount, indexName)),
                    new IndexWriterConfig(new StandardAnalyzer())
                            .setOpenMode(IndexWriterConfig.OpenMode.CREATE));
            writer.deleteAll();
            writer.commit();
            writer.close();

            Files.walkFileTree(getIndexPath(indexMount, indexName),
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult postVisitDirectory(
                                Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(
                                Path file, BasicFileAttributes attrs)
                                throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });

        } catch (IOException e) {
            throw mapIndexException(indexName, e);
        }
    }


}
