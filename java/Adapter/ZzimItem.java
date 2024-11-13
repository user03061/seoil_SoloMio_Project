package Adapter;

public class ZzimItem {
    private String photoUrl;
    private String title;
    private String id;

    public ZzimItem(String photoUrl, String title, String id) {
        this.photoUrl = photoUrl;
        this.title = title;
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}
