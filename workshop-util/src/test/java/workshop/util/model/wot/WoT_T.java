package workshop.util.model.wot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

//*************************************************************************************
/**
 * Test application to create web of things.
 * 
 * @author IKT MK
 * @version 2019-03-27
 */
// *************************************************************************************
public class WoT_T {
	/** log4j logger reference for this class */
	private static final Logger logger = LogManager.getFormatterLogger(WoT_T.class);

	public static void main(String[] args) {
		try {
			/** Create a thing that represents a dimmable light */
			Thing light = new ThingLightDimmableActuator();

			/** Create a thing that represents a temperature sensor */
			Thing temperature = new ThingTemperatureSensor();

			/** Create a thing that represents a humidity sensor */
			Thing humidity = new ThingHumiditySensor();

			/** Create a thing that represents a brightness sensor */
			Thing brightness = new ThingBrightnessSensor();

			/** Create a thing that represents a onoff light */
			Thing lightonoff = new ThingLightOnOffActuator();

			/** Create a thing that represents a air quality sensor */
			Thing airquality = new ThingAirQualitySensor();

			/** Create a thing that represents a air pressure sensor */
			Thing airpressure = new ThingAirPressureSensor();

			/** Create a thing that represents a air pressure sensor */
			Thing button = new ThingButtonSensor();

			/** Create a thing that represents a air pressure sensor */
			Thing jalousie = new ThingJalousieActuator();

			/** Creates an emty thing list */
			List<Thing> things = new ArrayList<>();

			/** Adds created things to the thing list */
			things.add(light);
			things.add(temperature);
			things.add(humidity);
			things.add(brightness);
			things.add(lightonoff);
			things.add(airquality);
			things.add(airpressure);
			things.add(button);
			things.add(jalousie);

			/** Displays and creates a json description file of a thing */
			for (int i = 0; i < things.size(); i++) {
				/** Gets the description of the thing */
				JSONObject description = things.get(i).asThingDescription();

				logger.info("########################################");

				logger.info("Thing name: '%s'", things.get(i).getName());

				logger.info("Thing description: '%s'", description);

				/**
				 * Creates a file writer to save the description into the file
				 */
				FileWriter fw = new FileWriter(String.format("conf/WoT_%s.json", things.get(i).getName()));

				/**
				 * Creates a buffer writer to link the description to the file
				 * writer
				 */
				BufferedWriter bw = new BufferedWriter(fw);

				/** Writes the description into the file */
				bw.write(description.toString());

				/** Closes the file */
				bw.close();
			}
		} catch (Throwable T) {
			logger.catching(T);
		}
	}
}
