package org.nwn.ts;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.nwn.ts.util.Configuration;

public class Model {
    private static Model INSTANCE = new Model();

    public static Model getInstance() {
        return INSTANCE;
    }

    private Model() {

    }

    private BooleanProperty baselineSet = new SimpleBooleanProperty(false);
    private ObjectProperty<Configuration> configuration = new SimpleObjectProperty<>(new Configuration());


    public boolean getBaselineSet() {
        return baselineSet.get();
    }

    public BooleanProperty baselineSetProperty() {
        return baselineSet;
    }

    public void setBaselineSet(boolean baselineSet) {
        this.baselineSet.set(baselineSet);
    }

    public Configuration getConfiguration() {
        return configuration.get();
    }

    public ObjectProperty<Configuration> configurationProperty() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration.set(configuration);
    }
}
