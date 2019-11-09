package workshop.util.model.wot;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import workshop.util.model.wot.Property;
import workshop.util.model.wot.Thing;
import workshop.util.model.wot.Value;

/**
 * A temperature sensor.
 */
public class ThingAirPressureSensor extends Thing {
	/** The current value */
	private final Value<Double> level;

	public ThingAirPressureSensor() {
		super("AirPressure_Sensor", new JSONArray(Arrays.asList("MultiLevelSensor")),
				"A web connected air pressure sensor");

		/** Additional thing description area */
		
		JSONObject levelDescription = new JSONObject();
		levelDescription.put("@type", "LevelProperty");
		levelDescription.put("title", "Air pressure");
		levelDescription.put("type", "number");
		levelDescription.put("description", "The current air pressure in hPa");
		levelDescription.put("minimum", 300);
		levelDescription.put("maximum", 1100);
		levelDescription.put("unit", "hPa");
		levelDescription.put("readOnly", true);
		this.level = new Value<>(0.0);
		this.addProperty(new Property(this, "level", level, levelDescription));
		
		this.setHrefPrefix("/things/airpressuresensor");
	}
}
