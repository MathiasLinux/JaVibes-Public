package fr.kliem.javibes.api;

import java.io.*;

import fr.kliem.javibes.JaVibes;
import javafx.scene.image.Image;
import okhttp3.*;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.time.LocalTime;


/**
 * This class is used to connect to the Jellyfin API
 */
public class Jellyfin {
//    HttpClient client = HttpClient.newHttpClient();

    OkHttpClient client = new OkHttpClient();

    Cache cache;
    String appName = "JaVibes";
    String appVersion = "0.0.1";
    String deviceName = "JaVibes";
    String uniqueDeviceId = "JaVibes";

    String accessToken;
    String userId;

    String headerAuthorization;
    String siteUrl;

    String libraryNameSelected;

    String libraryIdSelected;

    // Contructor
    public Jellyfin() throws IOException, InterruptedException {
        // We create a cache for the OkHttpClient
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        // We get the cache directory
        String homeDir = System.getProperty("user.home");
        Path cacheDirectory = Paths.get(homeDir, ".javibes", "cache");
        // We create the cache
        cache = new Cache(new File(cacheDirectory.toString()), cacheSize);

        // if the connection has not been made, we connect to the Jellyfin API

        if (siteUrl == null) {
            connectToJellyfin();
        }
    }

    /**
     * A method to connect to the Jellyfin API
     * This method is called when the user wants to connect to the Jellyfin API
     * 
     * @return void
     */
    private void connectToJellyfin() {
        // First we try to get the parameters from the resources
        String url = Objects.requireNonNull(JaVibes.class.getResource("configs/services.properties")).getPath();

        Properties serviceProperties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(url);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            serviceProperties.load(isr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String jellyfinEnable = serviceProperties.getProperty("jellyfin");

        String jellyfin_login;
        String jellyfin_password;
        String jellyfin_url;
        String jellyfin_library;

        if (Objects.equals(jellyfinEnable, "true")) {
            // We get all the properties
            jellyfin_login = serviceProperties.getProperty("jellyfin_login").trim();
            jellyfin_password = serviceProperties.getProperty("jellyfin_password").trim();
            jellyfin_url = serviceProperties.getProperty("jellyfin_url").trim();
            jellyfin_library = serviceProperties.getProperty("jellyfin_library").trim();
        } else {
            throw new RuntimeException("Jellyfin is not enabled");
        }

        try {
            connect(jellyfin_login, jellyfin_password, jellyfin_url);
            getMusicView(jellyfin_library);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect to the Jellyfin API using the login and password of the user
     *
     * @param login    : the username of the user
     * @param password : the password of the user
     */
    public void connect(String login, String password, String url) throws IOException, InterruptedException {
        siteUrl = url;

        // create the authorization header
        String authorization = "MediaBrowser Client=\"" + appName + "\", Device=\"" + deviceName + "\", DeviceId=\"" + uniqueDeviceId + "\", Version=\"" + appVersion + "\"";

        // Initialize the HttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        // create a post request with the login and the password in a json body
        Request request = new Request.Builder()
                .url(url + "/Users/AuthenticateByName")
                .header("Content-Type", "application/json")
                .header("Authorization", authorization)
                .post(RequestBody.create("{\"Username\":\"" + login + "\",\"Pw\":\"" + password + "\"}", MediaType.parse("application/json")))
                .build();

        // send the request and get the response in an object
        Response response = client.newCall(request).execute();

        if (response.code() != 200) {
            throw new RuntimeException("Error while connecting to the Jellyfin API");
        }

        assert response.body() != null;
        String responseBody = response.body().string();

        // Convert the body to a json object
        JSONObject jsonObject = new JSONObject(responseBody);

        // First we get the AccessToken
        accessToken = jsonObject.get("AccessToken").toString();

        // We get the user id (Set in the User JsonObject)
        userId = jsonObject.getJSONObject("User").get("Id").toString();

        // We create the header for the next requests (ex MediaBrowser Client="other", Device="my-script", DeviceId="some-unique-id", Version="0.0.0", Token="47ef8efea67c4c8faf9c9dc9ed367dbe")
        headerAuthorization = "MediaBrowser Client=\"" + appName + "\", Device=\"" + deviceName + "\", DeviceId=\"" + uniqueDeviceId + "\", Version=\"" + appVersion + "\", Token=\"" + accessToken + "\"";
    }

    /**
     * A method to get the music view (the library of the user)
     * 
     * @param libraryName : the name of the library
     * @throws IOException
     * @throws InterruptedException
     */
    public void getMusicView(String libraryName) throws IOException, InterruptedException {
        libraryNameSelected = libraryName;

        // Initialize the HttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        // create a get request to get the music view
        Request request = new Request.Builder()
                .url(siteUrl + "/Users/" + userId + "/Views")
                .header("Content-Type", "application/json")
                .header("Authorization", headerAuthorization)
                .build();

        // send the request and get the response in an object
        Response response = client.newCall(request).execute();

        // We get the Musique view
        assert response.body() != null;
        JSONObject jsonObject = new JSONObject(response.body().string());
        // We search the music view
        for (int i = 0; i < jsonObject.getJSONArray("Items").length(); i++) {
            if (jsonObject.getJSONArray("Items").getJSONObject(i).get("Name").toString().equals(libraryName)) {
                libraryIdSelected = jsonObject.getJSONArray("Items").getJSONObject(i).get("Id").toString();
            }
        }
    }

    /**
     * A method to get all the music albums of the user
     * 
     * @return String : the response of the Jellyfin API
     * @throws IOException
     * @throws InterruptedException
     */
    public String getMusicAlbums() throws IOException, InterruptedException {
        // We create the OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();


        // create a get request to get the music view with caching
        Request request = new Request.Builder()
                .url(siteUrl + "/Users/" + userId + "/Items?SortBy=SortName&SortOrder=Ascending&IncludeItemTypes=MusicAlbum&Recursive=true&Fields=PrimaryImageAspectRatio%2CSortName%2CBasicSyncInfo&ImageTypeLimit=1&EnableImageTypes=Primary%2CBackdrop%2CBanner%2CThumb&StartIndex=0&ParentId=" + libraryIdSelected)
                .header("Content-Type", "application/json")
                .header("Authorization", headerAuthorization)
                .build();

        // send the request and get the response in an object
        Response response = client.newCall(request).execute();

//        System.out.println(response.body());
        assert response.body() != null;
        return response.body().string();
    }

    /**
     * A method to download the image of the album (only one image)
     *
     * @param albumImageUrl : the url of the image of the album
     * @param imgsDir       : the directory where the image will be saved
     * @param albumImageId  : the id of the album
     */
    public void downloadImage(String albumImageUrl, String imgsDir, String albumImageId) throws IOException, InterruptedException {
        // We create the OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        // create a get request to get the image of the album
        Request request = new Request.Builder()
                .url(siteUrl + albumImageUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", headerAuthorization)
                .build();

        // send the request and get the response in an object
        Response response = client.newCall(request).execute();

        // We get the image
        assert response.body() != null;
        byte[] bytes = response.body().bytes();

        // We create the directory if it doesn't exist
        File directory = new File(imgsDir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // We create the file
        File file = new File(imgsDir + "/" + albumImageId + ".jpg");
        file.createNewFile();

        // We write the bytes in the file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
    }

    /**
     * A method to get the tracks infos for a specific album from the Jellyfin API using the album id
     *
     * @param albumId String : the id of the album
     * @return String : The tracks infos in a JSON format
     */
    public String getTracks(String albumId) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        // create a get request to get the album infos
        Request request = new Request.Builder()
                .url(siteUrl + "/Users/" + userId + "/Items/?ParentId=" + albumId + "&Fields=ItemCounts%2CPrimaryImageAspectRatio%2CBasicSyncInfo%2CCanDelete%2CMediaSourceCount&SortBy=ParentIndexNumber%2CIndexNumber%2CSortName")
                .header("Content-Type", "application/json")
                .header("Authorization", headerAuthorization)
                .build();

        // send the request and get the response in an object
        Response response = client.newCall(request).execute();

        // We get the tracks infos
        JSONObject tracksOGJson = new JSONObject(response.body().string());

        // Create an empty JSONObject to store the tracks infos (so it's the same for all services)
        JSONObject tracksInfosJson = new JSONObject();

        // We get the tracks infos

        // We create an array to store the tracks
        JSONObject[] tracks = new JSONObject[tracksOGJson.getJSONArray("Items").length()];

        // We loop through the tracks and add them to the titleList

        for (int i = 0; i < tracksOGJson.getJSONArray("Items").length(); i++) {
            JSONObject track = tracksOGJson.getJSONArray("Items").getJSONObject(i);
            // We create the track nb
            JSONObject trackJson = new JSONObject();
            trackJson.put("TrackNb", i + 1);
            trackJson.put("Title", track.get("Name").toString());

            // Add the track id for the playback
            trackJson.put("Id", track.get("Id").toString());

            // We calculate the duration of the track
            long duration = Long.parseLong(track.get("RunTimeTicks").toString());
            // We use LocalTime
            LocalTime time = LocalTime.ofSecondOfDay(duration / 10000000);
            // We remove the hours if there is no hours
            String durationText = time.getHour() == 0 ? time.toString().substring(3) : time.toString();
            trackJson.put("Duration", durationText);

            tracks[i] = trackJson;
        }

        tracksInfosJson.put("Items", tracks);

        return tracksInfosJson.toString();
    }

    /**
     * A method to get the album infos for a specific album from the Jellyfin API using the album id
     * 
     * @param albumId String : the id of the album
     * @return String : The album infos in a JSON format
     * @throws IOException
     */
    public String getAlbumInfos(String albumId) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        // create a get request to get the album infos
        Request request = new Request.Builder()
                .url(siteUrl + "/Users/" + userId + "/Items/" + albumId)
                .header("Content-Type", "application/json")
                .header("Authorization", headerAuthorization)
                .build();

        // send the request and get the response in an object
        Response response = client.newCall(request).execute();

        // We get the album infos
        JSONObject albumOGJson = new JSONObject(response.body().string());

        // Create an empty JSONObject to store the album infos (so it's the same for all services)
        JSONObject albumInfosJson = new JSONObject();

        // We get the album infos

        albumInfosJson.put("Name", albumOGJson.get("Name").toString());
        albumInfosJson.put("AlbumArtist", albumOGJson.getJSONArray("AlbumArtists").getJSONObject(0).get("Name").toString());
        albumInfosJson.put("PremiereDate", albumOGJson.get("PremiereDate").toString());
        albumInfosJson.put("ChildCount", albumOGJson.get("ChildCount").toString());

        // First we get the ImageTags array
        JSONObject imageTags = albumOGJson.getJSONObject("ImageTags");

        // We get the primary image
        String primaryImage = imageTags.get("Primary").toString();

        // After we get the primary image
        albumInfosJson.put("Image", primaryImage);

        return albumInfosJson.toString();
    }

    /**
     * A method to get the track uri for a specific track from the Jellyfin API using the track id
     * 
     * @param trackId String : the id of the track
     * @return String : The track uri
     */
    public String getTrackUri(String trackId) {

        String containerNotEncode = "opus,webm|opus,mp3,aac,m4a|aac,m4a|alac,m4b|aac,flac,webma,webm|webma,wav,ogg";
        String container = URLEncoder.encode(containerNotEncode, StandardCharsets.UTF_8);
        // We create the url
        String url = this.siteUrl + "/Audio/" + trackId + "/main.m3u8?container=" + container + "&MaxStreamingBitrate=999999999&AudioCodec=aac&TranscodingContainer=ts&TranscodingProtocol=hls" + "&api_key=" + this.accessToken;

        return url;
    }

    /**
     * A method to get the next track uri from the current track uri
     * 
     * @param currentTrackUri String : the current track uri
     * @return String : The next track uri
     */
    public String getNextTrackUri(String currentTrackUri) {
        String nextTrackUri = "";

        // We cut the currentTrackUri to get the track id
        String currentTrackUriCut = currentTrackUri.split("/")[4];

        // First we get the album linked to the current track
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        // create a get request to get the album infos
        Request request = new Request.Builder()
                .url(siteUrl + "/Users/" + userId + "/Items/" + currentTrackUriCut)
                .header("Content-Type", "application/json")
                .header("Authorization", headerAuthorization)
                .build();

        // send the request and get the response in an object
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get the response body
        assert response != null;
        String responseBody = null;
        try {
            responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // From the body we get the parent id 'ParentId' and the 'IndexNumber'
        JSONObject trackOGJson = new JSONObject(responseBody);

        String parentId = trackOGJson.get("ParentId").toString();
        String indexNumber = trackOGJson.get("IndexNumber").toString();

        // If we have the two strings we can continue
        if (parentId != null && indexNumber != null) {
            // We verify that the IndexNumber is superior to 1
            // We transform the string indexNumber to an integer
            int indexNumberInt = Integer.parseInt(indexNumber);

            if (indexNumberInt >= 1) {
                // We all the tracks of the album
                // create a get request to all the tracks
                Request requestAlbum = new Request.Builder()
                        .url(siteUrl + "/Users/" + userId + "/Items/?ParentId=" + parentId + "&Fields=ItemCounts%2CPrimaryImageAspectRatio%2CBasicSyncInfo%2CCanDelete%2CMediaSourceCount&SortBy=ParentIndexNumber%2CIndexNumber%2CSortName")
                        .header("Content-Type", "application/json")
                        .header("Authorization", headerAuthorization)
                        .build();

                // send the request and get the response in an object
                Response responseAlbum = null;
                try {
                    responseAlbum = client.newCall(requestAlbum).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // get the response body
                assert responseAlbum != null;
                String responseBodyAlbum = null;
                try {
                    responseBodyAlbum = responseAlbum.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // We get the track with the index number +1, if it does not exist we get make +2 except if the result is superior to the number of tracks, we repeat the process until we get a track
                JSONObject albumOGJson = new JSONObject(responseBodyAlbum);

                String nextTrackId = null;
                int plusTrack = 0;

                // First we verify that the current track is not the last
                if (indexNumberInt == albumOGJson.getJSONArray("Items").length()) {
                    // Redirect to the first track of the album
                    nextTrackId = albumOGJson.getJSONArray("Items").getJSONObject(0).get("Id").toString();
                    nextTrackUri = this.siteUrl + "/Audio/" + nextTrackId + "/main.m3u8?container=" + URLEncoder.encode("opus,webm|opus,mp3,aac,m4a|aac,m4a|alac,m4b|aac,flac,webma,webm|webma,wav,ogg", StandardCharsets.UTF_8) + "&MaxStreamingBitrate=999999999&AudioCodec=aac&TranscodingContainer=ts&TranscodingProtocol=hls" + "&api_key=" + this.accessToken;

                    return nextTrackUri;
                }

                // We loop through the array contained in the Items JSONObject, until we find the track with the indexNumber +1
                for (int i = 0; i < albumOGJson.getJSONArray("Items").length(); i++) {
                    if (nextTrackId == null) {
                        plusTrack += 1;
                        // Loop through the tracks
                        for (int j = 0; j < albumOGJson.getJSONArray("Items").length(); j++) {
                            if (albumOGJson.getJSONArray("Items").getJSONObject(j).get("IndexNumber").toString().equals(String.valueOf(indexNumberInt + plusTrack))) {
                                nextTrackId = albumOGJson.getJSONArray("Items").getJSONObject(j).get("Id").toString();
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                // We verify that the nexttrackid is not null
                if (nextTrackId == null) {
                    throw new RuntimeException("Error while getting the next track");
                }

                // We create the url for the next track
                nextTrackUri = this.siteUrl + "/Audio/" + nextTrackId + "/main.m3u8?container=" + URLEncoder.encode("opus,webm|opus,mp3,aac,m4a|aac,m4a|alac,m4b|aac,flac,webma,webm|webma,wav,ogg", StandardCharsets.UTF_8) + "&MaxStreamingBitrate=999999999&AudioCodec=aac&TranscodingContainer=ts&TranscodingProtocol=hls" + "&api_key=" + this.accessToken;
            }
        }
        return nextTrackUri;
    }

    /**
     * A method to get the previous track uri from the current track uri
     * 
     * @param uri String : the current track uri
     * @return String : The previous track uri
     */
    public JSONObject getTrackInfosFromUri(String uri) {
        // We get the item id in the uri
        String[] uriSplit = uri.split("/");
        String itemId = uriSplit[4];

        // We get the infos for the track
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        // create a get request to get the album infos
        Request request = new Request.Builder()
                .url(siteUrl + "/Users/" + userId + "/Items/" + itemId)
                .header("Content-Type", "application/json")
                .header("Authorization", headerAuthorization)
                .build();

        // send the request and get the response in an object
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get the response body
        assert response != null;
        String responseBody = null;
        try {
            responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // We get the album infos
        JSONObject albumOGJson = new JSONObject(responseBody);

        // Create an empty JSONObject to store the album infos (so it's the same for all services)
        JSONObject albumInfosJson = new JSONObject();

        // We get the album infos
        albumInfosJson.put("Name", albumOGJson.get("Name").toString());
        albumInfosJson.put("AlbumArtist", albumOGJson.getJSONArray("AlbumArtists").getJSONObject(0).get("Name").toString());
        albumInfosJson.put("AlbumId", albumOGJson.get("AlbumId").toString());
        albumInfosJson.put("AlbumImage", albumOGJson.get("AlbumPrimaryImageTag").toString());

        return albumInfosJson;
    }

    /**
     * A method to get the AlbumArt of the album with the ImageId
     * 
     * @param ImageId String : the id of the image
     * @return Image : the image of the album
     */
    public Image getAlbumArt(String ImageId) {

        // We get the album art

        // First we verify if the image is in the cache
        String homeDir = System.getProperty("user.home");
        Path cacheDirectory = Paths.get(homeDir, ".javibes", "imgs");
        File file = new File(cacheDirectory + "/" + ImageId + ".jpg");

        // If the file exists we return the image
        return new Image(file.toURI().toString());
    }
}
