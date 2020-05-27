package com.thingsenz.soundy.musicplayer.db;

public class DBSchema {

    public static class RecentHistory {
        public static String TABLE_NAME = "RecentHistory";
        public static class Cols {
            public static final String ID = "id";
            public static final String TIME_PLAYED = "time_played";
        }
    }

}
