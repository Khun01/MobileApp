package com.example.bagouli

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.example.bagouli.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentMapBinding.inflate(inflater,container,false)

        binding.cd1.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setHasOptionsMenu(true)

        binding.cd2.setOnClickListener {
            showPopupMenu(it)
        }

        return binding.root
    }

    private fun showPopupMenu(anchorView: View){
        val popupMenu = PopupMenu(context, anchorView)
        popupMenu.menuInflater.inflate(R.menu.map_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }
        popupMenu.show()
    }

    private fun changeMap(itemId : Int){
        when(itemId){
            R.id.normalMap -> mGoogleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybridMap -> mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satelliteMap -> mGoogleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrainMap -> mGoogleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        val lat = 16.0433
        val lng = 120.3333

        val placeLatLng = LatLng(lat, lng)
        mGoogleMap.addMarker(
            MarkerOptions()
                .position(placeLatLng)
                .title("Dagupan City, Pangasinan")
        )

        zoomToLocation(lat, lng, 15f)
    }

    private fun zoomToLocation(latitude: Double, longitude: Double, zoomLevel: Float){
        val location = LatLng(latitude, longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, zoomLevel)
        mGoogleMap.animateCamera(cameraUpdate)
    }
}