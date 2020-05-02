package com.thingsenz.soundy.listeners;

public interface OnDatabaseChangedListener {

    void onNewDatabaseEntryAdded();
    void onDatabaseEntryRenamed();

}
