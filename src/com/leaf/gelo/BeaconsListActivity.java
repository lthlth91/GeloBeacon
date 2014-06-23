package com.leaf.gelo;

import android.app.Activity;
// import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
// import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gelo.gelosdk.GeLoBeaconManager;
import com.gelo.gelosdk.Model.Beacons.GeLoBeacon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.gelo.gelosdk.GeLoConstants.GELO_BEACON_FOUND;

public class BeaconsListActivity extends Activity {
    GeLoBeaconManager manager;
    BeaconListAdapter adapter;
    // beacon distances
    static int dist1 = 0;
    static int dist2 = 0;
    static int dist3 = 0;
    private static int x1=0,x2=0,y1=0,y2=0,x3=0,y3=0;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_list);
        manager = GeLoBeaconManager.sharedInstance(getApplicationContext());
        manager.startScanningForBeacons();
        
        
        
        //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//        TextView tv = (TextView) findViewById(R.id.coord); 
//        double coord[] = new double[2];
//        double coordCompare[] = new double[2];
//        double rad = 300; // 318, 270
//        coord = Methods.microLocation(1, 550, 275, 0, 2, 476, 270, 270, rad);
//        coordCompare = Methods.getCoordinate(1, 0, 550, 2, 275, 476, 270, 270, rad);
//        tv.setText("[" + Integer.toString((int) coord[0]) + "," + Integer.toString((int) coord[1]) + "] \n" 
//        		+ "[" + Integer.toString((int) coordCompare[0]) + "," + Integer.toString((int) coordCompare[1]) + "] \n" );
      //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^     
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(GELO_BEACON_FOUND));

        ListView list = (ListView) findViewById(R.id.listView);
        adapter = new BeaconListAdapter(getApplicationContext(), R.layout.single_list_item, manager.getKnownTourBeacons());
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<GeLoBeacon> knownBeacons = manager.getKnownTourBeacons();
                GeLoBeacon beacon = knownBeacons.get(position);
                Intent detailIntent = new Intent(getBaseContext(), DetailActivity.class);
                detailIntent.putExtra("beaconId", beacon.getBeaconId());
              //  startActivity(detailIntent);
            }
        });        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new UpdateTask(), 0, 1 * 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.beacons_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GELO_BEACON_FOUND)) {
               //We don't actually use the messages transmitted by the beacon manager
                //This is only included for illustrative purposes on how to listen for intents.
            }
        }
    };

     class BeaconListAdapter extends ArrayAdapter<GeLoBeacon> {
        Context context;
        List<GeLoBeacon> beacons;
        static final double cmToInch = 0.393701;
        int ind = 0;
        String stringX;
        String stringY;
        String distanceA;
        String distanceB;
        String distanceC;
     //   int rssi[] = new int[200];
     //   double distances[] = new double[200];      
     //   double avgRSSI=0;
     //   double avgDistance =0;    
     //   int n=0;
     //   double Var = 0;
     //   double var2 = 0;
        public BeaconListAdapter(Context context, int textViewResourceId, List<GeLoBeacon> beacons) {
            super(context, textViewResourceId, beacons);
            this.context = context;
            this.beacons = beacons;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = inflater.inflate(R.layout.single_list_item, parent, false);
            TextView nameView = (TextView) rootView.findViewById(R.id.idView);
            TextView tv = (TextView) findViewById(R.id.coord);   
            if (position < beacons.size()) {
                GeLoBeacon beacon = beacons.get(position);
                // still working on the more accurate version of RF equation
//                if(beacon.getBeaconId()==107)
//                {
//                	rssi[n] = beacon.getSignalStregth();
//                	double strength = (double) beacon.getSignalStregth();
//                	double equation = (31.593-strength-50*Math.log10(2412)+150-32.44)/50;
//                    double distance = Math.pow(10, equation);
//                    distances[n] = distance;
//                    distance = 0.3352*Math.pow(distance, 2.303);
//                    distance1 = (int) distance;
//                    n=n+1;
//                }             
//                else if(beacon.getBeaconId()==107 && n==200)
//                {
//                	
//                	for (int index=0; index< rssi.length; index++){
//                		avgRSSI += rssi[index];
//                		avgDistance += distances[index];
//                	}
//                	avgRSSI = avgRSSI/(rssi.length); // avg
//                	avgDistance= avgDistance/(distances.length);
//                	
//                	for (int index=0; index< rssi.length; index++){
//                		Var = Var + (rssi[index] - avgRSSI)*(rssi[index] - avgRSSI);
//                		var2 = var2 + (distances[index] - avgDistance)*(distances[index] - avgDistance);
//                	}
//                	  Var = Var/(rssi.length);
//                    Var = Math.sqrt(Var);
//                    var2 = var2/(distances.length);
//                    var2 = Math.sqrt(var2);                	 
////        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
////        					BeaconsListActivity.this);
////        			// set title
////        			alertDialogBuilder.setTitle(" Mean and STD");
////         
////        			// set dialog message
////        			double avg = avgDistance;
////        			double var = var2;
////        			alertDialogBuilder
////        				.setMessage("Mean: " + Double.toString(avg) + "  Std: " + Double.toString(var) )
////        				.setCancelable(false)
////        				.setNegativeButton("   ",new DialogInterface.OnClickListener() {
////        					public void onClick(DialogInterface dialog,int id) {
////        						// if this button is clicked, just close
////        						// the dialog box and do nothing
////        						dialog.cancel();
////        					}
////        				});
////        				// create alert dialog
////        				AlertDialog alertDialog = alertDialogBuilder.create();
////        				// show it
////        				alertDialog.show();
//        		}
                              
          // NEW SETS OF MEASUREMENTS FOR ANDROID ARE NEEDED!      
                double coord[] = new double[2];
 //               double coordCompare[] = new double[2];
                double sum1 = 0;
//                double distances1[] = new double[6];
                double rssi1[] = new double[6];
                double sum2 = 0;
//                double distances2[] = new double[6];
                double rssi2[] = new double[6];
                double sum3 = 0;
//                double distances3[] = new double[6];
                double rssi3[] = new double[6];
                int counter1 = 0;
                int counter2 = 0;
                int counter3 = 0;
                int radius[] = new int[3];
                              
                if(beacons.size()>=3){
                    if(beacon.getBeaconId()==107)
                    {
                        double strength = (double) beacon.getSignalStregth();
                        rssi1[counter1] = strength; //  double distance = 0.0271*Math.exp(-0.121*strength);    Old equation.                    
                        if(counter1==5){
//                        	distances1[0] = distances1[1];
//                        	distances1[1] = distances1[2];
//                        	distances1[2] = distances1[3];
//                        	distances1[3] = distances1[4];
//                        	distances1[4] = distances1[5];
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
                    	nameView.setText(Integer.toString(beacon.getBeaconId())+ "  " + Integer.toString(dist1)  + " Inch");                        
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
                    	nameView.setText(Integer.toString(beacon.getBeaconId())+ "  " + Integer.toString(dist2)  + " Inch"); 
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
                    	nameView.setText(Integer.toString(beacon.getBeaconId())+ "  " + Integer.toString(dist3)  + " Inch");                  
                    }
                    radius[0] = dist1;
                    radius[1] = dist2;
                    radius[2] = dist3;
                }
                // Coordinated system created on June 11th 2014 at Leaf Lounge area.
                x1 = 2;
                y1 = 1;
                x2 = 162;
                y2 = 2;
                x3 = 114;
                y3 = 296;
                if(radius[0]!=0 && radius[1]!=0 && radius[2]!=0)
                {
                    coord = Methods.getCoordinate(x1 ,y1 ,x2 ,y2 ,x3 ,y3, radius[2], radius[0], radius[1]);
  //                  coordCompare = Methods.microLocation(x1, x2, x3, y1, y2, y3, radius[2], radius[0], radius[1]);
  //              	coord = Methods.getCoordinate(x1 ,y1 ,x2 ,y2 ,x3 ,y3, 167, 198, 158);
                    tv.setText("from getcoord: \n"+"[" + Integer.toString((int) coord[0]) + "," + Integer.toString((int) coord[1]) + "] \n" + ind++ );
                }
                // save all the coordinates to the txt files for later process.

                if(ind > 1000  &&  ind <= 6000)
                {
                	if(coord[0]!= 0 && coord[1] != 0)
                	{
                    	stringX = stringX + Integer.toString((int) coord[0]) + "\n";
                    	stringY = stringY + Integer.toString((int) coord[1]) + "\n";
                	}
                	distanceA = distanceA + Integer.toString(dist1) + "\n";
                	distanceB = distanceB + Integer.toString(dist2) + "\n";
                	distanceC = distanceC + Integer.toString(dist3) + "\n";

                }
                else if(ind==6001)
                {
                    try {
                        saveToFile(stringX);
                        saveToFile2(stringY);
                        saveToFile3(distanceA);
                        saveToFile4(distanceB);
                        saveToFile5(distanceC);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }      
                }
            }
            
            Button button = (Button) findViewById(R.id.location_button);
            button.setOnClickListener(new OnClickListener(){
            	@Override
            	public void onClick(View v){
            		Intent intent = new Intent(getBaseContext(),LocationActivity.class);
            		startActivity(intent);
            	}
            });
            
            return rootView;
        }             
    }
     
     private void saveToFile(String data) throws FileNotFoundException {
         
    	 File f = new File(Environment.getExternalStorageDirectory() + "/Leaf/");
    	 if(!f.exists())
    	 {
    		 f.mkdirs();
    	 }
    	 File file = new File(Environment.getExternalStorageDirectory() + "/Leaf/GeloFileX2.txt");
    	 if(!file.exists())
    	 {
    		 try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
    	 FileOutputStream fos = new FileOutputStream(file);
          try {
             fos.write(data.getBytes());
             fos.close();
          } catch (IOException e) {
             Log.e("Controller", e.getMessage() + e.getLocalizedMessage() + e.getCause());
         }
     }
     
     private void saveToFile2(String data) throws FileNotFoundException {
         
    	 File f = new File(Environment.getExternalStorageDirectory() + "/Leaf/");
    	 if(!f.exists())
    	 {
    		 f.mkdirs();
    	 }
    	 File file = new File(Environment.getExternalStorageDirectory() + "/Leaf/GeloFileY2.txt");
    	 if(! file.exists())
    	 {
    		 try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
    	 FileOutputStream fos = new FileOutputStream(file);
         try {
             fos.write(data.getBytes());
             fos.close();
          } catch (IOException e) {
             Log.e("Controller", e.getMessage() + e.getLocalizedMessage() + e.getCause());
         }
     }
     
     private void saveToFile3(String data) throws FileNotFoundException {
	         
	    	 File f = new File(Environment.getExternalStorageDirectory() + "/Leaf/");
	    	 if(!f.exists())
	    	 {
	    		 f.mkdirs();
	    	 }
	    	 File file = new File(Environment.getExternalStorageDirectory() + "/Leaf/GeloFileDistanceA.txt");
	    	 if(! file.exists())
	    	 {
	    		 try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	 }
	    	 FileOutputStream fos = new FileOutputStream(file);
	         try {
	             fos.write(data.getBytes());
	             fos.close();
	          } catch (IOException e) {
	             Log.e("Controller", e.getMessage() + e.getLocalizedMessage() + e.getCause());
	         }
	     }
     
     private void saveToFile4(String data) throws FileNotFoundException {
	    
		 File f = new File(Environment.getExternalStorageDirectory() + "/Leaf/");
		 if(!f.exists())
		 {
			 f.mkdirs();
		 }
		 File file = new File(Environment.getExternalStorageDirectory() + "/Leaf/GeloFileDistanceB.txt");
		 if(! file.exists())
		 {
			 try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 FileOutputStream fos = new FileOutputStream(file);
	    try {
	        fos.write(data.getBytes());
	        fos.close();
	     } catch (IOException e) {
	        Log.e("Controller", e.getMessage() + e.getLocalizedMessage() + e.getCause());
	    }
	}
     
     private void saveToFile5(String data) throws FileNotFoundException {
      
 	 File f = new File(Environment.getExternalStorageDirectory() + "/Leaf/");
 	 if(!f.exists())
 	 {
 		 f.mkdirs();
 	 }
 	 File file = new File(Environment.getExternalStorageDirectory() + "/Leaf/GeloFileDistanceC.txt");
 	 if(! file.exists())
 	 {
 		 try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 	 }
 	 FileOutputStream fos = new FileOutputStream(file);
      try {
          fos.write(data.getBytes());
          fos.close();
       } catch (IOException e) {
          Log.e("Controller", e.getMessage() + e.getLocalizedMessage() + e.getCause());
      }
  }

  class UpdateTask extends TimerTask {
        @Override
        public  void run() {
            BeaconsListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    ArrayList<GeLoBeacon> knownBeacons = manager.getKnownTourBeacons();
                    adapter.addAll(knownBeacons);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
