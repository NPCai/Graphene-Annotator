package testopenie.testopenie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.lambda3.graphene.core.*;
import org.lambda3.graphene.core.relation_extraction.model.ExContent;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		int numThreads = 3;

		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		LinkedBlockingQueue[] queues = new LinkedBlockingQueue[numThreads];

		for (int i = 0; i < numThreads; i++) {
			queues[i] = new LinkedBlockingQueue<String>();
		}

		LinkedBlockingQueue<String> outs = new LinkedBlockingQueue<String>();
		long count = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(new File("./test.txt")))) {
			String line;
			
			while ((line = br.readLine()) != null) {
				queues[(int) (++count % numThreads)].put(line.trim());
			}
		}

		for (int i = 0; i < numThreads; i++) {
			executor.submit(new GrapheneWorker(queues[i], outs));
		}

		while (outs.size() < count) { // task is not finished
			System.out.println("\n--------------\n\n\nSentences done: " + outs.size() + "\n------------\n");
			Thread.sleep(1000);
		}

		executor.shutdown();
		FileWriter writer = new FileWriter("./all_news_tgt.txt");
		PrintWriter printWriter = new PrintWriter(writer);
		for (String s : outs) {
			printWriter.println(s);
		}
		printWriter.close();
		System.out.println("Done");
	}
}
