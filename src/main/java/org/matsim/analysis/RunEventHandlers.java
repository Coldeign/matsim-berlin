package org.matsim.analysis;

import org.apache.commons.csv.CSVFormat;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

// this is basically my analysis program. with additional code and some tweaks between the different tasks,
// it gives out all the data shown in the Policy_Stats.csv file, in which we put the different data
public class RunEventHandlers {

    //set to true to get results for our simulation with the policy
    private static boolean policyInPlace = false;

    //paths to files
    private static final String networkFile = "Z:\\Studium\\Bachelor - Verkehrswesen\\FS06\\Multi-agent transport simulation\\Homework 1\\open-berlin\\scenarios\\berlin-v5.5-1pct\\output-berlin-v5.5-1pct_Policy\\berlin-v5.5-1pct.output_network.xml.gz";
    private static final String eventsFileNoPolicy = "Z:\\Studium\\Bachelor - Verkehrswesen\\FS06\\Multi-agent transport simulation\\Homework 1\\control_sim\\scenarios\\berlin-v5.5-1pct\\output-berlin-v5.5-1pct_noPolicy\\berlin-v5.5-1pct.output_events.xml.gz";

    private static final String eventsFilePolicy = "Z:\\Studium\\Bachelor - Verkehrswesen\\FS06\\Multi-agent transport simulation\\Homework 1\\open-berlin\\scenarios\\berlin-v5.5-1pct\\output-berlin-v5.5-1pct_Policy\\berlin-v5.5-1pct.output_events.xml.gz";
    private static final String outFile = "C:\\Users\\carlj\\IdeaProjects\\matsim-berlin_control\\";

    public static void main(String[] args) {
        ArrayList<String> listOfLinks = new ArrayList();
        ArrayList<String> vehicles = new ArrayList();
        ArrayList<Double> tripDistance = new ArrayList();

        // List of links, we identified in Via and edited to prohibit cars
        File listOfLinksFile = new File("C:\\Users\\carlj\\IdeaProjects\\matsim-berlin_control\\affLinkIDs.csv");
        try (BufferedReader reader = new BufferedReader(new FileReader(listOfLinksFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(";");
                listOfLinks.add(values[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // after first running this code to find out the affected vehicles (and by that the affected persons), i put the IDs in this csv file to use at a later point
        File vehicleIDsFile = new File("C:\\Users\\carlj\\IdeaProjects\\matsim-berlin_control\\vehicleIDs.csv");
        try (BufferedReader reader = new BufferedReader(new FileReader(vehicleIDsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(";");
                vehicles.add(values[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        var network = NetworkUtils.readNetwork(networkFile);
        var manager = EventsUtils.createEventsManager();

        var linkHandler = new LinkEventHandler();
        linkHandler.setLinksOfInterest(listOfLinks);

        var simpleHandler = new SimpleEventHandler();
        var distanceHandler = new TravelledDistanceHandler(network);

        manager.addHandler(simpleHandler);
        manager.addHandler(linkHandler);
        manager.addHandler(distanceHandler);

        if(policyInPlace) {
            EventsUtils.readEvents(manager, eventsFilePolicy);
        }
        else EventsUtils.readEvents(manager, eventsFileNoPolicy);

        // needs to commented out, when analyzing the simulation results
        try (var writer = Files.newBufferedWriter(Paths.get(outFile + "vehicleIDs.csv")); var printer = CSVFormat.EXCEL.withDelimiter(';').withRecordSeparator("\r\n").withHeader("Affected Vehicle IDs").print(writer)) {
            // by analyzing which vehicles leave the links we want to shut, we know (in theory) which vehicles have to change routes when the policy is in place
            for (int i = 0; i < linkHandler.vehicleIDs.size(); i++) {
                printer.printRecord(linkHandler.getVehicleID(i),simpleHandler.getTravelTimeByPerson(Id.createPersonId(linkHandler.getVehicleID(i))) + "s");
                printer.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (var writer = Files.newBufferedWriter(Paths.get(outFile + "vehicles_and_travel_times.csv")); var printer = CSVFormat.EXCEL.withDelimiter(';').withRecordSeparator("\r\n").withHeader("Affected Vehicle IDs","Travel Time").print(writer)) {

            for (int i = 0; i < vehicles.size(); i++) {
                printer.printRecord(vehicles.get(i),simpleHandler.getTravelTimeByPerson(Id.createPersonId(vehicles.get(i))) + "s");
                printer.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // copies the hashmap created by the TravelledDistanceHandler of to this file.
        Map<Id<Person>, List<Double>> personToTrips = new HashMap<Id<Person>, List<Double>>(distanceHandler.getPersonToTrips());

        // for every vehicleID (which corresponds to a personID from what I saw in the files)
        for(int i = 0; i < vehicles.size(); i++) {
            // grab the trip distance list
            List<Double> distances = personToTrips.get(Id.createPersonId(vehicles.get(i)));
            double totalDistance = 0;
            // check for null
            if(distances !=null) {
                // add all trips together
                for (int j = 0; j < distances.size(); j++) {
                    totalDistance += distances.get(j);
                }
            }
            // and give them to the tripDistance ArrayList
            tripDistance.add(totalDistance);
        }

        try (var writer = Files.newBufferedWriter(Paths.get(outFile + "vehicles_and_travel_distances.csv")); var printer = CSVFormat.EXCEL.withDelimiter(';').withRecordSeparator("\r\n").withHeader("Vehicle ID","Traveled Distance after Policy").print(writer)) {

            for (int i = 0; i < tripDistance.size(); i++) {
                printer.printRecord(vehicles.get(i),tripDistance.get(i) + "m");
                printer.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}