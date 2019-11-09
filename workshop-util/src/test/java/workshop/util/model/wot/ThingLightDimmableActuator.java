package workshop.util.model.wot;

import java.util.Arrays;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A dimmable light.
 */
public class ThingLightDimmableActuator extends Thing {
	public ThingLightDimmableActuator() {
		super("Light_Dimmable", new JSONArray(Arrays.asList("MultiLevelSwitch")), "A web connected dimmable lamp");

		/** Additional thing description area */

		JSONObject onDescription = new JSONObject();
		onDescription.put("@type", "OnOffProperty");
		onDescription.put("title", "On/Off");
		onDescription.put("type", "boolean");
		onDescription.put("description", "Whether the lamp is turned on");

		Value<Boolean> on = new Value<>(true,
				// Here, you could send a signal to
				// the GPIO that switches the lamp
				// off
				v -> System.out.printf("On-State is now %s\n", v));

		this.addProperty(new Property(this, "on", on, onDescription));

		JSONObject brightnessDescription = new JSONObject();
		brightnessDescription.put("@type", "BrightnessProperty");
		brightnessDescription.put("title", "Brightness");
		brightnessDescription.put("type", "integer");
		brightnessDescription.put("description", "The level of light from 0-100");
		brightnessDescription.put("minimum", 0);
		brightnessDescription.put("maximum", 100);
		brightnessDescription.put("unit", "percent");

		Value<Integer> brightness = new Value<>(50,
				// Here, you could send a signal
				// to the GPIO that controls the
				// brightness
				l -> System.out.printf("Brightness is now %s\n", l));

		this.addProperty(new Property(this, "brightness", brightness, brightnessDescription));

		JSONObject fadeMetadata = new JSONObject();
		JSONObject fadeInput = new JSONObject();
		JSONObject fadeProperties = new JSONObject();
		JSONObject fadeBrightness = new JSONObject();
		JSONObject fadeDuration = new JSONObject();
		fadeMetadata.put("@type", "FadeAction");
		fadeMetadata.put("title", "Fade");
		fadeMetadata.put("description", "Fade the lamp to a given level");
		fadeInput.put("type", "object");
		fadeInput.put("required", new JSONArray(Arrays.asList("brightness", "duration")));
		fadeBrightness.put("type", "integer");
		fadeBrightness.put("minimum", 0);
		fadeBrightness.put("maximum", 100);
		fadeBrightness.put("unit", "percent");
		fadeDuration.put("type", "integer");
		fadeDuration.put("minimum", 1);
		fadeDuration.put("unit", "milliseconds");
		fadeProperties.put("brightness", fadeBrightness);
		fadeProperties.put("duration", fadeDuration);
		fadeInput.put("properties", fadeProperties);
		fadeMetadata.put("input", fadeInput);
		this.addAvailableAction("fade", fadeMetadata, FadeAction.class);

		JSONObject overheatedMetadata = new JSONObject();
		overheatedMetadata.put("description", "The lamp has exceeded its safe operating temperature");
		overheatedMetadata.put("type", "number");
		overheatedMetadata.put("unit", "degree celsius");
		this.addAvailableEvent("overheated", overheatedMetadata);

		this.setHrefPrefix("/things/dimmablelight");
	}

	public static class OverheatedEvent extends Event {
		public OverheatedEvent(Thing thing, int data) {
			super(thing, "overheated", data);
		}
	}

	public static class FadeAction extends Action {
		public FadeAction(Thing thing, JSONObject input) {
			super(UUID.randomUUID().toString(), thing, "fade", input);
		}

		@Override
		public void performAction() {
			Thing thing = this.getThing();
			JSONObject input = this.getInput();
			try {
				Thread.sleep(input.getInt("duration"));
			} catch (InterruptedException e) {
			}

			try {
				thing.setProperty("brightness", input.getInt("brightness"));
				thing.addEvent(new OverheatedEvent(thing, 102));
			} catch (IllegalArgumentException e) {
			}
		}
	}
}