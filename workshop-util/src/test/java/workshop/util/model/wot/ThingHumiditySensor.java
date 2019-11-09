package workshop.util.model.wot;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import workshop.util.model.wot.Property;
import workshop.util.model.wot.Thing;
import workshop.util.model.wot.Value;

/**
 * A humidity sensor.
 */
public class ThingHumiditySensor extends Thing {
	/** The current value */
	private final Value<Double> level;

	public ThingHumiditySensor() {
		super("Humidity_Sensor", new JSONArray(Arrays.asList("MultiLevelSensor")),
				"A web connected humidity sensor");

		JSONObject levelDescription = new JSONObject();
		levelDescription.put("@type", "LevelProperty");
		levelDescription.put("title", "Humidity");
		levelDescription.put("type", "number");
		levelDescription.put("description", "The current humidity in %");
		levelDescription.put("minimum", 0);
		levelDescription.put("maximum", 100);
		levelDescription.put("unit", "percent");
		levelDescription.put("readOnly", true);
		this.level = new Value<>(0.0);
		this.addProperty(new Property(this, "level", level, levelDescription));
		this.setHrefPrefix("/things/humiditysensor");
	}
}
