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
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
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

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Map<UUID, String> call() throws Exception {
        final Map<UUID, String> uuidStringMap = new HashMap<UUID, String>();
        for (final UUID uuid : uuids) {
            final HttpURLConnection connection = (HttpURLConnection) new URL(
                    PROFILE_URL + uuid.toString().replace("-", "")).openConnection();

            JSONObject response = null;

            String name = null;

            String fromStream = null;
            try {
                response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));

                name = (String) response.get("name");

            } catch (final ParseException e) {
                // Try converting the stream to a string and removing all the
                // spaces.
                fromStream = fromStream(connection.getInputStream()).replaceAll(" ", "");

                // Parse again
                response = (JSONObject) jsonParser.parse(fromStream);

                // Should work now!
                name = (String) response.get("name");

                if (name == null) {
                    System.out.print("[Autorank] Could not parse uuid '" + uuid.toString() + "' to name!");
                    continue;
                }

                final String error = (String) response.get("error");
                final String errorMessage = (String) response.get("errorMessage");
                if (error != null && error.length() > 0) {
                    throw new IllegalStateException(errorMessage);
                }
            } finally {
                if (name == null || response == null) {
                    System.out.print("[Autorank] Could not find name of account with uuid: '" + uuid.toString() + "'");
                }
            }

            uuidStringMap.put(uuid, name);
        }
        return uuidStringMap;
    }
}
