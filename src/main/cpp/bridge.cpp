#include "bridge.h"
#include "details.h"
#include <cstdlib>


simulationDetails* getDetails(long pointer) {
     return reinterpret_cast<simulationDetails*>(pointer);
}
loadType getType(char c) {
     switch (c) {
     case 'P':
     case 'p':
          return PASSENGER;
     case 'F':
     case 'f':
          return FREIGHT;
     default:
          return UNDEF;
     }

}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    create_simulation
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_nwn_ts_simulation_TrainBridge_create_1simulation
(JNIEnv * env, jobject obj) {

     simulationDetails* details = new simulationDetails();

     return (long)details;
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    delete_simulation
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_delete_1simulation
(JNIEnv * env, jobject obj, jlong pointer) {
     simulationDetails* details = getDetails(pointer);
     delete details;
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_hub
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1hub
(JNIEnv * env, jobject obj, jlong pointer, jstring hub) {
     simulationDetails* details = getDetails(pointer);
     details->nodes.push_back(nodeDetails{ true, env->GetStringUTFChars(hub, 0) });
}


/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_station
 * Signature: (JLjava/lang/String;CIIIIID)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1station
(JNIEnv *env, jobject obj, jlong pointer, jstring station, jchar type, jint maxNumberOfTrains, jint randomOnRangeLow, jint randomOnRangeHigh, jint randomOffRangeLow, jint randomOffRangeHigh, jdouble ticketPrice) {
     simulationDetails* details = getDetails(pointer);
     details->nodes.push_back(nodeDetails{ false, env->GetStringUTFChars(station, 0),getType(type),maxNumberOfTrains,randomOnRangeLow,randomOnRangeHigh,randomOffRangeLow,randomOffRangeHigh,ticketPrice });
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_rail
 * Signature: (JLjava/lang/String;Ljava/lang/String;III)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1rail
(JNIEnv * env, jobject obj, jlong pointer, jstring name1, jstring name2, jint distance, jint startTime, jint endTime) {
     simulationDetails* details = getDetails(pointer);
     details->edges.push_back(edgeDetails{ env->GetStringUTFChars(name1, 0),env->GetStringUTFChars(name2, 0),distance,startTime,endTime });

}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_train
 * Signature: (JLjava/lang/String;Ljava/lang/String;C)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1train
(JNIEnv * env, jobject obj, jlong pointer, jstring name, jstring hub, jchar type) {
     simulationDetails* details = getDetails(pointer);
     details->trains.push_back(trainDetails{ env->GetStringUTFChars(name, 0),env->GetStringUTFChars(hub, 0),getType(type) });

}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    set_train_active
 * Signature: (JLjava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_set_1train_1active
(JNIEnv * env, jobject obj, jlong pointer, jstring jname, jboolean state) {

     simulationDetails* details = getDetails(pointer);
     string name = env->GetStringUTFChars(jname, 0);
     for (int i = 0; i < details->trains.size(); i++) {
          if (details->trains.at(i).name == name) {
               details->trains.at(i).disabled = state;
               break;
          }
     }

}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    set_station_active
 * Signature: (JLjava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_set_1station_1active
(JNIEnv * env, jobject obj, jlong pointer, jstring jname, jboolean state) {
     simulationDetails* details = getDetails(pointer);
     string name = env->GetStringUTFChars(jname, 0);
     for (int i = 0; i < details->nodes.size(); i++) {
          if (details->nodes.at(i).name == name && !details->nodes.at(i).isHub) {
               details->nodes.at(i).disabled = state;
               break;
          }
     }
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    set_hub_active
 * Signature: (JLjava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_set_1hub_1active
(JNIEnv *env, jobject obj, jlong pointer, jstring jname, jboolean state) {
     simulationDetails* details = getDetails(pointer);
     string name = env->GetStringUTFChars(jname, 0);
     for (int i = 0; i < details->nodes.size(); i++) {
          if (details->nodes.at(i).name == name && details->nodes.at(i).isHub) {
               details->nodes.at(i).disabled = state;
               break;
          }
     }
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    set_rail_active
 * Signature: (JLjava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_set_1rail_1active
(JNIEnv * env, jobject obj, jlong pointer, jstring jname1, jstring jname2, jboolean state) {
     simulationDetails* details = getDetails(pointer);
     string name1 = env->GetStringUTFChars(jname1, 0);
     string name2 = env->GetStringUTFChars(jname2, 0);
     for (int i = 0; i < details->edges.size(); i++) {
          if ((details->edges.at(i).node1 == name1 &&
               details->edges.at(i).node2 == name2) || (details->edges.at(i).node1 == name2 &&
                    details->edges.at(i).node2 == name1)
               ) {
               details->edges.at(i).disabled = state;
               break;
          }
     }
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_maintenance_edge
 * Signature: (JILjava/lang/String;Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1maintenance_1edge
(JNIEnv * env, jobject obj, jlong pointer, jint day, jstring stop1, jstring stop2, jint daysDown) {
     simulationDetails* details = getDetails(pointer);
     details->edgeMaintenance.push_back(edgeMaintenanceDetails{ day,env->GetStringUTFChars(stop1, 0),env->GetStringUTFChars(stop2, 0),daysDown });
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_maintenance_train
 * Signature: (JILjava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1maintenance_1train
(JNIEnv * env, jobject obj, jlong pointer, jint day, jstring train, jint daysDown) {
     simulationDetails* details = getDetails(pointer);
     details->trainMaintenance.push_back(trainMaintenanceDetails{ day,env->GetStringUTFChars(train, 0),daysDown });
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    configure_train
 * Signature: (JCIIII)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_configure_1train
(JNIEnv * env, jobject obj, jlong pointer, jchar type, jint fuelCapacity, jint fuelCost, jint speed, jint capacity) {
     simulationDetails* details = getDetails(pointer);
     switch (type) {
     case PASSENGER:
          details->configuration.passengerTrainParameters.fuelCapacity = fuelCapacity;
          details->configuration.passengerTrainParameters.fuelCost = fuelCost;
          details->configuration.passengerTrainParameters.speed = speed;
          details->configuration.passengerTrainParameters.capacity = capacity;
          break;
     case FREIGHT:
          details->configuration.freightTrainParameters.fuelCapacity = fuelCapacity;
          details->configuration.freightTrainParameters.fuelCost = fuelCost;
          details->configuration.freightTrainParameters.speed = speed;
          details->configuration.freightTrainParameters.capacity = capacity;
          break;
     }
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    configure
 * Signature: (JIII)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_configure
(JNIEnv * env, jobject obj, jlong pointer, jint crews, jint fuel, jint daysToRun) {
     simulationDetails* details = getDetails(pointer);
     details->configuration.hubFuel = fuel;
     details->configuration.duration = daysToRun;
     details->configuration.crewsPerHub = crews;
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    configure_weather
 * Signature: (JIF)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_configure_1weather
(JNIEnv * env, jobject obj, jlong pointer, jint type, jfloat severity) {
     simulationDetails* details = getDetails(pointer);
     details->configuration.weatherType = type;
     details->configuration.weatherSeverity = severity;
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    repeatable_route
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_repeatable_1route
(JNIEnv *, jobject, jlong pointer) {
     simulationDetails* details = getDetails(pointer);
     if (details->currentRepeatableRoute.stations.size() > 0) {
          details->repeatingPassengerRoutes.push_back(details->currentRepeatableRoute);
     }
     details->currentRepeatableRoute = dailyPassengerRouteDetails{ -1 };
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_repeatable_freight_route_stop
 * Signature: (JLjava/lang/String;Ljava/lang/String;II)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1repeatable_1freight_1route_1stop
(JNIEnv * env, jobject obj, jlong pointer, jstring stop1, jstring stop2, jchar type, jint requiredStart, jint capacity) {
     simulationDetails* details = getDetails(pointer);
     string name1 = env->GetStringUTFChars(stop1, 0);
     string name2 = env->GetStringUTFChars(stop2, 0);
     day t;
     bool add = true;
     switch (type) {
     case 'W':
     case 'w':
          t = WKD;
          break;
     case 'F':
     case 'f':
          t = ALL;
          break;
     default:
          add = false;
          break;
     }
     if (add)
          details->repeatingFreightRoutes.push_back(freightDetails{ name1,name2,t,requiredStart,capacity });


}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_repeatable_passenger_route_stop
 * Signature: (JLjava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1repeatable_1passenger_1route_1stop
(JNIEnv *env , jobject, jlong pointer, jstring station, jint time) {
     simulationDetails* details = getDetails(pointer);
     string name = env->GetStringUTFChars(station, 0);
     details->currentRepeatableRoute.stations.push_back(routeDetails{ name,time });

}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    daily_route
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_daily_1route
(JNIEnv *, jobject, jlong pointer, jint day) {
     simulationDetails* details = getDetails(pointer);
     if (details->currentDailyFreightRoute.stations.size() > 0) {
          details->dailyFreightRoutes.push_back(details->currentDailyFreightRoute);
     }
     if (details->currentDailyPassengerRoute.stations.size() > 0) {
          details->dailyPassengerRoutes.push_back(details->currentDailyPassengerRoute);
     }
     details->currentDailyFreightRoute = dailyFreightRouteDetails{ day };
     details->currentDailyPassengerRoute = dailyPassengerRouteDetails{ day };

}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_daily_freight_route_stop
 * Signature: (JLjava/lang/String;Ljava/lang/String;II)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1daily_1freight_1route_1stop
(JNIEnv * env, jobject, jlong pointer, jstring stop1, jstring stop2, jint startTime, jint capacity) {
     simulationDetails* details = getDetails(pointer);
     string name1 = env->GetStringUTFChars(stop1, 0);
     string name2 = env->GetStringUTFChars(stop2, 0);
     details->currentDailyFreightRoute.stations.push_back(freightDetails{ name1,name2,NONE,startTime,capacity });
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    add_daily_passenger_route_stop
 * Signature: (JLjava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_add_1daily_1passenger_1route_1stop
(JNIEnv * env, jobject obj, jlong pointer, jstring name, jint time) {
     simulationDetails* details = getDetails(pointer);
     string name1 = env->GetStringUTFChars(name, 0);
     details->currentDailyPassengerRoute.stations.push_back(routeDetails{ name1,time });

}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    reset
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_nwn_ts_simulation_TrainBridge_reset
(JNIEnv * env, jobject obj, jlong pointer) {
     simulationDetails* details = getDetails(pointer);
     details = new simulationDetails();
}

/*
 * Class:     org_nwn_ts_simulation_TrainBridge
 * Method:    start
 * Signature: (JLjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_nwn_ts_simulation_TrainBridge_start
(JNIEnv * env, jobject obj, jlong pointer, jstring outputDir) {
     simulationDetails* details = getDetails(pointer);
     string outputDir = env->GetStringUTFChars(outputDir, 0);
     //interface here using details, return string to output file.

}
