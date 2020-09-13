package de.loc.online;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpRequestHeader;
import com.badlogic.gdx.net.HttpResponseHeader;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Map;

import de.loc.input.userinterface.EditorMenuScreen;
import de.loc.tools.Constants;
import de.loc.tools.ZipHelper;

import static com.badlogic.gdx.net.HttpStatus.SC_OK;

public final class OnlineHelper {
    //richtige constants
    private static final String URL_SERVER = "http://zuse1.efi.fh-nuernberg.de:8007/";
    private static final String URL_LOGIN = "loc/auth/logIn.json";
    private static final String URL_PACKAGES = "loc/api/packages.json";
    private static final String URL_USER_PACKAGES = URL_PACKAGES + "/user/";
    private static final String FORM_EMAIL = "email";
    private static final String FORM_PASSWORD = "password";
    private static final String FORM_TITLE = "title";
    private static final String FORM_PACKAGE = "package";
    private static final String BOUNDARY = "----LandsOfCinderBoundary35kaFkg6gp1jL";
    private static final String ZIP_UPLOAD = "uploadTmp.zip";
    private static final String ZIP_DOWNLOAD = "downloadTmp.zip";

    private OnlineHelper() {
    }

    // ---- BLOCKING FUNCTIONS ----

    public static boolean waitForHttpPostLogin(OnlineStatus onlineStatus) {
        sendHttpPostLogin(onlineStatus);

        while ( !onlineStatus.loggedIn && !onlineStatus.failed && !onlineStatus.cancelled ) {
            Gdx.app.log("ONLINE", "Logging in");
        }

        boolean result = onlineStatus.loggedIn && !onlineStatus.failed && !onlineStatus.cancelled;

        onlineStatus.loggedIn = false;
        onlineStatus.failed = false;
        onlineStatus.cancelled = false;

        return result;
    }

    public static boolean waitForHttpGetPackages(OnlineStatus onlineStatus) {
        sendHttpGetPackages(onlineStatus);

        while ( !onlineStatus.packageListPulled && !onlineStatus.failed && !onlineStatus.cancelled ) {
            Gdx.app.log("ONLINE", "Pulling package list");
        }

        boolean result = onlineStatus.packageListPulled && !onlineStatus.failed && !onlineStatus.cancelled;

        onlineStatus.packageListPulled = false;
        onlineStatus.failed = false;
        onlineStatus.cancelled = false;

        return result;
    }

    public static boolean waitForHttpPostPackages(OnlineStatus onlineStatus) {
        sendHttpPostPackage(onlineStatus);

        while ( !onlineStatus.uploaded && !onlineStatus.failed && !onlineStatus.cancelled ) {
            Gdx.app.log("ONLINE", "Uploading package");
        }

        boolean result = onlineStatus.uploaded && !onlineStatus.failed && !onlineStatus.cancelled;

        onlineStatus.uploaded = false;
        onlineStatus.failed = false;
        onlineStatus.cancelled = false;

        return result;
    }

    public static boolean waitForHttpGetPackage(OnlineStatus onlineStatus) {
        sendHttpGetPackage(onlineStatus);

        while ( !onlineStatus.downloaded && !onlineStatus.failed && !onlineStatus.cancelled ) {
            Gdx.app.log("ONLINE", "Downloading package");
        }

        boolean result = onlineStatus.downloaded && !onlineStatus.failed && !onlineStatus.cancelled;

        onlineStatus.downloaded = false;
        onlineStatus.failed = false;
        onlineStatus.cancelled = false;

        return result;
    }

    public static boolean waitForHttpDeletePackage(OnlineStatus onlineStatus) {
        sendHttpDeletePackage(onlineStatus);

        while ( !onlineStatus.deleted && !onlineStatus.failed && !onlineStatus.cancelled ) {
            Gdx.app.log("ONLINE", "Deleting package");
        }

        boolean result = onlineStatus.deleted && !onlineStatus.failed && !onlineStatus.cancelled;

        onlineStatus.deleted = false;
        onlineStatus.failed = false;
        onlineStatus.cancelled = false;

        return result;
    }

    public static boolean waitForHttpPutPackage(OnlineStatus onlineStatus) {
        sendHttpPutPackage(onlineStatus);

        while ( !onlineStatus.updated && !onlineStatus.failed && !onlineStatus.cancelled ) {
            Gdx.app.log("ONLINE", "Updating package");
        }

        boolean result = onlineStatus.updated && !onlineStatus.failed && !onlineStatus.cancelled;

        onlineStatus.updated = false;
        onlineStatus.failed = false;
        onlineStatus.cancelled = false;

        return result;
    }

