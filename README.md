# RAGlette (pun intended mostly 6 or 8)

DIY RAG (Retrieval-Augmented Generation)

Idea:
use Apache Tika Server(https://github.com/jpapenhagen/docker-tikaserver) as a Service in Docker-Compose<br>
for text extration from files (pdf/office docs)<br>
(send file by HTTP PUT and get JSON back)

than send to Ollama(https://github.com/ollama/ollama)<br>
Tutotial(https://ollama.com/blog/embedding-models)<br>
More Info(https://github.com/ollama/ollama/blob/main/docs/api.md#generate-embeddings)

build a JSON payload and upload into qdrant:<br>
https://qdrant.github.io/qdrant/redoc/index.html?v=master#section/Examples

### Start Qdrant and create collection 
- mv .env.example .env
- docker-compose up
- ./createCollection.sh


By splitting text extration and text embedding in seperate parts.
this can be scaled.

1. File upload by HTTP PUT to Tika Server
2. JSON response from Tika Server send to Ollama Endpoint
3. JSON response from Ollama Endpoint send to Qdrand Endpoint


--- old ---<br>
vector db [Qdrant](https://github.com/qdrant/qdrant)

and for text embeddings use model:
[all-MiniLM-L6-v2](https://huggingface.co/Xenova/all-MiniLM-L6-v2)
over [langchain4j](https://github.com/langchain4j/langchain4j)

like:
https://medium.com/@seeusimong/retrieval-augmented-generation-with-pgvector-and-ollama-e342967a0ff7
https://tembo.io/blog/sentence-transformers




