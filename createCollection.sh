#!/bin/sh
curl -X PUT "https://""$QDRANT_URL""/collections/""$QDRANT_COLLECTION" \
  --header "Content-Type: application/json" \
  --data '{"vectors": {"size": 384, "distance": "Cosine"}}'