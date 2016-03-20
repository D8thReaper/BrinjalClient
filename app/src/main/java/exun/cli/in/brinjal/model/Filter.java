package exun.cli.in.brinjal.model;

/**
 * Created by n00b on 3/11/2016.
 */
public class Filter {

    public String tag;
    public int value;
    public boolean isChecked = false;

    public Filter(){}

    public Filter(String tag, int value){
        this.tag = tag;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getTag() {
        return tag;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void toggleChecked() {
        isChecked= !isChecked;
    }
}
