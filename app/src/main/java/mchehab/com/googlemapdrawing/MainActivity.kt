package mchehab.com.googlemapdrawing

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {

    private val listLatLng = mutableListOf<LatLng>()

    private lateinit var googleMap: GoogleMap
    private val listMarkers = mutableListOf<Marker>()

    private var polyline: Polyline? = null
    private var polygon: Polygon? = null

    private val beirutLatLng = LatLng(33.893865, 35.501175)
    private val beirutMarker = MarkerOptions().position(beirutLatLng)
    private val transparentBlue = 0x300000FF

    val SEEKBAR_STROKE_WIDTH_PROGRESS = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
//        initial default value
        seekBarStrokeWidth.progress = SEEKBAR_STROKE_WIDTH_PROGRESS

        setButtonPolylineListener()
        setButtonPolygonListener()
        setButtonClearListener()
        setupSeekBars()
        setupCheckBox()
    }

    private fun setButtonPolylineListener() {
        buttonPolyline.setOnClickListener { e -> connectPolyline() }
    }

    private fun setButtonPolygonListener() {
        buttonPolygon.setOnClickListener { e -> connectPolygon() }
    }

    private fun setButtonClearListener() {
        buttonReset.setOnClickListener{ e -> resetMap() }
    }

    private fun setupSeekBars() {
        seekBarRed.setOnSeekBarChangeListener(this)
        seekBarGreen.setOnSeekBarChangeListener(this)
        seekBarBlue.setOnSeekBarChangeListener(this)
        setupSeekBarStroke()
    }

    private fun setupSeekBarStroke() {
        seekBarStrokeWidth.progress = 5
        seekBarStrokeWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                setStrokeWidth()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setupCheckBox() {
        checkBoxPolygonFill.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                fillPolygon()
            } else {
                unFillPolygon()
            }
        }
    }

    private fun fillPolygon(){
        polygon?.fillColor = getColor()
    }

    private fun unFillPolygon(){
        polygon?.fillColor = Color.parseColor("#00000000")
    }

    private fun setColor(){
        val color = getColor()
        polyline?.color = color
        polygon?.strokeColor = color
    }

    private fun getColor(): Int {
        val red = seekBarRed.progress
        val green = seekBarGreen.progress
        val blue = seekBarBlue.progress
        return Color.rgb(red, green, blue)
    }

    private fun setStrokeWidth(){
        polygon?.strokeWidth = seekBarStrokeWidth.progress.toFloat()
        polyline?.width = seekBarStrokeWidth.progress.toFloat()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        setColor()
        if (checkBoxPolygonFill.isChecked){
            fillPolygon()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        addCircle()
        googleMap.addMarker(beirutMarker)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(beirutLatLng, 17.0f))
        setGoogleMapClickListener()
        setCircleClickListener()
        setPolylineClickListener()
        setPolygonClickListener()
    }

    private fun resetMap() {
        polyline?.remove()
        polygon?.remove()
        listMarkers.forEach { it.remove() }
        listMarkers.clear()
        listLatLng.clear()
    }

    private fun addCircle() {
        val circle = CircleOptions()
                .center(beirutLatLng)
                .radius(50.0)
                .strokeColor(Color.RED)
                .fillColor(transparentBlue)
                .clickable(true)
        googleMap.addCircle(circle)
    }

    private fun setGoogleMapClickListener() {
        googleMap.setOnMapClickListener { e ->
            val markerOptions = MarkerOptions().position(e)
            val marker = googleMap.addMarker(markerOptions)
            listMarkers.add(marker)
            listLatLng.add(e)
        }
    }

    private fun setCircleClickListener() {
        googleMap.setOnCircleClickListener { e -> Snackbar.make(frameLayout, "Circle clicked!", Snackbar.LENGTH_SHORT).show() }
    }

    private fun setPolylineClickListener() {
        googleMap.setOnPolylineClickListener { e -> Snackbar.make(frameLayout, "Polyline clicked!", Snackbar.LENGTH_SHORT).show() }
    }

    private fun setPolygonClickListener() {
        googleMap.setOnPolygonClickListener { e -> Snackbar.make(frameLayout, "Polygon clicked!", Snackbar.LENGTH_SHORT).show() }
    }

    private fun connectPolyline() {
        polyline?.remove()
        val polylineOptions = PolylineOptions().addAll(listLatLng).clickable(true)
        polyline = googleMap.addPolyline(polylineOptions)
        setColor()
        setStrokeWidth()
    }

    private fun connectPolygon() {
        polygon?.remove()
        val polygonOptions = PolygonOptions().addAll(listLatLng).clickable(true)
        polygon = googleMap.addPolygon(polygonOptions)
        setColor()
        setStrokeWidth()
    }
}