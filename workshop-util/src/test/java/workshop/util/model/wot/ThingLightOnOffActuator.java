package workshop.util.model.wot;

import java.util.Arrays;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import workshop.util.model.wot.Action;
import workshop.util.model.wot.Event;
import workshop.util.model.wot.Property;
import workshop.util.model.wot.Thing;
import workshop.util.model.wot.Value;

/**
 * A simple onoff light.
 */
public class ThingLightOnOffActuator extends Thing {
	public ThingLightOnOffActuator() {
		super("Light_OnOff", new JSONArray(Arrays.asList("Light")), "A web connected onoff lamp");

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

		JSONObject fadeMetadata = new JSONObject();
		fadeMetadata.put("@type", "ToggleAction");
		fadeMetadata.put("title", "Toggle");
		fadeMetadata.put("description", "Toggle the lamp to a given level");
		this.addAvailableAction("toggle", fadeMetadata, ToggleAction.class);
		
		this.setHrefPrefix("/things/onofflight");
	}

	public static class Toggle extends Event {
		public Toggle(Thing thing, int data) {
			super(thing, "toggle", data);
		}
	}

	public static class ToggleAction extends Action {
		public ToggleAction(Thing thing, JSONObject input) {
			super(UUID.randomUUID().toString(), thing, "toggle", input);
		}

		@Override
		public void performAction() {
			Thing thing = this.getThing();

			try {
				thing.addEvent(new Toggle(thing, 102));
			} catch (IllegalArgumentException e) {
			}
		}
	}
}
