package pricing;

import enums.VehicleType;
import models.Location;

public class SurgePricingStrategy implements PricingStrategy {
	
    private int demandCount;
    // private DemandService demandService

    public SurgePricingStrategy(int demandCount) {
        this.demandCount = demandCount;
    }


	@Override
	public double calculateFare(Location from, Location to, VehicleType vehicleType) {
		double baseRatePerKm = switch (vehicleType) {
        case HATCHBACK -> 8.0;
        case SEDAN -> 10.0;
        case SUV -> 12.5;
		};
    	double distance = from.distanceTo(to);
    	// demandService.getSurgeMultiplier(from, to);
    	double surgeMultiplier = 1 + (demandCount / 10.0); // e.g., 10+ riders = 2x fare
    	return baseRatePerKm * distance * surgeMultiplier;
	}
}