package com.toto.travelmantotoproject.base;

import android.app.Application;

import com.toto.travelmantotoproject.activity.MapsActivity;

import dagger.Component;


// Definition of the Application graph
@Component
interface ApplicationComponent {
    void inject(MapsActivity mapsActivity);
}
public class MetroApplication extends Application {

    // Reference to the application graph that is used across the whole app
    ApplicationComponent appComponent = DaggerApplicationComponent.create();
}
