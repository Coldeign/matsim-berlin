/*
package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

import java.util.HashMap;
import java.util.Map;

public class LinkLeavePersonID implements LinkLeaveEventHandler {

    private static final Id<Link> linkOfInterest = Id.createLinkId("67932");

    private final Map<String, Integer> volumes = new HashMap<>();

    Map<String, Integer> getVolumes() {
        return volumes;
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {

        if (event.getLinkId().equals(linkOfInterest)) {
            //String affIDs = event.getVehicleId();
            //  int currentCount = volumes.get(key);
            //  int newCount = currentCount + 1;
            //  volumes.put(key, newCount);

            // shorter version
            //volumes.merge(affIDs, 1, Integer::sum);
        }
    }



    private Id<Person> getPID(Id<Link> vehicleID) {
        return vehicleID;
    }
}*/
