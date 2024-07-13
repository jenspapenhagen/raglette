import flask
from flask import request, jsonify
import requests
import logging
import sys
import uuid
from werkzeug.exceptions import HTTPException
from qdrant_client import QdrantClient, models
import json

app = flask.Flask(__name__)

# Configure logging
logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
                    handlers=[logging.StreamHandler(sys.stdout)])
logger = logging.getLogger(__name__)

# Ensure Flask's logger is also set to DEBUG
app.logger.setLevel(logging.DEBUG)

OLLAMA_API_URL = "http://localhost:11434/api/embeddings"
QDRAD_URL = "http://localhost:6333"

@app.errorhandler(Exception)
def handle_exception(e):
    if isinstance(e, HTTPException):
        return e
    logger.error(f"An unhandled exception occurred: {str(e)}", exc_info=True)
    return jsonify(error="An unexpected error occurred"), 500

def process_input(input_item:str, filename:str):   
    logger.debug(f"Processing single input: {input_item[:100]}...")  # Log first 100 chars
    
    # Send request to Ollama
    response = requests.post(OLLAMA_API_URL, json={
        "model": "nomic-embed-text",
        "prompt": input_item
    })
    
    logger.debug(f"Ollama API response status code: {response.status_code}")
    
    if response.status_code == 200:
        embedding = response.json().get('embedding')
        if embedding:
            logger.debug(f"Received embedding of length: {len(embedding)}")
            logger.debug(f"First few values of embedding: {embedding[:5]}...")
        else:
            logger.error("No embedding found in Ollama API response")
            return None
    else:
        logger.error(f"Ollama API request failed with status code {response.status_code}")
        return None
    
    #than upload to qrant
    client = QdrantClient(url=QDRAD_URL)
    operation_info = client.upsert(
    collection_name="test_collection",
    wait=True,
    points=[
        models.PointStruct(
            id= str(uuid.uuid4()), 
            vector=embedding, 
            payload={
                "filename": filename
                }
            ),
        ],   
    )
    return operation_info;


@app.route('/embeddings', methods=['POST'])
def embeddings():
    data = request.json
    logger.info(f"Received request: {json.dumps(data, indent=2)}")
    
    input_data: str = data.get('input')
    file_name: str = data.get('filename')
    logger.debug(f"Input data: type={type(input_data)}")
    logger.debug(f"Input data (truncated): {str(input_data)[:500]}...")
    
    embeddings = process_input(input_data, file_name)
    if embeddings is None:
        raise HTTPException("No Response")
    
    response = { "status": embeddings.status }
        
    return jsonify(response)

if __name__ == '__main__':
    app.run(debug=True, port=5000)