    public static boolean waitForHttpGetUserPackages(OnlineStatus onlineStatus) {
        sendHttpGetUserPackages(onlineStatus);

        while ( !onlineStatus.userPackageListPulled && !onlineStatus.failed && !onlineStatus.cancelled ) {
            Gdx.app.log("ONLINE", "Pulling user package list");
        }

        boolean result = onlineStatus.userPackageListPulled && !onlineStatus.failed && !onlineStatus.cancelled;

        onlineStatus.userPackageListPulled = false;
        onlineStatus.failed = false;
        onlineStatus.cancelled = false;

        return result;
    }

    // ---- ASYNCHRONOUS FUNCTIONS ----
    public static void sendHttpPostLogin(final OnlineStatus status) {
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
        httpPost.setUrl(URL_SERVER + URL_LOGIN);
        httpPost.setHeader(HttpRequestHeader.ContentType, "application/x-www-form-urlencoded");

        Map<String, String> parameters = new HashMap<>();

        parameters.put(FORM_EMAIL, EditorMenuScreen.getLoginUsername());
        parameters.put(FORM_PASSWORD, EditorMenuScreen.getLoginPassword());
        httpPost.setContent(HttpParametersUtils.convertHttpParameters(parameters));

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if ( httpResponse.getStatus().getStatusCode() == SC_OK ) {
                    JsonValue response = new JsonReader().parse(httpResponse.getResultAsString());
                    if ( response.getBoolean("success") ) {
                        String[] strings = httpResponse.getHeader(HttpResponseHeader.SetCookie).split("\\;");

                        // save cookie on local file system
                        status.cookie = strings[0];
                        status.userId = response.getString("message");
                        status.loggedIn = true;
                        Gdx.app.log("ONLINE", "Login successful! " + response.getString("message"));
                    } else {
                        status.failed = true;
                        Gdx.app.log("ONLINE", "Login failed serverside! " + response.getString("message"));
                    }
                } else {
                    status.failed = true;
                    Gdx.app.log("ONLINE", "Login failed serverside! " + httpResponse.getStatus().getStatusCode());
                }
            }

            @Override
            public void failed(Throwable t) {
                status.failed = true;
                Gdx.app.log("ONLINE", "Login failed clientside! " + t.getMessage());
            }

