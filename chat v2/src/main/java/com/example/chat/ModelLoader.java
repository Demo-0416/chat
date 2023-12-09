package com.example.chat;

import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ModelLoader {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static Future<WordVectorModel> modelFuture;

    public static void loadModel() {
        modelFuture = executorService.submit(() -> {
            System.out.println("Asynchronously loading model...");
            try {
                return new WordVectorModel("E:\\javaproject\\chat-main (2)\\chat-main\\sgns.baidubaike.bigram-char");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
        System.out.println("Model loading initiated.");
    }

    public static WordVectorModel getModel() throws ExecutionException, InterruptedException {
        if (modelFuture == null) {
            throw new IllegalStateException("Model not initiated for loading.");
        }
        return modelFuture.get(); // This will block until the model is loaded
    }
    public static void shutdown() {
        executorService.shutdown();
    }
}