package org.nwn.ts.util;

import org.nwn.ts.stats.TrainType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    public enum WeatherType {
        SUN, RAIN, SNOW;
    }


    private Float weatherSeverity = 0.0f;
    private WeatherType weatherType = WeatherType.SUN;
    private boolean collisionAvoidance = false;
    private int duration = 0;
    private int transportCost = 0;
    private int totalCrews = 0;
    private int hubFuelCapacity = 0;
    private int runDuration = 14;
    private Map<TrainType, TrainConfiguration> trainConfigurations = new HashMap<>();

    private File layoutFile;
    private File maintenanceFile;
    private File configurationFile;
    private File dailyRoutesFile;
    private File repeatableRoutesFile;

    public Float getWeatherSeverity() {
        return weatherSeverity;
    }

    public void setWeatherSeverity(Float weatherSeverity) {
        this.weatherSeverity = weatherSeverity;
    }

    public WeatherType getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(WeatherType weatherType) {
        this.weatherType = weatherType;
    }

    public boolean isCollisionAvoidance() {
        return collisionAvoidance;
    }

    public void setCollisionAvoidance(boolean collisionAvoidance) {
        this.collisionAvoidance = collisionAvoidance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(int transportCost) {
        this.transportCost = transportCost;
    }

    public File getLayoutFile() {
        return layoutFile;
    }

    public void setLayoutFile(File layoutFile) {
        this.layoutFile = layoutFile;
    }

    public File getMaintenanceFile() {
        return maintenanceFile;
    }

    public void setMaintenanceFile(File maintenanceFile) {
        this.maintenanceFile = maintenanceFile;
    }

    public File getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    public File getDailyRoutesFile() {
        return dailyRoutesFile;
    }

    public void setDailyRoutesFile(File dailyRoutesFile) {
        this.dailyRoutesFile = dailyRoutesFile;
    }

    public File getRepeatableRoutesFile() {
        return repeatableRoutesFile;
    }

    public void setRepeatableRoutesFile(File repeatableRoutesFile) {
        this.repeatableRoutesFile = repeatableRoutesFile;
    }

    public Map<TrainType, TrainConfiguration> getTrainConfigurations() {
        return trainConfigurations;
    }

    public int getTotalCrews() {
        return totalCrews;
    }

    public void setTotalCrews(int totalCrews) {
        this.totalCrews = totalCrews;
    }

    public int getHubFuelCapacity() {
        return hubFuelCapacity;
    }

    public void setHubFuelCapacity(int hubFuelCapacity) {
        this.hubFuelCapacity = hubFuelCapacity;
    }

    public int getRunDuration() {
        return runDuration;
    }

    public void setRunDuration(int runDuration) {
        this.runDuration = runDuration;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "weatherSeverity=" + weatherSeverity +
                ", weatherType=" + weatherType +
                ", collisionAvoidance=" + collisionAvoidance +
                ", duration=" + duration +
                ", transportCost=" + transportCost +
                ", layoutFile=" + layoutFile +
                ", maintenanceFile=" + maintenanceFile +
                ", configurationFile=" + configurationFile +
                ", dailyRoutesFile=" + dailyRoutesFile +
                ", repeatableRoutesFile=" + repeatableRoutesFile +
                '}';
    }
}
