package org.matsim.urbanEV.analysis;

import com.google.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contrib.ev.charging.ChargingEndEvent;
import org.matsim.contrib.ev.charging.ChargingEndEventHandler;
import org.matsim.contrib.ev.charging.ChargingStartEvent;
import org.matsim.contrib.ev.charging.ChargingStartEventHandler;
import org.matsim.contrib.ev.infrastructure.Charger;
import org.matsim.contrib.ev.infrastructure.ChargingInfrastructureSpecification;
import org.matsim.core.controler.IterationCounter;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.qsim.agents.WithinDayAgentUtils;
import org.matsim.core.router.StageActivityTypeIdentifier;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.utils.misc.Time;
import org.matsim.urbanEV.UrbanVehicleChargingHandler;
import org.matsim.withinday.utils.EditPlans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActsWhileChargingAnalyzer implements ActivityStartEventHandler, ActivityEndEventHandler, IterationEndsListener {

    @Inject
    OutputDirectoryHierarchy controlerIO;
    @Inject
    IterationCounter iterationCounter;
    @Inject
    Scenario scenario;


//   private Map<Id<Charger>, List<String>> actsPerCharger = new HashMap<>();
   private Map<Id<Person>, List<String>> actsPerPersons = new HashMap<>();
   private Map<Id<Link>, Id<Charger>> chargersAtLinks = new HashMap<>();
   static List<Container> containers = new ArrayList<>();
   static List<PersonContainer> personContainers = new ArrayList<>();



    @Inject
    public ActsWhileChargingAnalyzer(ChargingInfrastructureSpecification chargingInfrastructureSpecification, Scenario scenario){



        for (Id<Person> personId : scenario.getPopulation().getPersons().keySet()) {
            List<String> acts = new ArrayList<>();
            Container container = new Container(personId,acts);
            containers.add(container);

        }

        for (Id<Charger> chargerId : chargingInfrastructureSpecification.getChargerSpecifications().keySet()) {

            Id<Link> chargerLink = chargingInfrastructureSpecification.getChargerSpecifications().values().stream()
                                    .filter(chargerSpecification -> chargerSpecification.getId().equals(chargerId))
                                    .map(chargerSpecification -> chargerSpecification.getLinkId())
                                    .findAny()
                                    .get();
            chargersAtLinks.put(chargerLink, chargerId);

        }


    }

    @Override
    public void handleEvent(ActivityEndEvent event) {

        if (event.getActType().contains(UrbanVehicleChargingHandler.PLUGOUT_INTERACTION)){
        }
    }

    @Override
    public void handleEvent(ActivityStartEvent event) {
        if (!TripStructureUtils.isStageActivityType(event.getActType()))
        containers.stream()
                .filter(container -> container.personId.equals(event.getPersonId()))
                .findAny()
                .get()
                .acts.add(event.getActType());

        else if (event.getActType().contains(UrbanVehicleChargingHandler.PLUGIN_INTERACTION)){
            String chargingActAndTime = event.getActType()+ String.valueOf(event.getTime());
            containers.stream()
                    .filter(container -> container.personId.equals(event.getPersonId()))
                    .findAny()
                    .get()
                    .acts.add(chargingActAndTime);

            PersonContainer personContainer = new PersonContainer(event.getPersonId(), chargingActAndTime , event.getTime(), chargersAtLinks.get(event.getLinkId()));
            personContainers.add(personContainer);
        }

    }

    private static void compute(Map<Id<Person>, List<String>> map, ActivityStartEvent event) {
        map.compute(event.getPersonId(), (person,list) ->{
            if (list == null) list = new ArrayList<>();
            list.add(event.getActType());
            return list;
        });
    }

    @Override
    public void reset(int iteration) {

    }

    @Override
    public void notifyIterationEnds(IterationEndsEvent event) {

        CSVPrinter csvPrinter = null;
        try {
            csvPrinter = new CSVPrinter(Files.newBufferedWriter(Paths.get(controlerIO.getIterationFilename(iterationCounter.getIterationNumber(), "actsPerCharger.csv"))), CSVFormat.DEFAULT.withDelimiter(';').
                    withHeader("ChargerId", "Activity type", "Time"));

            for (PersonContainer personContainer : personContainers) {
                List<String> plan = containers.stream()
                        .filter(container -> container.personId.equals(personContainer.personId))
                        .findAny()
                        .get()
                        .acts;


                csvPrinter.printRecord(personContainer.chargerId, plan.get(plan.indexOf(personContainer.chargingAct)+1) , Time.writeTime(personContainer.time));


            }


            csvPrinter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    private class Container  {
        private final Id<Person> personId;
        private final List<String> acts;

        Container (Id<Person> personId, List<String> acts){
            this.personId = personId;
            this.acts = acts;
        }
    }

    private  class PersonContainer{
        private final Id<Person> personId;
        private final String chargingAct;
        private final double time;
        private final Id<Charger> chargerId;

        PersonContainer (Id<Person> personId, String chargingAct, double time, Id<Charger> chargerId){
            this.personId = personId;
            this.chargingAct = chargingAct;
            this.time = time;
            this.chargerId = chargerId;
        }


    }






}
