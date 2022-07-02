package org.matsim.analysis;

import org.apache.commons.csv.CSVFormat;
import org.matsim.core.events.EventsUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RunEventHandlers {

    private static final String eventsFile = "Z:\\Studium\\Bachelor - Verkehrswesen\\FS06\\Multi-agent transport simulation\\Homework 1\\control_sim\\scenarios\\berlin-v5.5-1pct\\output-berlin-v5.5-1pct_noPolicy\\berlin-v5.5-1pct.output_events.xml.gz";
    private static final String outFile = "Z:\\Studium\\Bachelor - Verkehrswesen\\FS06\\Multi-agent transport simulation\\Homework 1\\control_sim\\scenarios\\berlin-v5.5-1pct\\output-berlin-v5.5-1pct_noPolicy\\berlin-v5.5-1pct.output_links_and_agents.csv";

    private static final int[] listOfLinks = new int[13];

    public static void main(String[] args) {

        listOfLinks[0] = 151912;
        listOfLinks[1] = 67932;
        listOfLinks[2] = 66282;
        listOfLinks[3] = 101729;
        listOfLinks[4] = 57955;
        listOfLinks[5] = 66328;
        listOfLinks[6] = 57952;
        listOfLinks[7] = 126782;
        listOfLinks[8] = 153365;
        listOfLinks[9] = 290009;
        listOfLinks[10] = 153347;
        listOfLinks[11] = 29023;
        listOfLinks[12] = 79614;

        var manager = EventsUtils.createEventsManager();
        var linkHandler = new LinkEventHandler();
        /*
        var simpleHandler = new SimpleEventHandler();
        manager.addHandler(simpleHandler);
        */
        manager.addHandler(linkHandler);

        EventsUtils.readEvents(manager, eventsFile);

        //var volumes = linkHandler.getVolumes();

        /*
        try (var writer = Files.newBufferedWriter(Paths.get(outFile)); var printer = CSVFormat.DEFAULT.withDelimiter(';').withHeader("Hour", "Value","Agent IDs").print(writer)) {

            for (var volume : volumes.entrySet()) {
                printer.printRecord(volume.getKey(), volume.getValue());
                printer.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        try (var writer = Files.newBufferedWriter(Paths.get(outFile)); var printer = CSVFormat.EXCEL.withDelimiter(';').withRecordSeparator("\r").withHeader("Link ID","Agent IDs").print(writer)) {

            for (int listOfLink : listOfLinks) {
                printer.printRecord(Integer.toString(listOfLink), linkHandler.getVehicleIDs(listOfLink));
                printer.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}