package testopenie.testopenie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.lambda3.graphene.core.*;
import org.lambda3.graphene.core.relation_extraction.model.ExContent;

/**
 * inFile is the input file, read line by line posFile holds the last operated
 * input line
 * 
 */
public class App {
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		int numThreads = 200;

		ExecutorService executor = Executors.newFixedThreadPool(numThreads);

		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		LinkedBlockingQueue<String> outs = new LinkedBlockingQueue<String>();

		try (BufferedReader br = new BufferedReader(new FileReader(new File("./test.txt")))) {
			String line;
			while ((line = br.readLine()) != null) {
				queue.put(line.trim());
			}
			br.close();
		}

		List<Future<?>> futures = new ArrayList<Future<?>>();

		for (int i = 0; i < numThreads; i++) {
			futures.add(executor.submit(new GrapheneWorker(queue, outs)));
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					FileWriter queueWriter = new FileWriter("./test.txt");
					PrintWriter queuePrinter = new PrintWriter(queueWriter);
					synchronized (queue) {
						for (String s : queue) {
							queuePrinter.println(s);
						}
						queuePrinter.close();
					}

					synchronized (outs) {
						FileWriter outWriter = new FileWriter("./all_news_tgt.txt", true);
						PrintWriter outPrinter = new PrintWriter(outWriter);
						for (String s : outs) {
							outPrinter.println(s);
						}
						outPrinter.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		

		while (true) { // task is not finished
			boolean allDone = true;
			for (Future<?> future : futures) {
				allDone &= future.isDone(); // check if future is done
			}
			if (allDone)
				break;
			System.out.println("\n--------------\n\n\nSentences left: " + queue.size() + "\n------------\n");
			Thread.sleep(5000); // Once a minute, dump to text file
			// Overwrite test.txt with only remaining elements
			FileWriter queueWriter = new FileWriter("./test.txt");
			PrintWriter queuePrinter = new PrintWriter(queueWriter);
			synchronized (queue) {
				for (String s : queue) {
					queuePrinter.println(s);
				}
				queuePrinter.close();
			}
			// Save current extracts
			synchronized (outs) {
				FileWriter outWriter = new FileWriter("./all_news_tgt.txt", true);
				PrintWriter outPrinter = new PrintWriter(outWriter);
				for (String s : outs) {
					outPrinter.println(s);
					outPrinter.flush();
				}
				outPrinter.close();
				while (outs.size() > 0)
					outs.remove();
			}
		}

		executor.shutdown();

		synchronized (outs) {
			FileWriter outWriter = new FileWriter("./all_news_tgt.txt", true);
			PrintWriter outPrinter = new PrintWriter(outWriter);
			for (String s : outs) {
				outPrinter.println(s);
			}
			outPrinter.close();
		}

		System.out.println("Done");
	}
}
