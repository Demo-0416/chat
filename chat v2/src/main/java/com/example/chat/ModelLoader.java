package com.example.chat;

import com.hankcs.hanlp.mining.word2vec.WordVectorModel;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ModelLoader {
    // Create a single thread executor to run the model loading task
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    // Create a future object to store the result of the model loading task
    private static Future<WordVectorModel> modelFuture;

    // Create a method to load the model asynchronously
    public static void loadModel() {
        modelFuture = executorService.submit(() -> {
            System.out.println("Asynchronously loading model...");
            try {
                // Load the model from the specified path
                return new WordVectorModel("/sgns.baidubaike.bigram-char");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
        System.out.println("Model loading initiated.");
    }

    // Create a method to get the model once it is loaded
    public static WordVectorModel getModel() throws ExecutionException, InterruptedException {
        if (modelFuture == null) {
            throw new IllegalStateException("Model not initiated for loading.");
        }
        return modelFuture.get(); // This will block until the model is loaded
    }

    // Create a method to shutdown the executor service
    public static void shutdown() {
        executorService.shutdown();
    }
}