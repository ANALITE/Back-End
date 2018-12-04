package eci.analite.data.model;

import java.util.HashSet;

public class User {

    private String username;
    private String password;
    private String email;
    private HashSet<String> search_queries;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.search_queries = new HashSet<>();
    }

    public HashSet<String> getSearch_queries() {
        return search_queries;
    }

    public void setSearch_queries(HashSet<String> search_queries) {
        this.search_queries = search_queries;
    }

    public User() {
    }

    public void addQuery(String query) {
        search_queries.add(query);
    }

    public void deleteQuery(String query) {
        search_queries.remove(query);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User[username=" + username + ", password=" + password + ", email=" + email + "]";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
