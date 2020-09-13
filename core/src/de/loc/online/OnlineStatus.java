package de.loc.online;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class OnlineStatus {

    public String cookie = "";

    public JsonValue packageList;
    public JsonValue userPackageList;

    public String packageTitle;
    public String packageFolder;
    public String packageId;
    public String userId;

    //package local methoden, sollten nur vom onlinehelper verändert werden
    boolean packageListPulled = false;
    boolean userPackageListPulled = false;
    boolean loggedIn = false;
    boolean downloaded = false;
    boolean uploaded = false;
    boolean updated = false;
    boolean deleted = false;

    boolean failed = false;
    boolean cancelled = false;

    public Array<String> getPackageIds() {
        Array<String> idList = new Array<String>();
        for ( JsonValue entry = this.packageList; entry != null; entry = entry.next ) {
            idList.add(entry.getString("_id"));
        }
        return idList;
    }
}
