package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;

import java.util.ArrayList;

// overall i changed the whole function of the class to find out how to get the specific affected vehicles
public class LinkEventHandler implements LinkLeaveEventHandler {

    // I changed the linksOfInterest variable to an ArrayList, so it could take in as many link to analyze as needed
    private static ArrayList<Id<Link>> linksOfInterest = new ArrayList();

    public ArrayList<String> vehicleIDs = new ArrayList();


    String getVehicleID(int pos) {

        return vehicleIDs.get(pos);
    }

    // taking a list of links to later analyze
    void setLinksOfInterest(ArrayList<String> entryList) {
        for(int i = 0; i < entryList.size(); i++)
        linksOfInterest.add(Id.createLinkId(entryList.get(i)));
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {
        // for every link on the list
        for (int i = 0; i < linksOfInterest.size(); i++ ) {
            // check, if an event happens on that link, grab that vehicle id and make sure it's not yet added into the list
            if (event.getLinkId().equals(linksOfInterest.get(i)) && !vehicleIDs.contains(event.getVehicleId().toString())) {
                // transform the id into a string and add it to the list
                String id = event.getVehicleId().toString();
                vehicleIDs.add(id);
            }
        }
    }
}