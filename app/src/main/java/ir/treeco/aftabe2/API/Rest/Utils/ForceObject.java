package ir.treeco.aftabe2.API.Rest.Utils;

import com.google.gson.annotations.Expose;

import ir.treeco.aftabe2.Util.Logger;

/**
 * Created by al on 5/14/16.
 */
public class ForceObject {

    @Expose
    boolean forceUpdate;

    @Expose
    boolean forceDownload;

    @Expose
    int version;

    @Expose
    URLHolder file;

    @Expose
    boolean active;


    public boolean isActive() {
        return active;
    }

    public int getVersionId() {
        return version;
    }

    public String getUrl() {
        return Logger.getUrl()+ "api/files/version/download/" + file.name;
    }

    public int getSize() {
        return file.size;
    }

    public String getName() {
        return file.name;
    }

    public boolean isForceDownload() {
        return forceDownload;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public class URLHolder {

        @Expose
        String name;

        @Expose
        int size;
    }

}
