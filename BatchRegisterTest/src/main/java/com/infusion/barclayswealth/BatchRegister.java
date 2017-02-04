package com.infusion.barclayswealth;


import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * Created by rchase on 11/13/2015.
 */
public class BatchRegister {

    Logger logger = LoggerFactory.getLogger(BatchRegister.class.getName());

    // num constants
    private final static int MAX_COLLECTION_CHECKS = 1000;
    private final static int NUMBER_OF_ACCOUNTS = 1000;

    // names of mongo things
    private final String databaseName = "batchtest";
    private final static String BATCHLOAD_COLL_NAME = "batchloads";
    private final static String ACCOUNT_ENTITY_NAME = "accounts";
    private final static String ACPERF_ENTITY_NAME = "accountPerformance";

    // time/date related variables for java 7
    //    private final static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";
    //    private static final TimeZone utc = TimeZone.getTimeZone("UTC");
    //    private static final SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT);
    //
    //    static {
    //        isoFormatter.setTimeZone(utc);
    //    }

    public void runTest() throws UnknownHostException {
        logger.debug("method entry {}", "runTest");
        MongoClient client = MongoEverything.getMongoClient();
        Date start = new Date();
        MongoDatabase db = client.getDatabase(databaseName);

        MongoCollection<Document> batchLoadCollection = db.getCollection(BATCHLOAD_COLL_NAME);

        // insert "batch load started" document into batch loads collection for accounts
        Document accountBatchLoad = new Document()
                .append("entityType", ACCOUNT_ENTITY_NAME)
                .append("loadFinished", false);
        String accountCollName = getUniqueCollectionName(db, ACCOUNT_ENTITY_NAME, start);
        logger.info("Unique account collection name {}",accountCollName);
        accountBatchLoad = accountBatchLoad.append("collectionName", accountCollName);
        batchLoadCollection.insertOne(accountBatchLoad);

        // populate accounts collection
        List<InsertOneModel<Document>> accountsList = new ArrayList<InsertOneModel<Document>>(NUMBER_OF_ACCOUNTS);
        for (int i = 0; i < NUMBER_OF_ACCOUNTS; i++) {
            accountsList.add(new InsertOneModel<>(createAccountDocument("TODO generate guid")));
        }
        MongoCollection<Document> accountColl = db.getCollection(accountCollName);
        accountColl.bulkWrite(accountsList, new BulkWriteOptions().ordered(false));

        // insert "batch load started" document into batch loads collection for accountPerformance
        Document acPerfBatchLoad = new Document()
                .append("entityType", ACPERF_ENTITY_NAME)
                .append("loadFinished", false);
        String acPerfCollName = getUniqueCollectionName(db, ACPERF_ENTITY_NAME, start);
        logger.info("Unique account performance collection name {}",acPerfCollName);
        acPerfBatchLoad = acPerfBatchLoad.append("collectionName", acPerfCollName);
        batchLoadCollection.insertOne(acPerfBatchLoad);

        // populate accountPerformance collection
        List<InsertOneModel<Document>> acPerfList = new ArrayList<InsertOneModel<Document>>(NUMBER_OF_ACCOUNTS);
        for (int i = 0; i < NUMBER_OF_ACCOUNTS; i++) {
            acPerfList.add(new InsertOneModel<>(createAccountPerfDocument("TODO generate guid")));
        }
        MongoCollection<Document> acPerfColl = db.getCollection(acPerfCollName);
        acPerfColl.bulkWrite(acPerfList, new BulkWriteOptions().ordered(false));

        // update batchload docs for both accounts and accountPerformance
        // FindIterable<Document> list = batchLoadCollection.find(new Document("$or", Arrays.asList(new Document("entityType", "accounts"),new Document("entityType", "accountPerformance")))
        //        .append("loadFinished",false)).sort(new Document("finishedTimestamp",-1)).limit(1);
        FindIterable<Document> list1 = batchLoadCollection.find(new Document("entityType", ACCOUNT_ENTITY_NAME)
                .append("loadFinished",false))
                .sort(new Document("finishedTimestamp",-1))
                .limit(1);
        Document accountFindDoc = list1.first();

        FindIterable<Document> list2 = batchLoadCollection.find(new Document("entityType", ACPERF_ENTITY_NAME)
                .append("loadFinished",false))
                .sort(new Document("finishedTimestamp",-1))
                .limit(1);
        Document acPerfFindDoc = list2.first();

        Date finish = new Date();

        List<WriteModel<Document>> updates = new ArrayList<WriteModel<Document>>();
        updates.add(
                new UpdateOneModel<>(
                        accountFindDoc,                   // find
                        new Document("$set",new Document("collectionName", accountCollName).append("loadFinished", true).append("finishedTimestamp", finish)) // update
                )
        );
        updates.add(
                new UpdateOneModel<>(
                        acPerfFindDoc,                   // find
                        new Document("$set",new Document("collectionName", acPerfCollName).append("loadFinished", true).append("finishedTimestamp", finish)) // updat
                )
        );


        batchLoadCollection.bulkWrite(updates);

        // remove old collections based on retention policy
        dataCleanup(db, ACCOUNT_ENTITY_NAME, accountCollName);
        dataCleanup(db, ACPERF_ENTITY_NAME, acPerfCollName);
        logger.debug("Method exit {}","runTest");

        getLatestCollectionNames();
    }

