package com.example.mobilecyclingmanagement.repository;

import android.util.Log;

import androidx.annotation.NonNull;


import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
import com.example.mobilecyclingmanagement.callback.FirestoreRepository;
import com.example.mobilecyclingmanagement.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreRepositoryImpl<T> implements FirestoreRepository<T> {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String collectionName;
    private final Class<T> modelClass;

    public FirestoreRepositoryImpl(String collectionName, Class<T> modelClass) {
        this.collectionName = collectionName;
        this.modelClass = modelClass;
    }

    @Override
    public void create(T item, final FirestoreCallback callback) {
        db.collection(collectionName).add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                callback.onSuccess(documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error adding document", e);
                callback.onFailure(e);
            }
        });
    }
    @Override
    public void readAll(final FirestoreCallback callback) {
        db.collection(collectionName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<T> itemList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        T item = document.toObject(modelClass);

                        if (item instanceof Post) {
                            Post post = (Post) item;
                            post.setPostID(document.getId());
                        }
                        itemList.add(item);
                    }
                    callback.onSuccess(itemList);
                } else {
                    Log.w("Firestore", "Error getting documents.", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });
    }
    @Override
    public void update(String id, T item, final FirestoreCallback callback) {
        db.collection(collectionName).document(id).set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore", "DocumentSnapshot successfully updated!");
                callback.onSuccess(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error updating document", e);
                callback.onFailure(e);
            }
        });
    }

    @Override
    public void delete(String id, final FirestoreCallback callback) {
        Log.d("delete id", id);
        db.collection(collectionName).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                callback.onSuccess(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error deleting document", e);
                callback.onFailure(e);
            }
        });
    }

    @Override
    public void readById(String id, final FirestoreCallback callback) {
        Log.d("read id", id);
        db.collection(collectionName).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        T item = document.toObject(modelClass);


                        callback.onSuccess(item);
                    } else {
                        Log.w("Firestore", "No such document");
                        callback.onFailure(new Exception("Document does not exist"));
                    }
                } else {
                    Log.w("Firestore", "Error getting document.", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    @Override
    public void readByField(String field, String value, FirestoreCallback callback) {
        db.collection(collectionName).whereEqualTo(field, value).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0); // Get the first document
                        T item = document.toObject(modelClass);


                        callback.onSuccess(item); // Return the single item
                    } else {
                        Log.w("Firestore", "No such document");
                        callback.onFailure(new Exception("Document does not exist"));
                    }
                } else {
                    Log.w("Firestore", "Error getting document.", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });
    }


    public void updateField(String collection, String documentId, String fieldName, Object value, FirestoreCallback callback) {
        FirebaseFirestore.getInstance()
                .collection(collection)
                .document(documentId)
                .update(fieldName, value)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }






}