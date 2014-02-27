/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package codigo.labplc.mx.mitaxichofer.trip.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import codigo.labplc.mx.mitaxichofer.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Defines app-wide constants and utilities
 */
public final class LocationUtils {

    // Debugging tag for the application
    public static final String APPTAG = "Location";

    // Name of shared preferences repository that stores persistent state
    public static final String SHARED_PREFERENCES =
            "codigo.labplc.mx.mitaxi.trip.location.SHARED_PREFERENCES";

    // Key for storing the "updates requested" flag in shared preferences
    public static final String KEY_UPDATES_REQUESTED =
            "codigo.labplc.mx.mitaxi.trip.location.KEY_UPDATES_REQUESTED";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Constants for location update parameters
     */
    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    public static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

    // Create an empty string for initializing strings
    public static final String EMPTY_STRING = new String();

    /**
     * Get the latitude and longitude from the Location object returned by
     * Location Services.
     *
     * @param currentLocation A Location object containing the current location
     * @return The latitude and longitude of the current location, or null if no
     * location is available.
     */
    public static String getLatLng(Context context, Location currentLocation) {
        // If the location is valid
        if (currentLocation != null) {

            // Return the latitude and longitude as strings
            return context.getString(
                    R.string.latitude_longitude,
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude());
        } else {

            // Otherwise, return the empty string
            return EMPTY_STRING;
        }
    }
    
    /**
     * Convert a Location to LatLng format
     * 
     * @param location
     * @return an object in LatLng format
     */
    public static LatLng getLatLngFromLocation(Location location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}
	
    /**
     * Convert a LatLng to Location format
     * 
     * @param latLng
     * @return an object in Location format
     */
	public static Location getLocationFromLatLng(LatLng latLng) {
		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);
		
		return location;
	}
	
	/**
	 * Add a marker into the map
	 * 
	 * @param map
	 * @param point
	 * @param title
	 * @param dragable
	 * @param snippet
	 * @param icon
	 * @return a marker added into the map
	 */
	public static Marker addMarker(GoogleMap map, LatLng point, String title, boolean dragable, String snippet, BitmapDescriptor icon) {
		Marker marker = map.addMarker(new MarkerOptions()
				.position(point)
				.title(title)
				.draggable(dragable)
				.icon(icon)
				.snippet(snippet)
				.icon(icon).flat(false)
				//.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)).flat(true)
				//.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
				.alpha(0.7f)
				.anchor(0.5F, 0.5F)
				.rotation(0.0F));

		marker.showInfoWindow();
		
		return marker;
	}
	
	/**
	 * Center the map into a current position
	 * 
	 * @param map
	 * @param location
	 */
	public static void centerInMyCurrentUserLocation(GoogleMap map, LatLng latlng) {
	    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, map.getMaxZoomLevel() - 4));
	}
}
