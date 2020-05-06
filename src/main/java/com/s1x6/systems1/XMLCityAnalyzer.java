package com.s1x6.systems1;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.Map;

public class XMLCityAnalyzer implements AutoCloseable {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    private final XMLStreamReader reader;


    public XMLCityAnalyzer(InputStream is) throws XMLStreamException {
        reader = FACTORY.createXMLStreamReader(is);
    }

    public CityInfo makeStatistics() {
        CityInfo info = new CityInfo();
        Map<String, Integer> edits = info.getEdits();
        Map<String, Integer> keys = info.getKeys();
        while (processUntil(XMLEvent.START_ELEMENT)) {
            if (reader.getLocalName().equals("node")) {
                int userAttributeIndex = getAttributeIndex("user");
                String userName = reader.getAttributeValue(userAttributeIndex);
                edits.put(userName, edits.getOrDefault(userName, 0) + 1);
            } else if (reader.getLocalName().equals("tag")) {
                int keyAttributeIndex = getAttributeIndex("k");
                String keyName = reader.getAttributeValue(keyAttributeIndex);
                keys.put(keyName, edits.getOrDefault(keyName, 0) + 1);
            }
        }
        return info;
    }

    private boolean processUntil(int stopEvent) {
        try {
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == stopEvent) {
                    return true;
                }
            }
        } catch (XMLStreamException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private int getAttributeIndex(String name) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (reader.getAttributeLocalName(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void close() throws XMLStreamException {
        reader.close();
    }
}
