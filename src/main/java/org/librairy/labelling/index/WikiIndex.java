package org.librairy.labelling.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.codehaus.jackson.map.ObjectMapper;
import org.librairy.labelling.model.Relevance;
import org.librairy.labelling.model.WikiArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class WikiIndex {

    private static final Logger LOG = LoggerFactory.getLogger(WikiIndex.class);

    private AtomicInteger counter;

    private ObjectMapper jsonMapper;

    private IndexWriter writer;


    private Analyzer analyzer = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String s) {
            Tokenizer tokenizer = new StandardTokenizer();

            TokenFilter filter = new StandardFilter(tokenizer);
            filter = new LowerCaseFilter(filter);
            filter = new StopFilter(filter, StopAnalyzer.ENGLISH_STOP_WORDS_SET );
            return new TokenStreamComponents(tokenizer,filter);
        }
    };
    private DirectoryReader reader;
    private FSDirectory directory;

    public WikiIndex(String path) {
        initialize(path,false);
    }

    public WikiIndex(String path, Boolean overwrite) {
        initialize(path,overwrite);
    }

    private void initialize(String path, Boolean overwrite) {
        try {
            File indexFile = new File(path);
            this.jsonMapper = new ObjectMapper();
            this.directory = FSDirectory.open(indexFile.toPath());
            if (indexFile.exists() && !overwrite){
                this.reader = DirectoryReader.open(directory);
            }else{
                if (overwrite && indexFile.exists()) indexFile.delete();
                IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
                writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
                writerConfig.setRAMBufferSizeMB(500.0);
                this.writer = new IndexWriter(directory, writerConfig);
            }
            this.counter = new AtomicInteger();

        } catch (Exception e) {
            LOG.error("Unexpected error creating base directory",e);
            new RuntimeException(e);
        }
    }

    public void add(WikiArticle article) {
        // add point to lucene index
        try {
            if ((writer == null) || (!writer.isOpen())) throw new RuntimeException("Index is closed. A new instance is required");

            Document luceneDoc = new Document();
            // id
            luceneDoc.add(new StringField("id", article.getUrl(), Field.Store.YES));

            luceneDoc.add(new StringField("title", article.getTitle(), Field.Store.YES));
            // point
            luceneDoc.add(new TextField("text", article.getText(), Field.Store.NO));

            writer.addDocument(luceneDoc);
            if (counter.incrementAndGet() % 100 == 0 ) {
                writer.commit();
            }
        } catch (Exception e) {
            new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        if (writer.hasUncommittedChanges()) writer.commit();
        writer.close();
    }

    public List<Relevance<WikiArticle>> get(String terms, Integer max){

        try{
            if (this.reader == null){
                reader = DirectoryReader.open(directory);
            }
            TopScoreDocCollector collector = TopScoreDocCollector.create(max);
            IndexSearcher searcher  = new IndexSearcher(reader);
            searcher.setSimilarity(new BM25Similarity());

            int size = reader.numDocs();

            QueryParser parser = new QueryParser("text", analyzer);

            Query query = parser.parse(terms);
            searcher.search(query, collector);
            TopDocs results = collector.topDocs();

            return Arrays.stream(results.scoreDocs).parallel().map(sd -> {
                try {
                    Document docIndexed     = reader.document(sd.doc);
                    float score = sd.score;

                    WikiArticle article = new WikiArticle();

                    article.setId(String.format(docIndexed.get("id")));
                    article.setTitle(String.format(docIndexed.get("title")));
                    article.setUrl(String.format(docIndexed.get("id")));

                    return new Relevance<WikiArticle>(article,score);
                } catch (Exception e) {
                    LOG.warn("Error getting neighbour", e);
                    return null;
                }
            }).filter(a -> a != null).collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


}
