package com.example.mobilecyclingmanagement.service;


import com.example.mobilecyclingmanagement.callback.FirestoreRepository;
import com.example.mobilecyclingmanagement.callback.FirestoreCallback;
public class GenericService<T> {
    private final FirestoreRepository<T> repository;

    public GenericService(FirestoreRepository<T> repository) {
        this.repository = repository;
    }

    public void createItem(T item, final FirestoreCallback callback) {
        repository.create(item, callback);
    }

    public void getItems(final FirestoreCallback callback) {
        repository.readAll(callback);
    }

    public void updateItem(String id, T item, final FirestoreCallback callback) {
        repository.update(id, item, callback);
    }

    public void deleteItem(String id, final FirestoreCallback callback) {
        repository.delete(id, callback);
    }

    public void readItem(String id, final  FirestoreCallback callback){
        repository.readById(id, callback);
    }

    public void readItemByField(String field, String value, final FirestoreCallback callback){
        repository.readByField(field, value, callback);
    }


}
