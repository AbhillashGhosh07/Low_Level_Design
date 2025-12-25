package services;

import java.util.*;

import enums.VehicleType;
import models.Cab;
import models.Location;

public class CabService {
    private final List<Cab> cabs = new ArrayList<>();

    public void registerCab(String id, String driverName, Location location, VehicleType vehicleType, String carNumber) {
        cabs.add(new Cab(id, driverName, location, vehicleType, carNumber));
    }

    public List<Cab> findNearbyCabs(Location riderLocation, double maxDistance) {
        List<Cab> nearby = new ArrayList<>();
        for (Cab cab : cabs) {
            if (cab.isAvailable() && cab.getLocation().distanceTo(riderLocation) <= maxDistance) {
                nearby.add(cab);
            }
        }
        return nearby;
    }

    public Cab findNearestAvailableCab(Location riderLocation, VehicleType vehicleType) {
        Cab nearestCab = null;
        double minDistance = Double.MAX_VALUE;

        for (Cab cab : cabs) {
            if (cab.isAvailable() && cab.getVehicleType() == vehicleType) {
                double distance = cab.getLocation().distanceTo(riderLocation);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestCab = cab;
                }
            }
        }

        return nearestCab;
    }

    public List<Cab> findNearestAvailableCabs(Location riderLocation, VehicleType vehicleType) {
        double minDistance = Double.MAX_VALUE;
        List<Cab> nearestCabs = new ArrayList<>();
        for(Cab cab: cabs) {
            if(cab.isAvailable() && cab.getVehicleType() == vehicleType) {
                double distance = cab.getLocation().distanceTo(riderLocation);
                if(distance < minDistance) {
                    nearestCabs.add(cab);
                }
            }
        }
        return nearestCabs;
    }
}