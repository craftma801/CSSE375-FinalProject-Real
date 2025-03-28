package main;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WorldMapLoader {

    public static List<CityData> loadCityData() {
        try {
            File file = new File("assets/map.json");
            ObjectMapper mapper = new ObjectMapper();
            CityData[] cityArray = mapper.readValue(file, CityData[].class);
            return Arrays.asList(cityArray);
        } catch (IOException e) {
            throw new RuntimeException("Error reading map.json" + e);
        }
    }
}

