package workshop.util.model.wot;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import workshop.util.model.wot.Property;
import workshop.util.model.wot.Thing;
import workshop.util.model.wot.Value;

/**
 * A humidity brightness.
 */
public class ThingBrightnessSensor extends Thing {
	/** The current value */
	private final Value<Double> level;

	public ThingBrightnessSensor() {
		super("Brightness_Sensor", new JSONArray(Arrays.asList("MultiLevelSensor")),
				"A web connected brightness sensor");

		JSONObject levelDescription = new JSONObject();
		levelDescription.put("@type", "LevelProperty");
		levelDescription.put("title", "Brightness");
		levelDescription.put("type", "number");
		levelDescription.put("description", "The current brightness in lux");
		levelDescription.put("minimum", 0);
		levelDescription.put("maximum", 100000);
		levelDescription.put("unit", "lux");
		levelDescription.put("readOnly", true);
		this.level = new Value<>(0.0);
		this.addProperty(new Property(this, "level", level, levelDescription));
		this.setHrefPrefix("/things/brightnesssensor");
	}
}
