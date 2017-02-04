package com.infusion.barclayswealth;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rchase on 11/13/2015.
 */
public class Main
{
    private final static Logger logger = LoggerFactory.getLogger(Main.class.getName());
    public static void main( String[] args )
    {
        logger.info("Method entry {},{}", "main", args);

        BatchRegister batchRegister = new BatchRegister();
        try {
            batchRegister.runTest();
        } catch (UnknownHostException e) {
            logger.error("Exception: {e}",e);
            e.printStackTrace();
        }
        logger.info("Method exit {},{}", "main", args);
    }
}
