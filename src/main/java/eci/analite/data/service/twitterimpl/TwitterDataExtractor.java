/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eci.analite.data.service.twitterimpl;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 *
 * @author user
 */
public class TwitterDataExtractor {

    private static final Twitter TWITTER = TwitterFactory.getSingleton();
    
    private static final String KEY = "d64c3ee8e9d940dd8a7d3573a79d279c";
    private static final String HOST = "https://eastus.api.cognitive.microsoft.com";

    private static final String PATH_SENTIMENT = "/text/analytics/v2.0/sentiment";
    private static final String PATH_KEYPHRASES = "/text/analytics/v2.0/keyPhrases";


    public static void search_data(String search_query) {
        try {
            Query query = new Query(search_query);
            query.setCount(100);
            query.setSince("2016-12-07");
            query.setUntil("2018-12-07");
            QueryResult result = TWITTER.search(query);
            while (result.getTweets().size() > 0) {
                Long minId = Long.MAX_VALUE;
                for (Status s : result.getTweets()) {
                    get_result_data(s);
                    if (s.getId() < minId) {
                        minId = s.getId();
                    }
                }
                query.setMaxId(minId - 1);
                result = TWITTER.search(query);
            }
        } catch (TwitterException ex) {
            Logger.getLogger(TwitterDataExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void get_result_data(Status status) {
        if (!status.isRetweet()) {
            String user = status.getUser().getName();
            String user_img = status.getUser().get400x400ProfileImageURL();
            int low_bound = status.getSource().indexOf(">") + 1;
            int up_bound = status.getSource().indexOf("<", 3);
            String source = status.getSource().substring(low_bound, up_bound);
            String text = status.getText();
            Date date = status.getCreatedAt();
            
            String line = String.format("%s,%s,%s,%s,%s", user, user_img, source, text, date.toString());
        }
    }
}