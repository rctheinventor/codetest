package com.infusion.barclayswealth;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.client.model.Projections.include;

/**
 * Created by rchase on 11/15/2015.
 */
public class DataCleanup {

    private final int RETENTION_DAYS_OF_HISTORY = 3;
    private static DataCleanup _instance = null;
    private Logger logger = LoggerFactory.getLogger(DataCleanup.class.getName());

    private DataCleanup() {}

    public static DataCleanup getInstance() {
        if (_instance == null) {
            _instance = new DataCleanup();
        }
        return _instance;
    }

    void doCleanup(MongoDatabase db, String stem, String batchLoadCollName) {
        logger.debug("method entry {}, db {}, stem {}", "doCleanup", db.getName(), stem);

        FindIterable<Document> batchLoadDocs = db.getCollection(batchLoadCollName).find(new Document("entityType",stem)
                .append("loadFinished", true))
                .projection(include("collectionName"))
                .sort(new Document("_id",-1))
                .limit(10);

        // TODO need to double check the logic here. feels like i might be 1 or 2 off on some of the limit checks.
        // and the whole thing feels a bit hacky ... it's fine for the prototype but for the actual implementation the
        // logic related to data cleanup needs to be properly analyzed, designed, and implemented  Ideally it should be
        // encapsulated in a well defined logic module that provides some ability to modify key parameters with config
        // changes rather than code changes.  Specifically:  RETENTION_DAYS_OF_HISTORY should be defined in a
        // properties file that can be modified to make changes with no application or server changes or restarts.
        // We should expect that other config parameters will probably be added so we need to keep that mind.  There
        // are standard patterns for this, Spring can handle it.
        int index = 0;
        for (Document batchLoadDoc: batchLoadDocs) {
            if (index >= (RETENTION_DAYS_OF_HISTORY)) {
                String collToDelete = batchLoadDoc.getString("collectionName");
                logger.info("collToDelete {}",collToDelete);
                MongoCollection collection = db.getCollection(collToDelete);
                if (collection != null) {
                    logger.info("Dropping a collection: {}",collToDelete);
                    db.getCollection(batchLoadCollName).deleteOne(batchLoadDoc);
                    collection.drop();
                }
            }
            index++;
        }
        logger.debug("method exit {}", "doCleanup");
    }

}
