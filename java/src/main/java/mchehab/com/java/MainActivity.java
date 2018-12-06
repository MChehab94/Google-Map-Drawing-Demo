package mchehab.com.java;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {

    private FrameLayout frameLayout;

    private GoogleMap googleMap;
    private Polyline polyline = null;
    private Polygon polygon = null;

    private Button buttonPolyline;
    private Button buttonPolygon;
    private Button buttonClear;

    private SeekBar seekBarRed;
    private SeekBar seekBarGreen;
    private SeekBar seekBarBlue;
    private SeekBar seekBarStrokeWidth;

    private CheckBox checkBoxPolygonFill;

    private final LatLng beirutLatLng = new LatLng(33.893865, 35.501175);
    private final MarkerOptions beirutMarker = new MarkerOptions().position(beirutLatLng);
    private final int transparentBlue = 0x300000FF;

    private List<LatLng> listLatLngs = new ArrayList<>();
    private List<Marker> listMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googleMap);
        supportMapFragment.getMapAsync(this);

        setupUI();
        setButtonPolylineListener();
        setButtonPolygonListener();
        setButtonClearListener();
        setupSeekBars();
        setupCheckBox();
    }

    private void setupUI(){
        frameLayout = findViewById(R.id.frameLayout);
        buttonPolyline = findViewById(R.id.buttonPolyline);
        buttonPolygon = findViewById(R.id.buttonPolygon);
        buttonClear = findViewById(R.id.buttonReset);

        seekBarRed = findViewById(R.id.seekBarRed);
        seekBarGreen = findViewById(R.id.seekBarGreen);
        seekBarBlue = findViewById(R.id.seekBarBlue);
        seekBarStrokeWidth = findViewById(R.id.seekBarStrokeWidth);

        checkBoxPolygonFill = findViewById(R.id.checkBoxPolygonFill);
    }

    private void setButtonPolylineListener() {
        buttonPolyline.setOnClickListener(e -> connectPolyline());
        buttonPolygon.setOnClickListener(e -> connectPolygon());
        buttonClear.setOnClickListener(e -> resetMap());
    }

    private void setButtonPolygonListener() {
        buttonPolygon.setOnClickListener(e -> connectPolygon());
    }

    private void setButtonClearListener() {
        buttonClear.setOnClickListener(e -> resetMap());
    }

    private void setupSeekBars() {
        seekBarRed.setOnSeekBarChangeListener(this);
        seekBarGreen.setOnSeekBarChangeListener(this);
        seekBarBlue.setOnSeekBarChangeListener(this);
        setupSeekBarStroke();
    }

    private void setupSeekBarStroke(){
        seekBarStrokeWidth.setProgress(5);
        seekBarStrokeWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setStrokeWidth();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void setupCheckBox() {
        checkBoxPolygonFill.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                fillPolygon();
            }else{
                unFillPolygon();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        addCircle();
        googleMap.addMarker(beirutMarker);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(beirutLatLng, 17.0f));
        setGoogleMapClickListener();
        setCircleClickListener();
        setPolylineClickListener();
        setPolygonClickListener();
    }

    private void addCircle() {
        CircleOptions circle = new CircleOptions()
                .center(beirutLatLng)
                .radius(50.0)
                .strokeColor(Color.RED)
                .fillColor(transparentBlue)
                .clickable(true);
        googleMap.addCircle(circle);
    }

    private void setGoogleMapClickListener(){
        googleMap.setOnMapClickListener(e -> {
            MarkerOptions markerOptions = new MarkerOptions().position(e);
            Marker marker = googleMap.addMarker(markerOptions);
            listMarkers.add(marker);
            listLatLngs.add(e);
        });
    }

    private void setCircleClickListener(){
        googleMap.setOnCircleClickListener(e ->{
            Snackbar.make(frameLayout, "Circle clicked!", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void setPolylineClickListener(){
        googleMap.setOnPolylineClickListener(e -> {
            Snackbar.make(frameLayout, "Polyline clicked!", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void setPolygonClickListener(){
        googleMap.setOnPolygonClickListener(e -> {
            Snackbar.make(frameLayout, "Polygon clicked!", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void connectPolyline(){
        if (polyline != null)
            polyline.remove();
        PolylineOptions polylineOptions = new PolylineOptions().addAll(listLatLngs).clickable(true);
        polyline = googleMap.addPolyline(polylineOptions);
        setColor();
        setStrokeWidth();
    }

    private void connectPolygon(){
        if (polygon != null)
            polygon.remove();
        PolygonOptions polygonOptions = new PolygonOptions().addAll(listLatLngs).clickable(true);
        polygon = googleMap.addPolygon(polygonOptions);
        setColor();
        setStrokeWidth();
    }

    private void resetMap(){
        if (polyline != null) polyline.remove();
        if (polygon != null) polygon.remove();

        for (Marker marker : listMarkers) marker.remove();
        listMarkers.clear();
        listLatLngs.clear();
    }

    private int getColor(){
        int red = seekBarRed.getProgress();
        int green = seekBarGreen.getProgress();
        int blue = seekBarBlue.getProgress();
        return Color.rgb(red, green, blue);
    }

    private void setColor(){
        int color = getColor();
        if (polyline != null)
            polyline.setColor(color);
        if (polygon != null) {
            polygon.setStrokeColor(color);
            if (checkBoxPolygonFill.isChecked())
                polygon.setFillColor(color);
        }
    }

    private void setStrokeWidth(){
        int width = seekBarStrokeWidth.getProgress();
        if (polyline != null)
            polyline.setWidth(width);
        if (polygon != null)
            polygon.setStrokeWidth(width);
    }

    private void fillPolygon(){
        if (polygon == null)
            return;
        int color = getColor();
        polygon.setFillColor(color);
    }

    private void unFillPolygon(){
        polygon.setFillColor(Color.parseColor("#00000000"));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setColor();
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
}
