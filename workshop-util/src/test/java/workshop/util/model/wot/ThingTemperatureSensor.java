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
public class ThingTemperatureSensor extends Thing {
	/** The current value */
	private final Value<Double> temperature;

	public ThingTemperatureSensor() {
		super("Temperature_Sensor", new JSONArray(Arrays.asList("TemperatureSensor")),
				"A web connected temperature sensor");

		/** Additional thing description area */
		
		JSONObject levelDescription = new JSONObject();
		levelDescription.put("@type", "TemperatureProperty");
		levelDescription.put("title", "Temperature");
		levelDescription.put("type", "number");
		levelDescription.put("description", "The current temperature in °C");
		levelDescription.put("unit", "grad celsius");
		levelDescription.put("readOnly", true);
		this.temperature = new Value<>(0.0);
		this.addProperty(new Property(this, "temperature", temperature, levelDescription));
		
		this.setHrefPrefix("/things/temperaturesensor");
	}
}
