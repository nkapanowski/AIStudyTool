# app.py
from flask import Flask, jsonify, request
import firebase_admin
from firebase_admin import credentials, firestore, auth
import os

app = Flask(__name__)

# Initialize Firebase Admin
cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

@app.route("/api/ping")
def ping():
    return jsonify({"status": "ok"})

@app.route("/api/update-progress", methods=["POST"])
def update_progress():
    data = request.get_json()
    uid = data.get("uid")
    points = data.get("points")
    db.collection("users").document(uid).set({"progressPoints": points})
    return jsonify({"message": "Progress updated"})

if __name__ == "__main__":
    app.run(debug=True)
