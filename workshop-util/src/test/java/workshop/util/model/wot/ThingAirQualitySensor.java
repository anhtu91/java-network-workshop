package workshop.util.model.wot;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import workshop.util.model.wot.Property;
import workshop.util.model.wot.Thing;
import workshop.util.model.wot.Value;

/**
 * A air quality sensor.
 */
public class ThingAirQualitySensor extends Thing {
	/** The current value */
	private final Value<Double> level;

	public ThingAirQualitySensor() {
		super("AirQuality_Sensor", new JSONArray(Arrays.asList("MultiLevelSensor")),
				"A web connected air quality sensor");

		JSONObject levelDescription = new JSONObject();
		levelDescription.put("@type", "LevelProperty");
		levelDescription.put("title", "Air quality");
		levelDescription.put("type", "number");
		levelDescription.put("description", "The current air quality in parts per million");
		levelDescription.put("minimum", 0);
		levelDescription.put("maximum", 1000);
		levelDescription.put("unit", "ppm");
		levelDescription.put("readOnly", true);
		this.level = new Value<>(0.0);
		this.addProperty(new Property(this, "level", level, levelDescription));
		this.setHrefPrefix("/things/airqualitysensor");
	}
}
