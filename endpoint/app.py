import flask
from flask import request, jsonify
import requests
import logging
import sys
from werkzeug.exceptions import HTTPException
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

@app.errorhandler(Exception)
def handle_exception(e):
    if isinstance(e, HTTPException):
        return e
    logger.error(f"An unhandled exception occurred: {str(e)}", exc_info=True)
    return jsonify(error="An unexpected error occurred"), 500

def process_input(input_item:str):   
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
            return embedding
        else:
            logger.error("No embedding found in Ollama API response")
            return None
    else:
        logger.error(f"Ollama API request failed with status code {response.status_code}")
        return None


@app.route('/embeddings', methods=['POST'])
def embeddings():
    data = request.json
    logger.info(f"Received request: {json.dumps(data, indent=2)}")
    
    input_data: str = data.get('input')
    logger.debug(f"Input data: type={type(input_data)}")
    logger.debug(f"Input data (truncated): {str(input_data)[:500]}...")
    
    embeddings = process_input(input_data)
    
    response = {
        "data": [
            {
                "embedding": emb,
                "index": i
            } for i, emb in enumerate(embeddings)
        ]
    }

    if response is None:
        raise HTTPException("No Response")
    
    logger.info(f"Successfully processed request and generated response with {len(embeddings)} embeddings")
    logger.debug(f"Response structure: {json.dumps(response, indent=2, default=str)}")
    logger.debug(f"First embedding in response (first few values): {embeddings[0][:5]}...")
    
    return jsonify(response)

if __name__ == '__main__':
    app.run(debug=True, port=5000)
