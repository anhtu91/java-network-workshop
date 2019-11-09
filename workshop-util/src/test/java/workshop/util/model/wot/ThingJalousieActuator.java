package workshop.util.model.wot;

import java.util.Arrays;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A jalousie.
 */
public class ThingJalousieActuator extends Thing {
	public ThingJalousieActuator() {
		super("Jalousie", new JSONArray(Arrays.asList("MultiLevelSwitch")), "A web connected jalousie");

		/** Additional thing description area */

		JSONObject openDescription = new JSONObject();
		openDescription.put("@type", "OpenProperty");
		openDescription.put("title", "Open/Closed");
		openDescription.put("type", "boolean");
		openDescription.put("description", "Whether the jalousie is open or closed");

		Value<Boolean> open = new Value<>(true,
				// Here, you could send a signal to
				// the GPIO that switches the lamp
				// off
				v -> System.out.printf("Open-State is now %s\n", v));

		this.addProperty(new Property(this, "open", open, openDescription));

		JSONObject positionDescription = new JSONObject();
		positionDescription.put("@type", "LevelProperty");
		positionDescription.put("title", "Jalousie");
		positionDescription.put("type", "integer");
		positionDescription.put("description", "The position of the jalousie from 0-100");
		positionDescription.put("minimum", 0);
		positionDescription.put("maximum", 100);
		positionDescription.put("unit", "percent");

		Value<Integer> position = new Value<>(50,
				// Here, you could send a signal
				// to the GPIO that controls the
				// brightness
				l -> System.out.printf("Brightness is now %s\n", l));

		this.addProperty(new Property(this, "position", position, positionDescription));

		JSONObject fadeMetadata = new JSONObject();
		JSONObject fadeInput = new JSONObject();
		JSONObject fadeProperties = new JSONObject();
		JSONObject fadePosition = new JSONObject();
		JSONObject fadeDuration = new JSONObject();
		fadeMetadata.put("@type", "FadeAction");
		fadeMetadata.put("title", "Fade");
		fadeMetadata.put("description", "Fade the jalousie to a given level");
		fadeInput.put("type", "object");
		fadeInput.put("required", new JSONArray(Arrays.asList("position", "duration")));
		fadePosition.put("type", "integer");
		fadePosition.put("minimum", 0);
		fadePosition.put("maximum", 100);
		fadePosition.put("unit", "percent");
		fadeDuration.put("type", "integer");
		fadeDuration.put("minimum", 1);
		fadeDuration.put("unit", "milliseconds");
		fadeProperties.put("position", fadePosition);
		fadeProperties.put("duration", fadeDuration);
		fadeInput.put("properties", fadeProperties);
		fadeMetadata.put("input", fadeInput);
		this.addAvailableAction("fade", fadeMetadata, FadeAction.class);

		this.setHrefPrefix("/things/jalousie");
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
				thing.setProperty("position", input.getInt("position"));
			} catch (IllegalArgumentException e) {
			}
		}
	}
}
