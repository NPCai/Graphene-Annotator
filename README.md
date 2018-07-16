# REST Graphene

A Spark Java REST API for Graphene, suitable for a large number of concurrent requests. It provides a simple wrapper around Graphene Core:

```java
public class App {
	public static void main(String[] args) throws IOException {
		int maxThreads = 64;
		threadPool(maxThreads);
		post("/extract", (req, res) -> {
			return (new Graphene()).doRelationExtraction(req.body(), false, false).serializeToJSON();
		});
	}
}
```

## Why?

The [official graphene server](https://github.com/Lambda-3/Graphene) was failing when hit with too many requests. I couldn't understand their graphene-server codebase enough to fix the issue. 

## Install

From the project root run:

```
mvn clean install
mvn package
mvn exec:java
```

To ensure it works:

```
curl -X POST -d "Although the Treasury will announce details of the November refunding on Monday, the funding will be delayed if Congress and President Bush fail to increase the Treasury's borrowing capacity." "http://localhost:4567/extract"
```
