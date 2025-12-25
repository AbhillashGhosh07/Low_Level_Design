package services;

import java.util.HashMap;
import java.util.Map;

import models.Rider;

public class RiderService {
    private final Map<String, Rider> riders = new HashMap<>();

    public Rider registerRider(String id, String name) {
        Rider rider = new Rider(id, name);
        riders.put(id, rider);
        return rider;
    }

    public Rider getRider(String id) {
        return riders.get(id);
    }
}