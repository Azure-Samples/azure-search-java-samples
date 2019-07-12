package model;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class Document {
	private String hotelId = "";
	private String hotelName = "";
	private String description = "";
	private String description_fr = "";
	private String category = "";
	private List<String> tags = null;
	private boolean parkingIncluded = false;
	private String lastRenovationDate = "";
	private double rating = 0.0;
	private Address address = null;
	private Location location = null;
	private List<Room> rooms = null;

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public boolean isParkingIncluded() {
		return parkingIncluded;
	}

	public void setParkingIncluded(boolean parkingIncluded) {
		this.parkingIncluded = parkingIncluded;
	}

	public String getLastRenovationDate() {
		return lastRenovationDate;
	}

	public void setLastRenovationDate(String lastRenovationDate) {
		this.lastRenovationDate = lastRenovationDate;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	
	public static Document FromJsonObject(JsonObject object) {
		Document d = new Document();
		d.setHotelId(object.getString("HotelId"));
		d.setHotelName(object.getString("HotelName"));
		d.setDescription(object.getString("Description"));
		d.setDescription_fr(object.getString("Description_fr"));
		d.setCategory(object.getString("Category"));
		
		JsonArray tagsFromJson = object.getJsonArray("Tags");
		List<String> tags = new ArrayList<>();
		for (int i = 0; i < tagsFromJson.size(); i++) {
			tags.add(tagsFromJson.getString(i));
		}
		d.setTags(tags);
		
		d.setParkingIncluded(object.getBoolean("ParkingIncluded"));
		
		JsonValue dateValue = object.get("LastRenovationDate");
		d.setLastRenovationDate(dateValue == JsonValue.NULL ? " " : dateValue.toString());
		
		d.setRating(object.getJsonNumber("Rating").doubleValue());
		d.setAddress(Address.FromJsonObject(object.getJsonObject("Address")));		
		d.setLocation(Location.FromJsonValue(object.get("Location")));
		
		JsonArray roomsFromJson = object.getJsonArray("Rooms");
		List<Room> rooms = new ArrayList<>();
		for (int i = 0; i < roomsFromJson.size(); i++) {
			rooms.add(Room.FromJsonObject(roomsFromJson.getJsonObject(i)));
		}
		d.setRooms(rooms);
		return d;
	}
}
