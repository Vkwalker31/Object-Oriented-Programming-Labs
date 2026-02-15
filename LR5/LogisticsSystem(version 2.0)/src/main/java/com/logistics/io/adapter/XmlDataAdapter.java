package com.logistics.io.adapter;

import com.logistics.io.dto.CargoItemInput;
import com.logistics.io.dto.InputData;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Класс XmlDataAdapter — адаптер для чтения входных данных из XML.
 * GoF: Adapter — преобразует XML-поток в единый DTO InputData.
 * Ожидаемая структура: root с элементами cargo (type, quantity), distance, transportType, destination.
 */

public class XmlDataAdapter implements DataFormatAdapter {

    @Override
    public boolean supports(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".xml");
    }

    @Override
    public InputData parse(InputStream input) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();

        List<CargoItemInput> cargo = new ArrayList<>();
        NodeList cargoNodes = root.getElementsByTagName("cargo");
        for (int i = 0; i < cargoNodes.getLength(); i++) {
            Element c = (Element) cargoNodes.item(i);
            String type = getText(c, "type");
            int qty = Integer.parseInt(getText(c, "quantity").isEmpty() ? "0" : getText(c, "quantity"));
            cargo.add(new CargoItemInput(type, qty));
        }

        double distance = 0;
        try {
            distance = Double.parseDouble(getText(root, "distance"));
        } catch (NumberFormatException ignored) { }
        String transportType = getText(root, "transportType");
        String destination = getText(root, "destination");

        return InputData.builder()
                .cargo(cargo)
                .distance(distance)
                .transportType(transportType.isEmpty() ? null : transportType)
                .destination(destination.isEmpty() ? null : destination)
                .build();
    }

    private static String getText(Element parent, String tagName) {
        NodeList nl = parent.getElementsByTagName(tagName);
        if (nl.getLength() == 0) return "";
        return nl.item(0).getTextContent().trim();
    }
}
