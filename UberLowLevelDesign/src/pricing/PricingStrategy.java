package pricing;

import enums.VehicleType;
import models.Location;

public interface PricingStrategy {
    double calculateFare(Location from, Location to, VehicleType vehicleType);
}