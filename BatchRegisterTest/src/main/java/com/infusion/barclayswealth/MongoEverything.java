package com.infusion.barclayswealth;

import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

/**
 * Created by rchase on 11/15/2015.
 */
public class MongoEverything {

    private static Logger logger = LoggerFactory.getLogger(MongoEverything.class.getName());

    // env/config settings
    private static final String dbTrace = "true";
    private static final String debugMongo = "true";
    private static final String host = "localhost";
    private static final int port = 61017;
    private static final boolean failFast = true;

    // instance variables
    private static MongoClient mongoClient = null;

    static MongoClient getMongoClient() throws UnknownHostException {
        if (mongoClient == null) {
            configureMongoLogging();
            mongoClient = new MongoClient(host, port);

            if (failFast) {
                logger.info("Connecting to mongo server at {}:{}", host, port);
                mongoClient.listDatabases();
            }
        }
        return mongoClient;
    }

    private static void configureMongoLogging() {
        System.setProperty("DEBUG.MONGO", debugMongo);
        System.setProperty("DB.TRACE", dbTrace);
    }
}