            @Override
            public void cancelled() {
                status.cancelled = true;
                Gdx.app.log("ONLINE", "Login cancelled clientside!");
            }
        });
    }

    public static void sendHttpGetPackages(final OnlineStatus status) {
        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(URL_SERVER + URL_PACKAGES);
        //Authorization
        httpGet.setHeader(HttpRequestHeader.Cookie, status.cookie);

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if ( httpResponse.getStatus().getStatusCode() == SC_OK ) {
                    JsonValue response = new JsonReader().parse(httpResponse.getResultAsString());
                    if ( response.getBoolean("success") ) {
                        status.packageList = response.getChild("message");
                        status.packageListPulled = true;
                        Gdx.app.log("ONLINE", "Getting packages successful! ");
                    } else {
                        status.failed = true;
                        Gdx.app.log("ONLINE", "Getting packages failed serverside! " + response.getString("message"));
                    }
                } else {
                    status.failed = true;
                    Gdx.app.log("ONLINE", "Getting packages failed serverside! " + httpResponse.getStatus().getStatusCode());
                }
            }

            @Override
            public void failed(Throwable t) {
                status.failed = true;
                Gdx.app.log("ONLINE", "Getting packages failed clientside! " + t.getMessage());
            }

            @Override
            public void cancelled() {
                status.cancelled = true;
                Gdx.app.log("ONLINE", "Getting packages cancelled clientside!");
            }
        });
    }

    private static String createHttpContent(String title, String filename) {

        //fuck also das statt nur \n \r\n gebraucht wird is ja mal voll der schrott, hab ewig nach dem fehler gesucht
        String content = "--" + BOUNDARY + "\r\n";
        content += "Content-Disposition: form-data; name=\"" + FORM_TITLE + "\"\r\n";
        content += "\r\n";
        content += title + "\r\n";
        content += "--" + BOUNDARY + "\r\n";
        content += "Content-Disposition: form-data; name=\"" + FORM_PACKAGE + "\"; filename=\"" + filename + "\"\r\n";
        content += "Content-Type: application/x-zip-compressed\r\n";
        content += "\r\n";

        return content;
    }

    public static void sendHttpPostPackage(final OnlineStatus status) {
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
        httpPost.setUrl(URL_SERVER + URL_PACKAGES);
        httpPost.setHeader(HttpRequestHeader.Cookie, status.cookie);
        httpPost.setHeader(HttpRequestHeader.ContentType, "multipart/form-data; boundary=" + BOUNDARY);

        ZipHelper.zip(Gdx.files.local(Constants.PACKAGE_FOLDER + status.packageFolder), Gdx.files.local(Constants.PACKAGE_FOLDER + ZIP_UPLOAD));

        FileHandle zippedPackage = Gdx.files.local(Constants.PACKAGE_FOLDER + ZIP_UPLOAD);

        String contentHeader = createHttpContent(status.packageTitle, zippedPackage.name());
        InputStream zipInStream = zippedPackage.read();
        String contentFooter = "\r\n" + "--" + BOUNDARY + "--\r\n";

        //super inputstream mit dem kompletten content
        final InputStream
            content =
            new SequenceInputStream(new SequenceInputStream(new ByteArrayInputStream(contentHeader.getBytes()), zipInStream),
                                    new ByteArrayInputStream(contentFooter.getBytes()));
        long length = contentHeader.getBytes().length + zippedPackage.length() + contentFooter.getBytes().length;

        httpPost.setContent(content, length);

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if ( httpResponse.getStatus().getStatusCode() == SC_OK ) {
                    JsonValue response = new JsonReader().parse(httpResponse.getResultAsString());
                    if ( response.getBoolean("success") ) {
                        status.uploaded = true;
                        Gdx.app.log("ONLINE", "Uploading package successful! " + response.getString("message"));
                    } else {
                        status.failed = true;
                        Gdx.app.log("ONLINE", "Uploading package failed serverside! " + response.getString("message"));
                    }
                } else {
                    status.failed = true;
                    Gdx.app.log("ONLINE", "Uploading package failed serverside! " + httpResponse.getStatus().getStatusCode());
                }

                try {
                    content.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable t) {
                status.failed = true;
                Gdx.app.log("ONLINE", "Uploading package failed clientside!" + t.getMessage());

                try {
                    content.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

            @Override
            public void cancelled() {
                status.cancelled = true;
                Gdx.app.log("ONLINE", "Uploading package cancelled clientside!");

                try {
                    content.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static boolean downloadFile(InputStream is, OutputStream os) {
        byte[] bytes = new byte[1024];
        int count;

        try {
            while ( (count = is.read(bytes, 0, bytes.length)) != -1 ) {
                os.write(bytes, 0, count);
            }
        } catch ( IOException e ) {
            e.printStackTrace();

            return false;
        } finally {
            try {
                is.close();
                os.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static void sendHttpGetPackage(final OnlineStatus status) {
        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(URL_SERVER + URL_PACKAGES + "/" + status.packageId);

        httpGet.setHeader(HttpRequestHeader.Cookie, status.cookie);

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if ( httpResponse.getStatus().getStatusCode() == SC_OK ) {
                    if ( downloadFile(httpResponse.getResultAsStream(), Gdx.files.local(Constants.ONLINE_FOLDER + ZIP_DOWNLOAD).write(false)) ) {
                        ZipHelper.unzip(Gdx.files.local(Constants.ONLINE_FOLDER + ZIP_DOWNLOAD), Gdx.files.local(Constants.ONLINE_FOLDER + status.packageId));
                        status.downloaded = true;
                        Gdx.app.log("ONLINE", "Downloading package successful!");
                    } else {
                        status.failed = true;
                        Gdx.app.log("ONLINE", "Downloading package failed!");
                    }
                } else {
                    status.failed = true;
                    Gdx.app.log("ONLINE", "Downloading package failed serverside! " + httpResponse.getStatus().getStatusCode());
                }
            }

            @Override
            public void failed(Throwable t) {
                status.failed = true;
                Gdx.app.log("ONLINE", "Downloading package failed clientside!" + t.getMessage());
            }

            @Override
            public void cancelled() {
                status.cancelled = true;
                Gdx.app.log("ONLINE", "Downloading package cancelled clientside!");
            }
        });
    }

    public static void sendHttpDeletePackage(final OnlineStatus status) {
        Net.HttpRequest httpDelete = new Net.HttpRequest(Net.HttpMethods.DELETE);
        httpDelete.setUrl(URL_SERVER + URL_PACKAGES + "/" + status.packageId);
        httpDelete.setHeader(HttpRequestHeader.Cookie, status.cookie);

        Gdx.net.sendHttpRequest(httpDelete, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if ( httpResponse.getStatus().getStatusCode() == SC_OK ) {
                    JsonValue response = new JsonReader().parse(httpResponse.getResultAsString());
                    if ( response.getBoolean("success") ) {
                        status.deleted = true;
                        Gdx.app.log("ONLINE", "Deleting package successful! " + response.getString("message"));
                    } else {
                        status.failed = true;
                        Gdx.app.log("ONLINE", "Deleting package failed serverside! " + response.getString("message"));
                    }
                } else {
                    status.failed = true;
                    Gdx.app.log("ONLINE", "Deleting package failed serverside! " + httpResponse.getStatus().getStatusCode());
                }
            }

            @Override
            public void failed(Throwable t) {
                status.failed = true;
                Gdx.app.log("ONLINE", "Deleting package failed clientside!" + t.getMessage());
            }

            @Override
            public void cancelled() {
                status.cancelled = true;
                Gdx.app.log("ONLINE", "Deleting package cancelled clientside!");
            }
        });
    }

    public static void sendHttpPutPackage(final OnlineStatus status) {
        Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.PUT);
        httpPost.setUrl(URL_SERVER + URL_PACKAGES + "/" + status.packageId);
        httpPost.setHeader(HttpRequestHeader.Cookie, status.cookie);
        httpPost.setHeader(HttpRequestHeader.ContentType, "multipart/form-data; boundary=" + BOUNDARY);

        ZipHelper.zip(Gdx.files.local(Constants.PACKAGE_FOLDER + status.packageFolder), Gdx.files.local(Constants.PACKAGE_FOLDER + ZIP_UPLOAD));

        FileHandle zippedPackage = Gdx.files.local(Constants.PACKAGE_FOLDER + ZIP_UPLOAD);
        final InputStream is = zippedPackage.read();
        httpPost.setContent(createHttpContent(status.packageTitle, zippedPackage.name()));
        httpPost.setContent(is, zippedPackage.length());

        Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if ( httpResponse.getStatus().getStatusCode() == SC_OK ) {
                    JsonValue response = new JsonReader().parse(httpResponse.getResultAsString());
                    if ( response.getBoolean("success") ) {
                        Gdx.app.log("ONLINE", "Updating package successful! " + response.getString("message"));
                        status.updated = true;
                    } else {
                        status.failed = true;
                        Gdx.app.log("ONLINE", "Updating package failed serverside! " + response.getString("message"));
                    }
                } else {
                    status.failed = true;
                    Gdx.app.log("ONLINE", "Updating package failed serverside! " + httpResponse.getStatus().getStatusCode());
                }

                try {
                    is.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable t) {
                status.failed = true;
                Gdx.app.log("ONLINE", "Updating package failed clientside!" + t.getMessage());
                try {
                    is.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

            @Override
            public void cancelled() {
                status.cancelled = true;
                Gdx.app.log("ONLINE", "Updating package cancelled clientside!");
                try {
                    is.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void sendHttpGetUserPackages(final OnlineStatus status) {
        Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(URL_SERVER + URL_USER_PACKAGES + status.userId);
        //Authorization
        httpGet.setHeader(HttpRequestHeader.Cookie, status.cookie);

        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                if ( httpResponse.getStatus().getStatusCode() == SC_OK ) {
                    JsonValue response = new JsonReader().parse(httpResponse.getResultAsString());
                    if ( response.getBoolean("success") ) {
                        status.userPackageList = response.getChild("message");
                        status.userPackageListPulled = true;
                        Gdx.app.log("ONLINE", "Getting your packages successful! ");
                    } else {
                        status.failed = true;
                        Gdx.app.log("ONLINE", "Getting your packages failed serverside! " + response.getString("message"));
                    }
                } else {
                    status.failed = true;
                    Gdx.app.log("ONLINE", "Getting your packages failed serverside! " + httpResponse.getStatus().getStatusCode());
                }
            }

            @Override
            public void failed(Throwable t) {
                status.failed = true;
                Gdx.app.log("ONLINE", "Getting your packages failed clientside! " + t.getMessage());
            }

            @Override
            public void cancelled() {
                status.cancelled = true;
                Gdx.app.log("ONLINE", "Getting your packages cancelled clientside!");
            }
        });
    }
}
