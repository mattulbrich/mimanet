package de.matul.lepton_sim.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Component {
    private final String name;
    private Map<String, String> attributes = new HashMap<>();

    public Component(String name) {
        super();
        this.name = name;
    }

    public void putAttribute(String attribute, String value) {
        attributes.put(attribute, value);
    }

    public String getDevice() {
        return attributes.get("device");
    }

    public String getName() {
        return name;
    }

    public int getPinnumber() {
        return Integer.parseInt(attributes.getOrDefault("pinseq", "0"));
    }

    public String getAttribute(String attribute) {
        return attributes.get(attribute);
    }

    public int getWidth() {
        return Integer.parseInt(attributes.getOrDefault("width", "1"));
    }

    public Map<String, String> allAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Component addPrefix(String prefix) {
        Component result = new Component(prefix + "." + name);
        result.attributes = new HashMap(attributes);
        return result;
    }
}
