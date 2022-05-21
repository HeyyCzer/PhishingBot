package me.heyyczer.phishing.database;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.heyyczer.phishing.utils.config.BotConfig;
import me.heyyczer.phishing.utils.ExceptionManager;
import org.bson.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseMethods extends DatabaseConnection {

    public static void registerKick(String discordID, String discordGuild, String sentUrl) {
        MongoClient connection = openConnection();
        try {
            MongoDatabase db = System.getenv().get("MONGO_DATABASE");
            MongoCollection<Document> collection = db.getCollection("phishing_kicks");

            Document document = new Document();
            document.put("discordID", discordID);
            document.put("guild", discordGuild);
            document.put("sentUrl", sentUrl);
            document.put("appliedAt", new Date());
            collection.insertOne(document);
        } catch (Exception e) {
            ExceptionManager.reportError(e);
        } finally {
            connection.close();
        }
    }

    public static Map<String, String> getLinks() {
        MongoClient connection = openConnection();
        Map<String, String> links = new HashMap<>();
        try {
            MongoDatabase db = System.getenv().get("MONGO_DATABASE");
            MongoCollection<Document> collection = db.getCollection("phishing_links");

            FindIterable<Document> i = collection.find();
            for (Document o : i)
                links.put((String) o.get("url"), (String) o.get("reason"));
        } catch (Exception e) {
            ExceptionManager.reportError(e);
        } finally {
            connection.close();
        }
        return links;
    }

    public static Object getConfig(String guildID, String configKey) {
        MongoClient connection = openConnection();
        try {
            MongoDatabase db = System.getenv().get("MONGO_DATABASE");
            MongoCollection<Document> collection = db.getCollection("phishing_config");
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("guild", guildID);
            FindIterable<Document> i = collection.find(searchQuery);
            if (i.iterator().hasNext())
                if(i.iterator().next().get(configKey) != null)
                    return i.iterator().next().get(configKey);
        } catch (Exception e) {
            ExceptionManager.reportError(e);
        } finally {
            connection.close();
        }
        return BotConfig.fromConfigKey(configKey).getDefaultValue();
    }

    public static Object setConfig(String guildID, String configKey, Object value) {
        MongoClient connection = openConnection();
        try {
            MongoDatabase db = System.getenv().get("MONGO_DATABASE");
            MongoCollection<Document> collection = db.getCollection("phishing_config");
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("guild", guildID);
            FindIterable<Document> i = collection.find(searchQuery);
            if (!i.iterator().hasNext()) {
                Document config = new Document();
                config.put("guild", guildID);
                config.put(configKey, value);
                collection.insertOne(config);
                return value;
            }

            searchQuery.clear();
            searchQuery.put("guild", guildID);

            Document config = i.iterator().next();
            config.put(configKey, value);
            collection.updateOne(i.iterator().next(), config);
        } catch (Exception e) {
            ExceptionManager.reportError(e);
        } finally {
            connection.close();
        }
        return value;
    }

    public static int getProtectedTimes(String guildID) {
        MongoClient connection = openConnection();
        try {
            MongoDatabase db = System.getenv().get("MONGO_DATABASE");
            MongoCollection<Document> collection = db.getCollection("phishing_kicks");
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("guild", guildID);
            FindIterable<Document> i = collection.find(searchQuery);
            int length = 0;
            for (Document ignored : i)
                length++;
            return length;
        } catch (Exception e) {
            ExceptionManager.reportError(e);
        } finally {
            connection.close();
        }
        return -1;
    }

}
