package org.matsim.urbanEV;


import com.google.common.collect.Iterables;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.ev.infrastructure.Charger;
import org.matsim.contrib.ev.infrastructure.ChargerSpecification;
import org.matsim.contrib.ev.infrastructure.ImmutableChargerSpecification;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVToXML {

    public String fileName;
    List<ChargerSpecification> chargers = new ArrayList<>();
    public CSVToXML(String fileName) throws IOException, CsvException {


        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            List<String[]> rows = reader.readAll();





            for (String[] row : Iterables.skip(rows, 1)) {
                ImmutableChargerSpecification.Builder builder = ImmutableChargerSpecification.newBuilder();
                if (!(row.length == 1)) {
                    if(Double.parseDouble(row[1]) > 0){
                        chargers.add(builder
                                .linkId(Id.createLinkId(row[0]))
                                .id(Id.create("Charger" +row[0], Charger.class))
                                .chargerType("3.7kW")
                                .plugCount((int) Math.round(Double.parseDouble(row[1])))
                                .plugPower(3.7)
                                .build());


                }
                    else if (Double.parseDouble(row[2]) > 0){
                        chargers.add(builder
                                .linkId(Id.createLinkId(row[0]))
                                .id(Id.create("Charger" +row[0], Charger.class))
                                .chargerType("11kW")
                                .plugCount((int) Math.round(Double.parseDouble(row[2])))
                                .plugPower(11)
                                .build());
                    }
                    else if (Double.parseDouble(row[3]) > 0){
                        chargers.add(builder
                                .linkId(Id.createLinkId(row[0]))
                                .id(Id.create("Charger" +row[0], Charger.class))
                                .chargerType("22kW")
                                .plugCount((int) Math.round(Double.parseDouble(row[3])))
                                .plugPower(22)
                                .build());
                    }

                    else if (Double.parseDouble(row[4]) > 0){
                        chargers.add(builder
                                .linkId(Id.createLinkId(row[0]))
                                .id(Id.create("Charger" + row[0], Charger.class))
                                .chargerType("50kW")
                                .plugCount((int) Math.round(Double.parseDouble(row[4])))
                                .plugPower(50)
                                .build());
                    }

                    else if(Double.parseDouble(row[5]) > 0){
                        chargers.add(builder
                                .linkId(Id.createLinkId(row[0]))
                                .id(Id.create("Charger" +row[0], Charger.class))
                                .chargerType("150kW")
                                .plugCount((int) Math.round(Double.parseDouble(row[5])))
                                .plugPower(150)
                                .build());
                    }


            }


            //rows.forEach(x -> System.out.println(Arrays.toString(x)));

        }
           // System.out.println(chargers);
        }

    }

    public static void main(String[] args) throws IOException, CsvException {
        String fileName = "C:\\Users\\admin\\IdeaProjects\\matsim-berlin\\src\\main\\java\\org\\matsim\\urbanEV\\ind_1004.csv";
        CSVToXML csvreader = new CSVToXML(fileName);
        new CreateNewXML(csvreader.chargers);


    }

}



