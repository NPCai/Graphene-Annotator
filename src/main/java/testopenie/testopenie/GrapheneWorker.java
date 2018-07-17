package testopenie.testopenie;

import java.util.concurrent.LinkedBlockingQueue;

import org.lambda3.graphene.core.Graphene;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GrapheneWorker implements Runnable {
	
	Graphene graphene;
	LinkedBlockingQueue<String> queue;
	LinkedBlockingQueue<String> outQueue;
	LinkedBlockingQueue<String> tempOut;
	
	public GrapheneWorker(LinkedBlockingQueue<String> queue, LinkedBlockingQueue<String> outQueue) {
		this.graphene = new Graphene();
		this.queue = queue;
		this.outQueue = outQueue;
		this.tempOut = new LinkedBlockingQueue<String>();
	}
	
	@Override
	public void run() {
		while (!this.queue.isEmpty()) {
			String sentence = queue.remove();
			try {
				String json = this.graphene.doRelationExtraction(sentence, false, false).serializeToJSON();
				tempOut.add(sentence + "\t" + json);
				if (this.queue.isEmpty() || (tempOut.size() > 20)){
					tempOut.drainTo(outQueue);
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

}
