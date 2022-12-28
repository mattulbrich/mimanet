package de.matul.lepton_sim.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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

    public int getIntAttribute(String attribute, int defaultValue) {
        String attr = getAttribute(attribute);
        if (attr == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(attr);
        }
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

    public int[] expandSpec() {
        String spec = attributes.get("spec");
        if (spec == null) {
            throw new NoSuchElementException("No spec on component " + getName());
        }
        List<Integer> list = new ArrayList<>();
        for (String part : spec.trim().split(" *, *")) {
            String[] range = part.split("-",2);
            int from = Integer.parseInt(range[0]), to;
            if (range.length > 1) {
                to = Integer.parseInt(range[1]);
            } else {
                to = from;
            }
            for (int i = from; i <= to; i++) {
                list.add(i);
            }
        }
        int[] result = new int[list.size()];
        int i = 0;
        for (Integer val : list) {
            result[i++] = val;
        }
        return result;
    }
}
