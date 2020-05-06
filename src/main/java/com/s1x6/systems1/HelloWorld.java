package com.s1x6.systems1;

import com.s1x6.systems1.binding.Node;
import com.s1x6.systems1.binding.Osm;
import com.s1x6.systems1.db.DatabaseHelper;
import com.s1x6.systems1.db.dao.NodeDAO;
import com.s1x6.systems1.db.dao.TagDAO;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class HelloWorld {
    private static final int INSERT_AMOUNT = 30000;

    public static void main(String[] args) throws ParseException {
        Logger logger = LoggerFactory.getLogger(HelloWorld.class);
        logger.info("Hello World");
        Options options = new Options();
        options.addOption("t", true, "Sets the task number");
        options.addOption("h", false, "Prints help");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("h") || cmd.hasOption("--help")) {
            printHelp(options);
            return;
        }
        if (cmd.hasOption("t")) {
            if (cmd.getOptionValue("t").equals("1")) {
                System.out.println("Executing task one");
                doTaskOne();
            } else if (cmd.getOptionValue("t").equals("2")) {
                System.out.println("Executing task two");
                doTaskTwo();
            } else {
                printHelp(options);
            }
        } else {
            printHelp(options);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "app.jar [OPTIONS]", options );
//        System.out.println("args:\n\t-t : choose task number (1-2)\n\t-h / --help : prints help message");
    }

    private static void doTaskOne() {
        try (XMLCityAnalyzer cityAnalyzer = new XMLCityAnalyzer(ArchiveReader.getContentReader("RU-NVS.osm.bz2"))) {
            CityInfo info = cityAnalyzer.makeStatistics();
            System.out.println("Unique keys in tags: " + info.getKeys().keySet().size());
            printMap(info.getEdits());
            System.out.println("--------------------------------");
            printMap(info.getKeys());
        } catch (IOException | XMLStreamException ex) {
            ex.printStackTrace();
        }
    }

    private static void doTaskTwo() {
        Osm osm = unmarshal();
        try (DatabaseHelper db = new DatabaseHelper(
//                ?rewriteBatchedStatements=true
                "jdbc:postgresql://localhost:5432/postgres",
                "s1x6",
                "password"
        )) {
            NodeDAO nodeDAO = new NodeDAO(db.getConnection());
            TagDAO tagDAO = new TagDAO(db.getConnection());
            if (osm != null) {
                List<Node> sublist = osm.getNode().subList(0, INSERT_AMOUNT);
                tagDAO.insertSql(sublist.get(0).getTag());
                calculateAndPrintTime("Sql insert string: ",
                        () -> nodeDAO.insertSql(sublist)
                );
                nodeDAO.deleteAll();
                calculateAndPrintTime("Prepared statement: ",
                        () -> nodeDAO.insertPreparedStatement(sublist)
                );
                nodeDAO.deleteAll();
                calculateAndPrintTime("Batch: ",
                        () -> nodeDAO.insertBatch(sublist)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void calculateAndPrintTime(String label, Runnable r) {
        long timeStart = System.currentTimeMillis();
        r.run();
        long diff = System.currentTimeMillis() - timeStart;
        System.out.println(label + String.format(
                "%dm:%ds, diff in ms - %d. Insert speed " + INSERT_AMOUNT / (diff / 1000.0) + " r/s",
                diff / 1000 / 60, diff / 1000 % 60, diff)
        );
    }

    private static Osm unmarshal() {
        try {
            File file = new File("RU-NVS.osm");
            JAXBContext jaxbContext = JAXBContext.newInstance(Osm.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Osm) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void printMap(Map<String, Integer> map) {
        map.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .forEachOrdered(e -> System.out.println(e.getKey() + " - " + e.getValue()));
    }
}