    private void getLatestCollectionNames() throws UnknownHostException {
        MongoDatabase db = MongoEverything.getMongoClient().getDatabase(databaseName);
        MongoCollection<Document> batchLoadCollection = db.getCollection(BATCHLOAD_COLL_NAME);

        FindIterable<Document> list1 = batchLoadCollection.find(new Document("entityType", ACCOUNT_ENTITY_NAME)
                .append("loadFinished",true))
                .sort(new Document("finishedTimestamp",-1))
                .limit(1);
        Document accountFindDoc = list1.first();

        FindIterable<Document> list2 = batchLoadCollection.find(new Document("entityType", ACPERF_ENTITY_NAME)
                .append("loadFinished",true))
                .sort(new Document("finishedTimestamp",-1))
                .limit(1);
        Document acPerfFindDoc = list2.first();

        logger.info("Newest/current account collection name: {}",accountFindDoc.get("collectionName"));
        logger.info("Newest/current accountPerformance collection name: {}",acPerfFindDoc.get("collectionName"));

    }

    private void dataCleanup(MongoDatabase db,String stem,String name) {
        logger.debug("method entry {}, db {}, stem {}, name {}", "dataCleanup", db.getName(), stem, name);

        DataCleanup.getInstance().doCleanup(db,stem,BATCHLOAD_COLL_NAME);
        logger.debug("method exit {}", "dataCleanup");
    }

    private String getUniqueCollectionName(MongoDatabase db, String stem, Date now) {
        logger.debug("method entry {}, db {}, stem {}", "getUniqueCollectionName", db.getName(), stem);
        String dateString = getDateString(now);

        int tries = 0;
        String name;
        String suffix = "_" + dateString;
        boolean unique = false;
        do {
            name = stem + suffix;
            unique = !collectionExists(db, name);
            logger.debug("Collection name {}, unique {}", name, unique);
            tries = ++tries;
            // the logic below is an alternative approach which uses an appended suffix of _1, _2, etc until a unique
            // name is found.  this is not needed if we use a complete timestamp that includes seconds.
            // TODO zero pad the tries string so that collection names are sortable
            //            suffix = "_" + dateString + "_" + Integer.toString(tries);
        } while (!unique && (tries < MAX_COLLECTION_CHECKS));

        if (!unique) {
            fail("Failed to find a unique collection name for accounts collection.");
        }
        logger.debug("method exit {}", "getUniqueCollectionName");
        return name;
    }

    private void fail(String reason) {
        logger.error("PROGRAM FAILURE, reason: {}", reason);
        System.exit(-1);
    }


    private Document createAccountDocument(String guid) {
        Document accountDoc = null;
        accountDoc = new Document("guid", guid)
                .append("sourceSystem", "AVQ")
                .append("branchCode", "BC")
                .append("baseCurrency", "GBP")
                .append("openDate", new Date())
                .append("accountName", "AccountName for " + guid)
                .append("accountType", "Account Type Placeholder")
                .append("serviceLevelId", Math.random())
                .append("serviceLevelDesc", "Unknown")
                .append("ipId", Math.random())
                .append("ipDescription", "Unknown");

        return accountDoc;
    }

    private Document createAccountPerfDocument(String guid) {
        Document accountPerfDoc = null;

        accountPerfDoc = new Document("guid", guid)
                .append("shardKey", guid)
                .append("branchCode", "BC")
                .append("baseCurrency", "GBP")
                .append("openDate", new Date())
                .append("accountName", "AccountName for " + guid)
                .append("accountType", "Account Type Placeholder")
                .append("serviceLevelId", Math.random())
                .append("serviceLevelDesc", "Unknown")
                .append("ipId", Math.random())
                .append("ipDescription", "Unknown");

        // create array for dimensionType
        int howMany = getRandomInt(5, 10);
        ArrayList<Document> list = new ArrayList<>();

        for (int i = 0; i < howMany; i++) {
            list.add(createDimensionType(getRandomInt(1, 9)));
        }

        accountPerfDoc = accountPerfDoc.append("dimensionType", list);

        return accountPerfDoc;
    }

    private Document createDimensionType(int seed) {
        Document dimTypeDoc = null;
        dimTypeDoc = new Document()
                .append("effectiveDate", new Date())
                .append("dimensionCurrencyCode", Integer.toString(seed))
                .append("dimensionAssetClassDescription", "assetClassDescribe")
                .append("currencyCode", "GBP")
                .append("netCashFlow", getRandomInt(1, 1000))
                .append("grossCashFlow", getRandomInt(1000, 100000));
        return dimTypeDoc;
    }

    private int getRandomInt(int low, int high) {
        Random ran = new Random();
        return low + ran.nextInt(high - low + 1);
    }

    private String getDateString(Date date) {
        // java 7
        // return isoFormatter.format(date).toString();

        // java 8
        return Instant.now().toString();
    }

    /**
     * collectionExists is deprecated in 3.0.x driver so lets do this ourselves.
     * this is similar to the implementation in the old DB class
     *
     * @param db
     * @return
     */
    private boolean collectionExists(MongoDatabase db, final String nameToCheck) {
        MongoIterable<String> collectionNames = db.listCollectionNames();
        logger.debug("collectionNames: {}",collectionNames);
        for (String collName : collectionNames) {
            logger.debug("collName: {}",collName);
            if (nameToCheck.equalsIgnoreCase(collName)) {
                return true;
            }
        }
        return false;
    }
}
