package org.nwn.ts.simulation.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PassengerRouteData {
    private final RouteType type;
    private final List<Stop> stops = new ArrayList<>();

    public PassengerRouteData(RouteType type, List<Stop> stops) {
        this.type = type;
        this.stops.addAll(stops);
    }

    public PassengerRouteData(RouteType type) {
        this.type = type;
    }

    public RouteType getType() {
        return type;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public final static class Stop {
        private final String station;
        private final int arrivalTime;

        public Stop(String station, int arrivalTime) {
            this.station = station;
            this.arrivalTime = arrivalTime;
        }

        public String getStation() {
            return station;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Stop)) return false;
            Stop stop = (Stop) o;
            return arrivalTime == stop.arrivalTime &&
                    station.equals(stop.station);
        }

        @Override
        public int hashCode() {
            return Objects.hash(station, arrivalTime);
        }
    }
}
