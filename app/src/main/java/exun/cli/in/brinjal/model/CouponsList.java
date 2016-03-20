package exun.cli.in.brinjal.model;

/**
 * Created by n00b on 3/9/2016.
 */
public class CouponsList {

    String title, description,conditions;

    public CouponsList(){}

    public CouponsList(String title, String description, String conditions){
        this.title = title;
        this.description = description;
        this.conditions = conditions;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
