# RAGlette (pun intended mostly 6 or 8)

JAVA base DIY RAG (Retrieval-Augmented Generation)

built on [Qdrant](https://github.com/qdrant/qdrant)

and for text embeddings use model:

[all-MiniLM-L6-v2](https://huggingface.co/Xenova/all-MiniLM-L6-v2)
over [langchain4j](https://github.com/langchain4j/langchain4j)

like:
https://medium.com/@seeusimong/retrieval-augmented-generation-with-pgvector-and-ollama-e342967a0ff7
https://tembo.io/blog/sentence-transformers



### Install
- mv .env.example .env
- docker-compose up
- ./createCollection.sh
