package org.matsim.analysis;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;

/*
import java.util.HashMap;
import java.util.Map;
*/

public class LinkEventHandler implements LinkLeaveEventHandler {

    private static Id<Link> linkOfInterest;

    //private final Map<String, Integer> volumes = new HashMap<>();

    private String vehicleIDs = "";

    /*
    Map<String, Integer> getVolumes() {
        return volumes;
    }
    */
    String getVehicleIDs( int link) {
        setLinkOfInterest(link);
        return vehicleIDs;
    }
    //String getLinkOfInterest() {return linkOfInterest.toString();}
    void setLinkOfInterest(int entryList) {
        linkOfInterest = Id.createLinkId(entryList);
    }

    /*
    @Override
    public void handleEvent(LinkLeaveEvent event) {

        if (event.getLinkId().equals(linkOfInterest)) {

            String key = getKey(event.getTime());
            //  int currentCount = volumes.get(key);
            //  int newCount = currentCount + 1;
            //  volumes.put(key, newCount);

            // shorter version
            volumes.merge(key, 1, Integer::sum);

        }
    }

    private String getKey(double time) {
        return Integer.toString((int) (time / 3600));
    }
    */

    public void handleEvent(LinkLeaveEvent event) {
        boolean stop = false;
        do {
            if (vehicleIDs.isEmpty()) {
                vehicleIDs = event.getVehicleId().toString();
            }

            else if (event.getLinkId().equals(linkOfInterest) && !vehicleIDs.contains(event.getVehicleId().toString())) {

                String id = event.getVehicleId().toString();
                vehicleIDs = "\r" + id;
            }
            else stop = true;
        }while (!stop);
    }
}