package net.tomatentum.musicbot.favourites;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.tomatentum.musicbot.TomatenMusic;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private File mainDirectory;

    {
        try {
            mainDirectory = new File(new File(TomatenMusic.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private File file = new File("favourites.db");
    private Connection connection = null;


    public Database() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened favourites database successfully");

        executeUpdate("CREATE TABLE IF NOT EXISTS favourites" +
                "(userid BIGINT PRIMARY KEY, trackURL varchar , title varchar, length BIGINT)");



    }

    public void addFavourite(long userId, DbTrack trackInfo) {
        ResultSet resultSet = executeQuery("SELECT * FROM favourites WHERE userid=" + userId);

        try {
            while (resultSet.next()) {
                if (resultSet.getString("trackURL").equals(trackInfo.getUrl())) {
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        executeUpdate(String.format("INSERT INTO favourites(userid, trackURL, title, length) VALUES(%s, '%s', '%s', %s)", userId, trackInfo.getUrl(), trackInfo.getTitle(), trackInfo.getLength()));
    }

    public void removeFavourite(long userId, String URL) {
        ResultSet resultSet = executeQuery("SELECT * FROM favourites WHERE userid=" + userId);

        try {
            while (resultSet.next()) {
                if (resultSet.getString("trackURL").equals(URL)) {
                    executeUpdate(String.format("DELETE FROM favourites WHERE userid=%s AND trackURL='%s'", userId, URL));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }




    public List<DbTrack> getFavourites(long userId) {
        ResultSet resultSet = executeQuery("SELECT * FROM favourites WHERE userid=" + userId);

        List<DbTrack> tracks = new ArrayList<>();
        try {
            while (resultSet.next()) {
                DbTrack track = new DbTrack(resultSet.getString("trackURL"), resultSet.getString("title"), resultSet.getLong("length"));
                tracks.add(track);
            }
        } catch (SQLException e) {
                e.printStackTrace();
            }
        return tracks;
    }

    private int executeUpdate(String sql) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    public ResultSet executeQuery(String code) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(code);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    private void close() {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class DbTrack {
        private final String url;
        private final String title;
        private final long length;


        public DbTrack(String url, String title, long length) {
            this.url = url;
            this.title = title;
            this.length = length;
        }

        public String getUrl() {
            return url;
        }

        public long getLength() {
            return length;
        }

        public String getTitle() {
            return title;
        }
    }
}


