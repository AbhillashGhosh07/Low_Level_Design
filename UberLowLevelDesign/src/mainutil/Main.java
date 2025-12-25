package mainutil;

import java.util.List;
import java.util.Scanner;

import enums.BookingStatus;
import enums.VehicleType;
import models.Booking;
import models.Location;
import models.Rider;
import models.VehicleFareEstimate;
import pricing.SurgePricingStrategy;
import services.BookingService;
import services.CabService;
import services.DriverService;
import services.RiderService;
import pricing.PricingStrategy;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        RiderService riderService = new RiderService();
        DriverService driverService = new DriverService();
        CabService cabService = new CabService();

        // For demo, set demand = 5 to trigger surge
        PricingStrategy pricingStrategy = new SurgePricingStrategy(5);
        BookingService bookingService = new BookingService(cabService, pricingStrategy, driverService);

        // Register rider
        Rider rider = riderService.registerRider("r1", "Alice");
        rider.updateLocation(new Location(10, 10));
        Location destination = new Location(20, 20);

        // Register drivers & cabs
        driverService.registerDriver("d1", "DriverOne");
        cabService.registerCab("c1", "DriverOne", new Location(12, 10), VehicleType.SEDAN, "TS09AB1234");

        driverService.registerDriver("d2", "DriverTwo");
        cabService.registerCab("c2", "DriverTwo", new Location(11, 11), VehicleType.HATCHBACK, "TS08XY5678");

        // Step 1: Show fare estimates
        System.out.println("\n Fare Estimates:");
        List<VehicleFareEstimate> estimates = bookingService.showAvailableVehicleTypes(rider.getCurrentLocation(), destination);
        for (VehicleFareEstimate e : estimates) {
            System.out.println(" - " + e);
        }

        // Step 2: Simulate rider selecting a vehicle type
        VehicleType chosenType = VehicleType.SEDAN;

        Booking booking;
        try {
            booking = bookingService.bookCab(rider, chosenType, destination);
        } catch (RuntimeException e) {
            System.out.println("Booking failed: " + e.getMessage());
            return;
        }

        // Step 3: Show OTP to user
        System.out.println("\n Share this OTP with driver to start the ride: " + booking.getOtp());

        // Step 4: Driver enters OTP with max 3 attempts
        Scanner scanner = new Scanner(System.in);
        int maxAttempts = 3;
        boolean rideStarted = false;

        for (int i = 1; i <= maxAttempts; i++) {
            System.out.print(" Driver: Please enter OTP to start ride (Attempt " + i + " of " + maxAttempts + "): ");
            String enteredOtp = scanner.nextLine();

            bookingService.driverStartRide(booking, enteredOtp);
            if (booking.getStatus() == BookingStatus.STARTED) {
                rideStarted = true;
                break;
            }
        }

        if (!rideStarted) {
            System.out.println("Too many failed attempts. Ride cannot be started.");
            return;
        }

        // Step 5: Simulate ride in progress
        System.out.println("Ride is in progress...");
        for (int i = 1; i <= 5; i++) {
            System.out.print("Traveling");
            for (int j = 0; j < i; j++) System.out.print(".");
            System.out.println(" (" + i + " sec)");
            Thread.sleep(1000);
        }

        // Step 6: Driver ends ride
        bookingService.driverEndRide(booking);

        // Step 7: Summary
        System.out.println("\nFinal Ride Summary:");
        booking.printSummary();
    }
}
