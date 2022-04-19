package com.ksp.subitesv.proveedores;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProveedorGeoFire {
    private DatabaseReference mDatabase;
    private GeoFire mGeoFire;

    public ProveedorGeoFire(String referencia) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child(referencia);
        mGeoFire = new GeoFire(mDatabase);
    }

    public void guardarUbicacion(String idConductor, LatLng latLng){

        mGeoFire.setLocation(idConductor, new GeoLocation(latLng.latitude, latLng.longitude));
    }

    public void removerUbicacion(String idConductor){
        mGeoFire.removeLocation(idConductor);
    }

    public GeoQuery obtenerConductoresActivos(LatLng latLng, double radius){
        GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radius);
        geoQuery.removeAllListeners();
        return geoQuery;
    }

    public DatabaseReference obtenerUbicacionConductor(String conductorId){
        return mDatabase.child(conductorId).child("l");
    }

    public DatabaseReference isConductorTrabajando(String idConductor){
        return FirebaseDatabase.getInstance().getReference().child("conductores_trabajando").child(idConductor);
    }


}
