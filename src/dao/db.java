package dao;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class db {
	public static void main(String[] args) throws Exception {
		MongoClient client = new MongoClient("localhost", 27017);
		
		DB db = client.getDB("test");
		for(String name : db.getCollectionNames()) {
			System.out.println("¼¯ºÏÃû×Ö" + name);
		}
		client.close();
	}
}
