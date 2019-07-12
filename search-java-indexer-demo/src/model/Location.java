package model;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class Location {
	private String longitude = "";
	private String latitude = "";
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	@Override
	public String toString() {
		return "(" + latitude + ", " + longitude + ")";
	}
	
	public static Location FromJsonValue(JsonValue value) {
		Location l = new Location();
		if (value != JsonValue.NULL)
		{				
			JsonArray coordinatesArray = ((JsonObject)value).getJsonArray("coordinates");

			if (coordinatesArray != null && coordinatesArray.size() == 2)
			{
				JsonValue latitudeValue = coordinatesArray.get(0);
				JsonValue longitudeValue = coordinatesArray.get(1);

				String latitude = latitudeValue == null ? "" : latitudeValue.toString();
				String longitude = longitudeValue == null ? "" : longitudeValue.toString();

				l.setLatitude(latitude);
				l.setLongitude(longitude);
			}
		}
		return l;
	}
}
