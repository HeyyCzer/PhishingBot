package me.heyyczer.phishing.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import me.heyyczer.phishing.utils.ExceptionManager;

public class DatabaseConnection {

    public static MongoClient openConnection() {
        MongoClient connection;
        try {
            connection = new MongoClient(new MongoClientURI(System.getenv().get("MONGO_URL")));
        }catch(Exception e) {
            ExceptionManager.reportError(e);
            return null;
        }
        return connection;
    }

}
