package com.leaf.gelo;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.gelo.gelosdk.GeLoBeaconManager;
import com.gelo.gelosdk.Model.Beacons.GeLoBeacon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class LocationActivity extends Activity {
    GeLoBeaconManager manager;
    ArrayList<GeLoBeacon> beacons;
    private int[] coord = new int[2];
   // private int[] oldCoord = new int[2];
  //  private static int index = 0;
	private static final int shiftx = 0;
	private static final int shifty = 305;	
    private static int dist1 = 0;
    private static int dist2 = 0;
    private static int dist3 = 0;
    private static int dist4 = 0;
    private static int x1=0,x2=0,y1=0,y2=0,x3=0,y3=0,x4=181,y4=232;
    private static final int xlimit = 190;
    private static final int xMAX = xlimit + 1;
    private static final int ylimit = 305;
    private static final int yMAX = ylimit + 1;

    
    Context context;
    int ind = 0;
    String stringX;
    String stringY;
    String distanceA;
    String distanceB;
    String distanceC;
    Canvas canvas;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
        x1 = 0;
        y1 = 0;
        x2 = 160;
        y2 = 0;
        x3 = 112;
        y3 = 295; 
        x4 = 181;
        y4 = 232;
        
        manager = GeLoBeaconManager.sharedInstance(getApplicationContext());
        manager.startScanningForBeacons();
        beacons = manager.getKnownTourBeacons();       
		if(beacons.size()>=4){
			for(int i = 0; i<beacons.size(); i++){
				GeLoBeacon beacon = beacons.get(i);
                double sum1 = 0;
                double rssi1[] = new double[6];
                double sum2 = 0;
                double rssi2[] = new double[6];
                double sum3 = 0;
                double rssi3[] = new double[6];
                double sum4 = 0;
                double rssi4[] = new double[6];
                int counter1 = 0;
                int counter2 = 0;
                int counter3 = 0;
                int counter4 = 0;
                // Running average of 5 samples.
                if(beacon.getBeaconId()==107)
                {
                    double strength = (double) beacon.getSignalStregth();
                    rssi1[counter1] = strength; //  double distance = 0.0271*Math.exp(-0.121*strength);    Old equation.                    
                    if(counter1==5){
                    	rssi1[0] = rssi1[1];
                    	rssi1[1] = rssi1[2];
                    	rssi1[2] = rssi1[3];
                    	rssi1[3] = rssi1[4];
                    	rssi1[4] = rssi1[5];                                	
                    	sum1 =+ rssi1[5]; // sum1 is now the sum of six values.
                    	sum1 =- rssi1[0]; // delete the oldest value
                        dist1 = Methods.getDistance(sum1/(counter1+1));
                    	counter1 = 4;
                    }
                    else{
                    	sum1 =+ strength;
                        dist1 = Methods.getDistance(sum1/(counter1+1));
                    }
                    counter1++;
                }
                
                if(beacon.getBeaconId()==177)
                {
                    double strength = (double) beacon.getSignalStregth();
                    rssi2[counter2] = strength;
                    if(counter2==5){
                    	rssi2[0] = rssi2[1];
                    	rssi2[1] = rssi2[2];
                    	rssi2[2] = rssi2[3];
                    	rssi2[3] = rssi2[4];
                    	rssi2[4] = rssi2[5];                                	
                    	sum2 =+ rssi2[5]; // sum2 is now the sum of six values.
                    	sum2 =- rssi2[0]; // delete the oldest value
                        dist2 = Methods.getDistance(sum2/(counter2+1));
                    	counter2 = 4;
                    }
                    else{
                    	sum2 =+ strength;
                        dist2 = Methods.getDistance(sum2/(counter2+1));
                    }
                    counter2++;
                }
                
                if(beacon.getBeaconId()==184)
                {
                    double strength = (double) beacon.getSignalStregth();
                    rssi3[counter3] = strength;
                    if(counter3==5){
                    	rssi3[0] = rssi3[1];
                    	rssi3[1] = rssi3[2];
                    	rssi3[2] = rssi3[3];
                    	rssi3[3] = rssi3[4];
                    	rssi3[4] = rssi3[5];                                	
                    	sum3 =+ rssi3[5]; // sum1 is now the sum of six values.
                    	sum3 =- rssi3[0]; // delete the oldest value
                        dist3 = Methods.getDistance(sum3/(counter3+1));
                    	counter3 = 4;
                    }
                    else{
                    	sum3 =+ strength;
                        dist3 = Methods.getDistance(sum3/(counter3+1));
                    }
                    counter3++;
                }
                if(beacon.getBeaconId()==164)
                {

                    double strength = (double) beacon.getSignalStregth();
                    rssi4[counter4] = strength;
                    if(counter4==5){
                    	rssi4[0] = rssi4[1];
                    	rssi4[1] = rssi4[2];
                    	rssi4[2] = rssi4[3];
                    	rssi4[3] = rssi4[4];
                    	rssi4[4] = rssi4[5];                                	
                    	sum4 =+ rssi4[5]; // sum1 is now the sum of six values.
                    	sum4 =- rssi4[0]; // delete the oldest value
                        dist4 = Methods.getDistance(sum4/(counter4+1));
                    	counter4 = 4;
                    }
                    else{
                    	sum4 =+ strength;
                        dist4 = Methods.getDistance(sum4/(counter4+1));
                    }
                    counter4++;               
                }
			}
		}

    	boolean check = true;
        Methods.leastSquare(x1 ,x2 ,x3, x4, y1, y2 ,y3, y4, dist3, dist1, dist2, dist4, xlimit, ylimit, check);   
		// does the following run more than one time??
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateTask(), 0, 1*800); 

	}
	
	public void updateDistance()
	{
		if(beacons.size()>=4)
		{

			for(int i = 0; i<beacons.size(); i++){
				GeLoBeacon beacon = beacons.get(i);
                double sum1 = 0;
                double rssi1[] = new double[8];
                double sum2 = 0;
                double rssi2[] = new double[8];
                double sum3 = 0;
                double rssi3[] = new double[8];
                double sum4 = 0;
                double rssi4[] = new double[8];
                int counter1 = 0;
                int counter2 = 0;
                int counter3 = 0;
                int counter4 = 0;
            //    beacon.getTxPower();
                if(beacon.getBeaconId()==107)
                {
                    double strength = (double) beacon.getSignalStregth();
                    rssi1[counter1] = strength; //  double distance = 0.0271*Math.exp(-0.121*strength);    Old equation.                    
                    if(counter1==7){
                    	rssi1[0] = rssi1[1];
                    	rssi1[1] = rssi1[2];
                    	rssi1[2] = rssi1[3];
                    	rssi1[3] = rssi1[4];
                    	rssi1[4] = rssi1[5];
                    	rssi1[5] = rssi1[6];
                    	rssi1[6] = rssi1[7];
                    	sum1 =+ rssi1[7]; // sum1 is now the sum of six values.
                    	sum1 =- rssi1[0]; // delete the oldest value
                        dist1 = Methods.getDistance(sum1/(counter1+1));
                    	counter1 = 6;
                    }
                    else{
                    	sum1 =+ strength;
                        dist1 = Methods.getDistance(sum1/(counter1+1));
                    }
                    counter1++;
                }
                
                if(beacon.getBeaconId()==177)
                {
                    double strength = (double) beacon.getSignalStregth();
                    rssi2[counter2] = strength;
                    if(counter2==7){
                    	rssi2[0] = rssi2[1];
                    	rssi2[1] = rssi2[2];
                    	rssi2[2] = rssi2[3];
                    	rssi2[3] = rssi2[4];
                    	rssi2[4] = rssi2[5];
                    	rssi2[5] = rssi2[6];
                    	rssi2[6] = rssi2[7];
                    	sum2 =+ rssi2[7]; // sum2 is now the sum of six values.
                    	sum2 =- rssi2[0]; // delete the oldest value
                        dist2 = Methods.getDistance(sum2/(counter2+1));
                    	counter2 = 6;
                    }
                    else{
                    	sum2 =+ strength;
                        dist2 = Methods.getDistance(sum2/(counter2+1));
                    }
                    counter2++;
                }
                
                if(beacon.getBeaconId()==184)
                {
                    double strength = (double) beacon.getSignalStregth();
                    rssi3[counter3] = strength;
                    if(counter3==7){
                    	rssi3[0] = rssi3[1];
                    	rssi3[1] = rssi3[2];
                    	rssi3[2] = rssi3[3];
                    	rssi3[3] = rssi3[4];
                    	rssi3[4] = rssi3[5];
                    	rssi3[5] = rssi3[6];
                    	rssi3[6] = rssi3[7];
                    	sum3 =+ rssi3[7]; // sum1 is now the sum of six values.
                    	sum3 =- rssi3[0]; // delete the oldest value
                        dist3 = Methods.getDistance(sum3/(counter3+1));
                    	counter3 = 6;
                    }
                    else{
                    	sum3 =+ strength;
                        dist3 = Methods.getDistance(sum3/(counter3+1));
                    }
                    counter3++;
                }
                if(beacon.getBeaconId()==164)
                {

                    double strength = (double) beacon.getSignalStregth();
                    rssi4[counter4] = strength;
                    if(counter4==7){
                    	rssi4[0] = rssi4[1];
                    	rssi4[1] = rssi4[2];
                    	rssi4[2] = rssi4[3];
                    	rssi4[3] = rssi4[4];
                    	rssi4[4] = rssi4[5];
                    	rssi4[5] = rssi4[6];
                    	rssi4[6] = rssi4[7];
                    	sum4 =+ rssi4[7]; // sum1 is now the sum of six values.
                    	sum4 =- rssi4[0]; // delete the oldest value
                        dist4 = Methods.getDistance(sum4/(counter4+1));
                    	counter4 = 7;
                    }
                    else{
                    	sum4 =+ strength;
                        dist4 = Methods.getDistance(sum4/(counter4+1));
                    }
                    counter4++;               
                }
			}		
		}
	}
	// Code works fine.
	class UpdateTask extends TimerTask {
	    @Override
	    public  void run() {
	        LocationActivity.this.runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	updateDistance();
                    int coordk[] = new int[2];
                    int	colors[] = new int[xMAX*yMAX];
	            	Paint paint = new Paint();	         
	                if(dist1!=0 && dist2!=0 && dist3!=0 && dist4!=0)
	                {
	                	boolean check = false;
	                	ArrayList<int[]> al = new ArrayList<int[]>();
//	                    drawHeatMap(x1 ,x2 ,x3, x4, y1, y2 ,y3, y4, dist3, dist1, dist2, dist4, 170, 300);
	                    al = Methods.leastSquare(x1 ,x2 ,x3, x4, y1, y2 ,y3, y4, dist3, dist1, dist2, dist4, xlimit, ylimit, check);
	                    coord = al.get(0);
	                    colors = al.get(1);
	        //            Log.i( "pixel value", Integer.toString(colors[50000]));
	                    coordk[0] = coord[0];
	                    coordk[1] = coord[1];
//	                    if(index >= 1)
//	                    {
//	                    	coord = Methods.speedCheck(coord, oldCoord);
////	                    }
////		                oldCoord = coord;
////		                if(index >= 1)
////		                {
//		                }
//	                    if(coordk!=null)
//	                    {
//		                    oldCoord[0] = coordk[0];
//		                    oldCoord[1] = coordk[1];
//	                    }
//		                index++;
	                }
	                Bitmap bm = Bitmap.createBitmap(colors, xlimit, ylimit, Bitmap.Config.ARGB_8888);	           
	                paint.setColor(Color.parseColor("#DEB887"));
	                Bitmap bg = Bitmap.createBitmap(xlimit, ylimit, Bitmap.Config.ARGB_8888);
	                Log.i("check density", Integer.toString(bm.getDensity()) + " " + Integer.toString(bg.getDensity()));
	                canvas = new Canvas(bg);
	                Rect r = new Rect(0,0,190,305);
	                canvas.drawBitmap(bm, 0, 0, null);	     
	                canvas.drawCircle(shiftx + x1, shifty - y1, 10, paint);
	                canvas.drawCircle(shiftx + x2, shifty - y2, 10, paint);
	                canvas.drawCircle(shiftx + x3, shifty - y3, 10, paint);
	                canvas.drawCircle(shiftx + x4, shifty - y4, 10, paint);
	                Paint p = new Paint();
	                p.setColor(Color.parseColor("#6495ED"));
	                canvas.drawCircle((float) (shiftx + coord[0]), (float) (shifty - coord[1]), 8, p);
	                Paint px = new Paint();
	                px.setStyle(Paint.Style.STROKE);
	                px.setColor(Color.parseColor("#6495ED"));
	                canvas.drawCircle((float) (shiftx + coord[0]), (float) (shifty - coord[1]), 15, px);
	                ImageView iv = (ImageView)findViewById(R.id.heatmap);
	                iv.setImageDrawable(new BitmapDrawable(getResources(), bg));
	       //         ll.setBackground(new BitmapDrawable(getResources(),bg));
	      //          ll.setBackgroundDrawable(new BitmapDrawable(bg));
	                beacons = manager.getKnownTourBeacons();	        		
	            }
	        });
	    }
	}
	

}
