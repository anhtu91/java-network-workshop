package workshop.util.model.wot;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import workshop.util.model.wot.Property;
import workshop.util.model.wot.Thing;
import workshop.util.model.wot.Value;

/**
 * A button sensor.
 */
public class ThingButtonSensor extends Thing {
	/** The current value */
	private final Value<Boolean> pushed;

	public ThingButtonSensor() {
		super("Button_Sensor", new JSONArray(Arrays.asList("PushButton")),
				"A push button");

		/** Additional thing description area */
		
		JSONObject levelDescription = new JSONObject();
		levelDescription.put("@type", "PushedProperty");
		levelDescription.put("title", "Button sensor");
		levelDescription.put("type", "boolean");
		levelDescription.put("description", "The current active / inactive state");
		levelDescription.put("state", "true or false");
		levelDescription.put("readOnly", true);
		this.pushed = new Value<>(false);
		this.addProperty(new Property(this, "pushed", pushed, levelDescription));
		
		JSONObject pressedMetadata = new JSONObject();
		pressedMetadata.put("@type", "PressedEvent");
		pressedMetadata.put("description", "The button was pressed");
		pressedMetadata.put("type", "boolean");
		pressedMetadata.put("state", "true or false");
		this.addAvailableEvent("pressed", pressedMetadata);

		this.setHrefPrefix("/things/pushbutton");
	} 
	
	public static class PressedEvent extends Event {
		public PressedEvent(Thing thing, int data) {
			super(thing, "pressed", data);
		}
	}
}
