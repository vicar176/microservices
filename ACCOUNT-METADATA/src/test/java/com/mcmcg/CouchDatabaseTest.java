package com.mcmcg;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

public class CouchDatabaseTest {
public static void main(String args[])
{
	
	//CouchbaseCluster cluster = CouchbaseCluster.create("127.0.0.1");
	

	//Bucket bucket = cluster.openBucket("degault");
	
CouchbaseCluster cluster = CouchbaseCluster.create();
//Bucket bucket = cluster.openBucket(name, password, timeout, timeUnit)

Bucket bucket = cluster.openBucket();






	//Bucket bucket = cluster.openBucket("test",20, TimeUnit.SECONDS);
	JsonObject content = JsonObject.create().put("hello", "world");
	JsonDocument inserted = bucket.upsert(JsonDocument.create("helloworld", content));
	
	 JsonDocument found = bucket.get("helloworld");
	 System.out.println("Couchbase is the best database in the " + found.content().getString("accountNumber"));
	 
	 
	 

	 // Close all buckets and disconnect
	// cluster.disconnect();

}
}
