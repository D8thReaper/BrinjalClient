package exun.cli.in.brinjal.model;

/**
 * Created by n00b on 3/7/2016.
 */
public class StoreList {

    String image,title,subtitle,thumb,description,tags,locality,email;
    int id;
    int contact;
    int rating;
    float disInM;
    double longi,lati;
    private int isDeals;

    public StoreList(){}

    public StoreList(String image, String title, String subtitle, String thumb, String description,
                     String tags, int id, double longi, double lati,String locality,int rating,int disInM){
        this.image = image;
        this.title = title;
        this.subtitle = subtitle;
        this.thumb = thumb;
        this.description = description;
        this.tags = tags;
        this.id = id;
        this.longi = longi;
        this.lati = lati;
        this.locality = locality;
        this.rating = rating;
        this.disInM = disInM;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public double getLongi() {
        return longi;
    }

    public int getId() {
        return id;
    }

    public double getLati() {
        return lati;
    }

    public String getLocality() {
        return locality;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTags() {
        return tags;
    }

    public String getThumb() {
        return thumb;
    }

    public String getTitle() {
        return title;
    }

    public int getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getIsDeals() {
        return isDeals;
    }

    public float getDisInM() {
        return disInM;
    }

    public void setDisInM(float disInM) {
        this.disInM = disInM;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContact(int contact) {
        this.contact = contact;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsDeals(int isDeals) {
        this.isDeals = isDeals;
    }
}
