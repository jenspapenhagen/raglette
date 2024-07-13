# RAGlette (pun intended mostly 6 or 8)

DIY RAG (Retrieval-Augmented Generation)

Idea:
use Apache Tika Server(<https://github.com/jpapenhagen/docker-tikaserver>) as a Service in Docker-Compose\
for text extration from files (pdf/office docs)\
(send file by HTTP PUT and get JSON back)

than send to Ollama(<https://github.com/ollama/ollama>)\
Tutotial(<https://ollama.com/blog/embedding-models>)\
More Info(<https://github.com/ollama/ollama/blob/main/docs/api.md#generate-embeddings>)

build a JSON payload and upload into qdrant:\
<https://qdrant.github.io/qdrant/redoc/index.html?v=master#section/Examples>

### Start Qdrant and create collection

- mv .env.example .env
- docker-compose up
- ./createCollection.sh

By splitting text extration and text embedding in seperate parts.
this can be scaled.

1. File upload by HTTP PUT to Tika Server
2. JSON response from Tika Server send to Ollama Endpoint
3. JSON response from Ollama Endpoint send to Qdrand Endpoint

extract text from test.pdf

```shell
curl -T documents/test.pdf http://localhost:9998/tika/text --header "Accept: application/json"
```

upload text to ollama

```shell
curl http://localhost:11434/api/embeddings -d '{
  "model": "nomic-embed-text",
  "prompt": "XXXXX"
}'
```

upload emdedding to qdrant\
hints:\

- count id up by hand
- custommice the payload

```shell
curl -L -X PUT 'http://localhost:6333/collections/test_collection/points?wait=true' \ -H 'Content-Type: application/json' \ --data-raw '{
  "points": [
    {"id": 1, "vector": [0.05, 0.61, 0.76, 0.74], "payload": {"filename": "test.pdf"}},
  ]
}'
```

<https://ollama.com/library/nomic-embed-text>

--- old ---\
vector db [Qdrant](https://github.com/qdrant/qdrant)

and for text embeddings use model:
[all-MiniLM-L6-v2](https://huggingface.co/Xenova/all-MiniLM-L6-v2)
over [langchain4j](https://github.com/langchain4j/langchain4j)

links:

- <https://medium.com/@seeusimong/retrieval-augmented-generation-with-pgvector-and-ollama-e342967a0ff7>
- <https://tembo.io/blog/sentence-transformers>
