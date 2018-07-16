package testopenie.testopenie;


import java.io.IOException;

import org.lambda3.graphene.core.*;
import org.lambda3.graphene.core.relation_extraction.model.ExContent;
import static spark.Spark.*;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException {
		int maxThreads = 64;
		threadPool(maxThreads);
		post("/extract", (req, res) -> {
			return (new Graphene()).doRelationExtraction(req.body(), false, false).serializeToJSON();
		});
	}
}
