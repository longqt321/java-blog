package org.example.javablog.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RecommenderService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Scheduled(cron = "0 0 8 * * *")
    public void runRecommender() {
        System.out.println("🔄 Scheduling recommender task...");

        executor.submit(() -> {
            try {
                String scriptPath = "D:\\study\\DUT\\PBL3_Blog\\script\\recommender.py";
                Process process = new ProcessBuilder("python", scriptPath).start();

                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    System.out.println("Recommender script finished successfully.");
                } else {
                    System.err.println("Recommender script finished with exit code: " + exitCode);
                }

            } catch (IOException e) {
                System.err.println("IO error while running recommender: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("❌ Recommender thread was interrupted.");
            }
        });
    }
}
