package com.dionext.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SystemInspector {
    @Autowired
    private ConfigurableEnvironment env;
    class PropOverrideInfo {
        public String propValue;
        public String sourceName;
    }
    public String generatePropertiesReport(){

        String br = "\n";

        StringBuilder str = new StringBuilder();

        Map<String, List<PropOverrideInfo>> allProps
                = new HashMap<>();

        str.append("=======================");
        str.append(br);
        str.append("Properties by Sources");
        str.append(br);
        str.append("=======================");
        str.append(br);
        for (var ps : env.getPropertySources()) {
            str.append("=======================");
            str.append(br);
            str.append("PropertySource: " + ps.toString() + " map: " + (ps instanceof MapPropertySource));
            str.append(br);
            str.append("=======================");
            str.append(br);
            //TODO report from all sources
            if (ps instanceof MapPropertySource) {
                for (var key : ((MapPropertySource) ps).getSource().keySet()) {
                    String v = String.valueOf(ps.getProperty(key));
                    addToReport(br, str, allProps, ps, key, v);
                }
            }
            else if (ps instanceof SimpleCommandLinePropertySource){
                SimpleCommandLinePropertySource sps = (SimpleCommandLinePropertySource)ps;
                for (var key : sps.getPropertyNames()){
                    String v = sps.getProperty(key);
                    addToReport(br, str, allProps, ps, key, v);
                }
            }
        }
        str.append("=======================");
        str.append(br);
        str.append("All Properties");
        str.append(br);
        str.append("=======================");
        str.append(br);
        List<String> sorted = new ArrayList<>(allProps.keySet());
        Collections.sort(sorted);
        for(String key : sorted){
            List<PropOverrideInfo> list = allProps.get(key);
            StringBuilder overrideInfoStr = new StringBuilder();
            if (list.size() > 1) {
                for (var pi : list) {
                    overrideInfoStr.append(pi.sourceName);
                    overrideInfoStr.append(" value: " + pi.propValue + " ");
                }
            }
            str.append(((list.size() > 1)?"o ":"s ") + key + ": "  + env.getProperty(key) + " " + overrideInfoStr.toString());
            str.append(br);
        }
        return str.toString();
    }

    private void addToReport(String br, StringBuilder str, Map<String, List<PropOverrideInfo>> allProps, PropertySource<?> ps, String key, String v) {
        List<PropOverrideInfo> list = allProps.get(key);
        if (list == null) {
            list = new ArrayList<>();
            allProps.put(key, list);
        }
        PropOverrideInfo po = new PropOverrideInfo();
        po.sourceName = ps.toString();
        po.propValue = v;
        list.add(po);
        str.append("" + key + ": " + v);
        str.append(br);
    }

}
