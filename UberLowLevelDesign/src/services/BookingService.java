package services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import enums.BookingStatus;
import enums.VehicleType;
import models.Booking;
import models.Cab;
import models.Location;
import models.Rider;
import models.VehicleFareEstimate;
import pricing.PricingStrategy;

public class BookingService {
    private final CabService cabService;
    private final PricingStrategy pricingStrategy;

    private final DriverService driverService;

    public BookingService(CabService cabService, PricingStrategy pricingStrategy, DriverService driverService) {
        this.cabService = cabService;
        this.pricingStrategy = pricingStrategy;
        this.driverService = driverService;
    }

    public List<VehicleFareEstimate> showAvailableVehicleTypes(Location from, Location to) {
        List<VehicleFareEstimate> estimates = new ArrayList<>();
        for (VehicleType type : VehicleType.values()) {
            double fare = pricingStrategy.calculateFare(from, to, type);
            fare = Math.round(fare * 100.0) / 100.0;
            estimates.add(new VehicleFareEstimate(type, fare));
        }
        return estimates;
    }

    public void requestCab(Rider rider, VehicleType vehicleType, Location dropLocation) {
        double fare = pricingStrategy.calculateFare(rider.getCurrentLocation(), dropLocation, vehicleType);
        // Send Fare and Drop Location to the nearest Drivers to accept.
        List<Cab> nearestAvailableCabs = cabService.findNearestAvailableCabs(rider.getCurrentLocation(), vehicleType);

        // wait till drives to accept
        for (Cab cab : nearestAvailableCabs) {
            //Trigger Notifications
            driverService.triggerRideAcceptance(cab.getDriverName(), fare, dropLocation);
        }
    }

    // @AcceptListener
    public Booking bookCabFromListener(Cab acceptedCab, Rider rider, Location dropLocation) {
        acceptedCab.assignToRide();
        Booking booking = new Booking(rider, acceptedCab, dropLocation, pricingStrategy);
        booking.setStatus(BookingStatus.CREATED);
        return booking;
    }

    public Booking bookCab(Rider rider, VehicleType vehicleType, Location destination) {
        Cab cab = cabService.findNearestAvailableCab(rider.getCurrentLocation(), vehicleType);
        if (cab == null) {
            throw new RuntimeException("No available cab of type: " + vehicleType);
        }
        cab.assignToRide();
        Booking booking = new Booking(rider, cab, destination, pricingStrategy);
        return booking;
    }

    public void driverStartRide(Booking booking, String enteredOtp) {
        if (!booking.getOtp().equals(enteredOtp)) {
            System.out.println("Invalid OTP! Ride cannot start.");
            return;
        }
        booking.setStatus(BookingStatus.STARTED);
        booking.setStartTime(LocalDateTime.now());
        System.out.println("OTP verified. Ride started.");
    }

    public void driverEndRide(Booking booking) {
        if (booking.getStatus() != BookingStatus.STARTED) {
            System.out.println("Cannot end ride. Ride hasn't started.");
            return;
        }
        booking.setStatus(BookingStatus.ENDED);
        booking.setEndTime(LocalDateTime.now());
        double finalFare = pricingStrategy.calculateFare(
            booking.getPickupLocation(), booking.getDestination(), booking.getCab().getVehicleType()
        );
        booking.setFare(Math.round(finalFare * 100.0) / 100.0);
        System.out.println("Ride ended by driver.");
    }
}
