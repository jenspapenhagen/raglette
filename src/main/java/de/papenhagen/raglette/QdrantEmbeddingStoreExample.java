package de.papenhagen.raglette;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;

import java.io.File;
import java.util.List;

public class QdrantEmbeddingStoreExample {

    public static void main(String[] args) {

        //Model all-MiniLM-L6-v2
        final EmbeddingStore<TextSegment> embeddingStore =
                QdrantEmbeddingStore.builder()
                        // Ensure the collection is configured with the appropriate dimensions (384)
                        // of the embedding model.
                        .collectionName(System.getenv("QDRANT_COLLECTION"))
                        .host(System.getenv("QDRANT_URL"))
                        // GRPC port of the Qdrant server
                        .port(6334)
                        .build();

        final EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        //feeding
        final List<String> fileNames = FileUtil.readLocalDocuments();
        for (final String fileName : fileNames) {
            final File tempFile = new File(fileName);
            final String text = PdfUtil.getText(tempFile);
            final String metaText = PdfUtil.getMetaText(tempFile);

            final TextSegment segment = TextSegment.from(text + metaText);
            final Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
        }

        //qasking
        Embedding queryEmbedding = embeddingModel.embed("Did you ever travel abroad?").content();
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
        EmbeddingMatch<TextSegment> embeddingMatch = relevant.getFirst();

        System.out.println(embeddingMatch.score());
        System.out.println(embeddingMatch.embedded().text());
    }
}
