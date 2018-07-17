package testopenie.testopenie;

import java.util.concurrent.LinkedBlockingQueue;

import org.lambda3.graphene.core.Graphene;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GrapheneWorker implements Runnable {
	
	Graphene graphene;
	LinkedBlockingQueue<String> queue;
	LinkedBlockingQueue<String> outQueue;
	
	public GrapheneWorker(LinkedBlockingQueue<String> queue, LinkedBlockingQueue<String> outQueue) {
		this.graphene = new Graphene();
		this.queue = queue;
		this.outQueue = outQueue;
	}
	
	@Override
	public void run() {
		while (!this.queue.isEmpty()) {
			String sentence = queue.remove();
			try {
				String json = this.graphene.doRelationExtraction(sentence, false, false).serializeToJSON();
				outQueue.add(sentence + "\t" + json);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

}
