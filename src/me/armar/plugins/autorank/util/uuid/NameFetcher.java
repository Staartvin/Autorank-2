package me.armar.plugins.autorank.util.uuid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.collect.ImmutableList;

/**
 * This class is used to get the name of a player from a UUID.
 * <p>
 * Date created: 17:02:13 2 apr. 2014
 * 
 * @author evilmidget38
 * 
 */
public class NameFetcher implements Callable<Map<UUID, String>> {
	private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	public static String fromStream(final InputStream in) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				in));
		final StringBuilder out = new StringBuilder();
		final String newLine = System.getProperty("line.separator");
		String line;

		while ((line = reader.readLine()) != null) {

			out.append(line);
			out.append(newLine);
		}
		return out.toString();
	}
	private final JSONParser jsonParser = new JSONParser();

	private final List<UUID> uuids;

	public NameFetcher(final List<UUID> uuids) {
		this.uuids = ImmutableList.copyOf(uuids);
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Map<UUID, String> call() throws Exception {
		final Map<UUID, String> uuidStringMap = new HashMap<UUID, String>();
		for (final UUID uuid : uuids) {
			final HttpURLConnection connection = (HttpURLConnection) new URL(
					PROFILE_URL + uuid.toString().replace("-", ""))
					.openConnection();

			JSONObject response = null;

			String name = null;

			String fromStream = null;
			// Ping code 204 == No content (Request was sent, but UUID was invalid)
			final int pingCode = connection.getResponseCode();

			/*if (pingCode == 204) {
				Bukkit.getLogger().warning("Tried to get UUID: " + uuid.toString() + " but this invalid.");
				continue;
			}*/

			System.out.print("Ping: " + pingCode);

			try {
				response = (JSONObject) jsonParser.parse(new InputStreamReader(
						connection.getInputStream()));

				System.out.print("Response: " + response);

				name = (String) response.get("name");

				System.out.print("Name: " + name);

				if (name == null) {
					continue;
				}

				// Try converting the stream to a string and removing all the spaces. 
				fromStream = fromStream(connection.getInputStream())
						.replaceAll(" ", "");

				System.out.print("from Stream: " + fromStream);

				// Parse again
				response = (JSONObject) jsonParser.parse(fromStream);

				// Should work now!
				name = (String) response.get("name");

				if (name == null) {
					System.out.print("Could not parse uuid '" + uuid.toString()
							+ "' to name!");
					continue;
				}

				final String cause = (String) response.get("cause");
				final String errorMessage = (String) response
						.get("errorMessage");
				if (cause != null && cause.length() > 0) {
					throw new IllegalStateException(errorMessage);
				}
			} catch (final ParseException e) {
				System.out.print("Could not identify returned values!");
			}

			/*try {
				response = (JSONObject) jsonParser.parse(new InputStreamReader(
						connection.getInputStream()));

				name = (String) response.get("name");
				if (name == null) {
					continue;
				}
				
				System.out.print("Name: " + name);
				
				final String cause = (String) response.get("cause");
				final String errorMessage = (String) response
						.get("errorMessage");
				if (cause != null && cause.length() > 0) {
					throw new IllegalStateException(errorMessage);
				}
			}*/

			uuidStringMap.put(uuid, name);
		}
		return uuidStringMap;
	}
}
