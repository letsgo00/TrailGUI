package ca.jamiesinn.trailgui.files;

import ca.jamiesinn.trailgui.TrailGUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {
    private TrailGUI trailGUI;

    public Updater(TrailGUI trailGUI) {
        this.trailGUI = trailGUI;

    }

    private int getLatestBuildNumber() throws IOException {
        URL url = new URL("http://wat.sinnpi.com/api.php?api=latestjenkins&server=http://ci.md-5.net&job=TrailGUI");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/4.76");
        String output = "";
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {

            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                output += inputLine;
            }
        } catch (IOException e) {
            trailGUI.getLogger().info("Could not connect to the updater server. Will check on next enable.");
        }

        return Integer.parseInt(output);
    }

    private int getCurrentBuildNumber() {
        //Format: ${primaryVersion}-b${BUILD_NUMBER}-${releaseType}
        String currentVersion = trailGUI.getDescription().getVersion();
        if (currentVersion.contains("${BUILD_NUMBER}")) return -1;
        String[] split = currentVersion.split("-");
        String build = split[1].replaceAll("[A-z]", "");
        return Integer.parseInt(build);
    }

    private boolean isItUpToDate() throws IOException {
        return getLatestBuildNumber() <= getCurrentBuildNumber();
    }


    public void check() throws IOException {
        trailGUI.getLogger().info("Updater is disabled internally.");
    }
}
