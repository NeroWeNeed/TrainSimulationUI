package org.nwn.ts.simulation;

public class TrainBridge {


    public native void hub(String name);

    public native void station(String name, char type, int maxNumberOfTrains, int randomOnRange, int randomOffRange);

    public native void rail(String pointA,String pointB,int distance,int startTime,int endTime);

    public native void train(String name,String hub,char type);



    public native void reset();

    public native String start(String outputDirectory);

}
