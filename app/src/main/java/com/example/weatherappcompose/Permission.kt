package com.example.weatherappcompose

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class Permission {

    companion object {
        fun checkPermission(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun getCurrentLocation(context: Context, callback: (Double, Double) -> Unit) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val lat = location.latitude
                        val long = location.longitude
                        callback(lat, long)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle location retrieval failure
                    exception.printStackTrace()
                }
        }
    }
}


@Composable
fun CheckPermission(location: MutableState<String>, context: Context) {
    Log.d("MyLog", "CHECK PERM")

    if (Permission.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
        Permission.getCurrentLocation(context) { lat, long ->
            //location = "Latitude: $lat, Longitude: $long"
            location.value = "$lat,$long"
        }
    } else {
        PermissionRequest(location, context)
    }
}


@Composable
fun PermissionRequest(location: MutableState<String>, context: Context) {
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("MyLog", "isGranted")
                    // Permission granted, update the location
                    Permission.getCurrentLocation(context) { lat, long ->
                        //location = "Latitude: $lat, Longitude: $long"
                        location.value = "$lat,$long"
                    }
                } else {
                    Log.d("MyLog", "notIsGranted")
                }
            })

    SideEffect {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}