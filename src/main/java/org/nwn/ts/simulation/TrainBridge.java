package org.nwn.ts.simulation;

public class TrainBridge {

    public native long create_simulation();

    public native void delete_simulation(long pointer);

    //Structure Calls

    public native void add_hub(long pointer, String hub);

    public native void add_station(long pointer, String name, char type, int max_number_of_trains, int random_on_range_low, int random_on_range_high, int random_off_range_low, int random_off_range_high, double ticket_price);

    public native void add_rail(long pointer, String pointA, String pointB, int distance, int startTime, int endTime);

    public native void add_train(long pointer, String name, String hub, char type);

    public native void set_train_active(long pointer, String name, boolean active);

    public native void set_station_active(long pointer, String name, boolean active);

    public native void set_hub_active(long pointer, String name, boolean active);

    public native void set_rail_active(long pointer, String name1,String name2, boolean active);

    //Maintenance


    public native void add_maintenance_edge(long pointer, int day, String stop1, String stop2, int days_down);

    public native void add_maintenance_train(long pointer, int day, String train, int days_down);

    //Configuration

    public native void configure_train(long pointer, char type, int fuel_capacity, int fuel_cost, int speed, int capacity);

    public native void configure(long pointer, int crews, int fuel, int days_to_run);

    public native void configure_weather(long pointer, int type, float severity);

    //Routes

    public native void repeatable_route(long pointer,char type);

    public native void add_repeatable_freight_route_stop(long pointer, String stop1, String stop2,char type, int required_start_time, int capacity);

    public native void add_repeatable_passenger_route_stop(long pointer, String stop, int arrival_time);

    public native void daily_route(long pointer,int day);

    public native void add_daily_freight_route_stop(long pointer, String stop1, String stop2, int required_start_time, int capacity);

    public native void add_daily_passenger_route_stop(long pointer, String stop, int arrivalTime);

    public native void reset(long pointer);

    public native String start(long pointer, String output_directory);

}
