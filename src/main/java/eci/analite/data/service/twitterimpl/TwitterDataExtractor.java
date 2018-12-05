/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eci.analite.data.service.twitterimpl;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
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

    private static ArrayList<Line> lines;

    public File search_data(String search_query) {
        lines = new ArrayList<>();
        get_tweets(search_query);
        sentiment_run();
        return get_sentiment_file(search_query);
    }

    private File get_sentiment_file(String file_name) {
        File file = new File(String.format("data/%s.csv", file_name));
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for (Line line : lines) {
                out.append(line.toString());
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(TwitterDataExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return file;
    }

    private void get_tweets(String search_query) {
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

    private void get_result_data(Status status) {
        if (!status.isRetweet()) {
            Line line = new Line();
            line.setUser(status.getUser().getName());
            line.setUser_img(status.getUser().get400x400ProfileImageURL());
            int low_bound = status.getSource().indexOf(">") + 1;
            int up_bound = status.getSource().indexOf("<", 3);
            line.setSource(status.getSource().substring(low_bound, up_bound));
            line.setText(status.getText());
            line.setDate(status.getCreatedAt());
            lines.add(line);
        }
    }

    private void json_map_sentiment(String response) {
        JSONObject json = new JSONObject(response);
        JSONArray sentiments = json.getJSONArray("documents");
        sentiments.forEach(result -> each_sentiment(result));
    }

    private void each_sentiment(Object result) {
        JSONObject object = (JSONObject) result;
        float score = Float.parseFloat(object.getJSONObject("score").toString());
        int index = Integer.parseInt(object.getJSONObject("id").toString());
        lines.get(index).setSentiment(score);
    }

    private void sentiment_run() {
        try {
            Documents documents = new Documents();
            for (int i = 0; i < lines.size(); ++i) {
                documents.add("" + i, "es", lines.get(i).getText());
            }
            json_map_sentiment(get_sentiment(documents));
        } catch (Exception ex) {
            Logger.getLogger(TwitterDataExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String get_sentiment(Documents documents) throws Exception {
        String text = new Gson().toJson(documents);
        byte[] encoded_text = text.getBytes("UTF-8");

        URL url = new URL(HOST + PATH_SENTIMENT);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/json");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", KEY);
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(encoded_text, 0, encoded_text.length);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }

    private String get_keyphrases(Documents documents) throws Exception {
        String text = new Gson().toJson(documents);
        byte[] encoded_text = text.getBytes("UTF-8");

        URL url = new URL(HOST + PATH_KEYPHRASES);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/json");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", KEY);
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(encoded_text, 0, encoded_text.length);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }

    private class Document {

        public String id, language, text;

        public Document(String id, String language, String text) {
            this.id = id;
            this.language = language;
            this.text = text;
        }
    }

    private class Documents {

        public List<Document> documents;

        public Documents() {
            this.documents = new ArrayList<>();
        }

        public void add(String id, String language, String text) {
            this.documents.add(new Document(id, language, text));
        }
    }

    private class Line {

        private String user_img;
        private String user;
        private String source;
        private String text;
        private Date date;
        private float sentiment = -1f;

        public float getSentiment() {
            return sentiment;
        }

        public void setSentiment(float sentiment) {
            this.sentiment = sentiment;
        }

        public String getUser_img() {
            return user_img;
        }

        public void setUser_img(String user_img) {
            this.user_img = user_img;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return String.format("%s,%s,%s,%s,%s,%.4f%n", user, user_img, source, text, date.toString(), sentiment);
        }
    }
}
