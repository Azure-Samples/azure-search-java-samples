package model;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class Room {
    private String description = "";
    private String description_fr = "";
    private String type = "";
    private double baseRate = 0.0;
    private String bedOptions = "";
    private int sleepsCount = 0;
    private boolean smokingAllowed = false;
    private List<String> tags = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription_fr() {
        return description_fr;
    }

    public void setDescription_fr(String description_fr) {
        this.description_fr = description_fr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public String getBedOptions() {
        return bedOptions;
    }

    public void setBedOptions(String bedOptions) {
        this.bedOptions = bedOptions;
    }

    public int getSleepsCount() {
        return sleepsCount;
    }

    public void setSleepsCount(int sleepsCount) {
        this.sleepsCount = sleepsCount;
    }

    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public void setSmokingAllowed(boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public static Room FromJsonObject(JsonObject object) {
    	Room r = new Room();
    	r.setDescription(object.getString("Description"));
    	r.setDescription_fr(object.getString("Description_fr"));
    	r.setType(object.getString("Type"));
    	r.setBaseRate(object.getJsonNumber("BaseRate").doubleValue());
    	r.setBedOptions(object.getString("BedOptions"));
    	r.setSleepsCount(object.getInt("SleepsCount"));
    	r.setSmokingAllowed(object.getBoolean("SmokingAllowed"));
    	
		JsonArray tagsFromJson = object.getJsonArray("Tags");
		List<String> tags = new ArrayList<>();
		for (int i = 0; i < tagsFromJson.size(); i++) {
			tags.add(tagsFromJson.getString(i));
		}
		r.setTags(tags);
		
    	return r;
    }
}
