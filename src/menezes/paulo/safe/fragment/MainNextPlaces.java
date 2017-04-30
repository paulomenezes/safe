package menezes.paulo.safe.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import menezes.paulo.safe.PlaceActivity;
import menezes.paulo.safe.R;
import menezes.paulo.safe.data.Connect;
import menezes.paulo.safe.entity.Place;
import menezes.paulo.safe.entity.Report;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class MainNextPlaces extends SherlockFragment implements OnInfoWindowClickListener {
	private GoogleMap mGoogleMap;
	private MobileServiceClient mClient;
	
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Place> mMarkers = new HashMap<Integer, Place>();
    SupportMapFragment supportMapFragment;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_next_places, container, false);

        FragmentManager myFragmentManager = getActivity().getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);

		mGoogleMap = getMapFragment().getMap(); //mySupportMapFragment.getMap();

		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.setOnInfoWindowClickListener(this);
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

	    if (location != null) {
	    	mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(14).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	    }

		mClient = Connect.getInstance();
	    mClient.getTable(Place.class).execute(new TableQueryCallback<Place>() {
			@Override
			public void onCompleted(final List<Place> places, int i, Exception e, ServiceFilterResponse response) {
				if(e == null && places.size() > 0) {
					mClient.getTable(Report.class).execute(new TableQueryCallback<Report>() {
						@Override
						public void onCompleted(List<Report> reports, int j, Exception ex, ServiceFilterResponse filter) {
							if(reports != null) {
								final List<Pair<Integer, Place>> alertPlaces = new ArrayList<Pair<Integer,Place>>();

								for (int k = 0; k < places.size(); k++) {
									int place = 0;
									for (int l = 0; l < reports.size(); l++) {
										if(places.get(k).id == reports.get(l).idPlace) {
											place++;
										}
									}

									alertPlaces.add(new Pair<Integer, Place>(place, places.get(k)));
								}

								for (int k = 0; k < alertPlaces.size(); k++) {
									if (alertPlaces.get(k).second.address != null) {
										if(getLocationFromAddress(alertPlaces.get(k).second.address + ", " + alertPlaces.get(k).second.city) != null) {
											MarkerOptions marker = new MarkerOptions();
											marker.position(getLocationFromAddress(alertPlaces.get(k).second.address + ", " + alertPlaces.get(k).second.city));
											marker.title(alertPlaces.get(k).second.id + " - " + alertPlaces.get(k).second.name);
											marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue));

											if(alertPlaces.get(k).first == 0)
												marker.snippet("Nenhuma denúncia");
											else if(alertPlaces.get(k).first == 1)
												marker.snippet("1 denúncia");
											else
												marker.snippet(alertPlaces.get(k).first + " denúncias");

											mGoogleMap.addMarker(marker);
											mMarkers.put(alertPlaces.get(k).second.id, alertPlaces.get(k).second);
										}
									}
								}
							}
						}
					});
				}
			}
		});
		
		return view;
	}
	
	public LatLng getLocationFromAddress(String strAddress) {
        if (getActivity() != null) {
            Geocoder coder = new Geocoder(getActivity());
            List<Address> address;
            LatLng latLng = null;

            try {
                address = coder.getFromLocationName(strAddress, 5);

                if (address == null) {
                    return null;
                }

                if (address.size() > 0) {
                    Address location = address.get(0);
                    location.getLatitude();
                    location.getLongitude();

                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return latLng;
        } else {
            return  null;
        }
	}

	private SupportMapFragment getMapFragment() {
		FragmentManager fm = null;

		if (Build.VERSION.SDK_INT < 21) {
			fm = getFragmentManager();
		} else {
			fm = getChildFragmentManager();
		}

		return (SupportMapFragment) fm.findFragmentById(R.id.map);
	}

	public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment supportMapFragment = getMapFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(supportMapFragment);
        ft.commit();
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		if (mMarkers.containsKey(Integer.parseInt(marker.getTitle().split(" - ")[0]))) {
			Intent main = new Intent(getActivity(), PlaceActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("place", mMarkers.get(Integer.parseInt(marker.getTitle().split(" - ")[0])));
			main.putExtras(b);
			
			startActivity(main);
		}
	}
}