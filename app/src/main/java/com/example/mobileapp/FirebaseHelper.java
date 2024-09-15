package com.example.mobileapp;

import android.net.Uri;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class FirebaseHelper {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    public FirebaseHelper() {
        // Initialize Firestore and Storage
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // Method to add a document
    public void addDocument(String collectionPath, String documentId, Object data, OnCompleteListener<Void> listener) {
        firestore.collection(collectionPath).document(documentId)
                .set(data)
                .addOnCompleteListener(listener);
    }

    // Method to get a document
    public void getDocument(String collectionPath, String documentId, final OnSuccessListener<DocumentSnapshot> successListener, final OnFailureListener failureListener) {
        firestore.collection(collectionPath).document(documentId)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Method to delete a document
    public void deleteDocument(String collectionPath, String documentId, OnCompleteListener<Void> listener) {
        firestore.collection(collectionPath).document(documentId)
                .delete()
                .addOnCompleteListener(listener);
    }

    // Method to upload a file to Firebase Storage
    public void uploadFile(String storagePath, Uri fileUri, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        // Get a reference to the storage location
        StorageReference storageRef = storage.getReference().child(storagePath);

        // Upload the file to the storage reference
        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL after successful upload
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(successListener)
                            .addOnFailureListener(failureListener);
                })
                .addOnFailureListener(failureListener);
    }

    // Method to download a file from Firebase Storage
    public void downloadFile(String storagePath, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        // Get a reference to the storage location
        StorageReference storageRef = storage.getReference().child(storagePath);

        // Get the download URL for the file
        storageRef.getDownloadUrl()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // Method to delete a file from Firebase Storage
    public void deleteFile(String storagePath, OnCompleteListener<Void> listener) {
        // Get a reference to the storage location
        StorageReference storageRef = storage.getReference().child(storagePath);

        // Delete the file from Firebase Storage
        storageRef.delete()
                .addOnCompleteListener(listener);
    }

}