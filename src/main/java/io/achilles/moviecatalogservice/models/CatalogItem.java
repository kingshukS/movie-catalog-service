package io.achilles.moviecatalogservice.models;

public class CatalogItem {

    private String id;
    private String title;
    private String desc;
    private int rating;

    public CatalogItem(){}

    public CatalogItem(String id, String title, String desc, int rating) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
