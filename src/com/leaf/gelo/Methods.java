package com.leaf.gelo;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

public class Methods {
	private static double d1[][];
	private static double d2[][];
	private static double d3[][];
	private static double d4[][];
	private static final double thre = 40;

	public static double[] microLocation(double x1, double x2, double x3, double y1, double y2, double y3, double r1, double r2, double r3){
		double loc[] = new double[2];
		double W,Z,Yk,x,y;
		W = r1*r1 - r2*r2 - x1*x1 - y1*y1 + x2*x2 + y2*y2;
		Z = r2*r2 - r3*r3 - x2*x2 - y2*y2 + x3*x3 + y3*y3;
		x = (W*(y3-y2) - Z*(y2-y1))/(2*((x2-x1)*(y3-y2) - (x3 - x2)*(y2 - y1)));
		y = (W - 2*x*(x2 - x1))/(2*(y2 - y1));
		Yk = (Z - 2*x*(x3 - x2))/(2*(y3 - y2));
		y = (y+Yk)/2;
		loc[0] = x;
		loc[1] = y;
		return loc;
	}
	
	public static int getDistance(double rssi){
		double distanceInInch;
		double equation = 2.303*((-rssi-20.32302)/50);
		distanceInInch = 0.43*(Math.pow(10, equation)); // Unit is in Inch. original value: 0.3352
		return (int) distanceInInch;
	}
	
	public static ArrayList<int[]> leastSquare(int x1, int x2, int x3, int x4, int y1, int y2, int y3, int y4, int r1, int r2, int r3, int r4, int xlimit, int ylimit, boolean check)
	{
		int coord[] = new int[2];
		int xMAX = xlimit + 1;
		int yMAX = ylimit + 1;
		// test case: 50*50 array, three beacon at (0,0), (50,0), (0,50)
		//	double rad1 = 71, rad2 =  71 ,rad3 = 71;
		ArrayList<int[]> al = new ArrayList<int[]>();
		int colors[][] = new int[xMAX][yMAX];
		double square[][] = new double[xMAX][yMAX];
		if(check==true) // Check true means leastSquare method is running for the first time
		{
			d1 = new double[xMAX][yMAX];
			d2 = new double[xMAX][yMAX];
			d3 = new double[xMAX][yMAX];
			d4 = new double[xMAX][yMAX];
			for(int i=0; i<xMAX; i++)
			{
				for(int j=0;j<yMAX;j++)
				{
					d1[i][j] = Math.sqrt((i-x1)*(i-x1) + (j-y1)*(j-y1));
					d2[i][j] = Math.sqrt((i-x2)*(i-x2) + (j-y2)*(j-y2));
					d3[i][j] = Math.sqrt((i-x3)*(i-x3) + (j-y3)*(j-y3));
					d4[i][j] = Math.sqrt((i-x4)*(i-x4) + (j-y4)*(j-y4));
				}
			}
		}
		if(check==false && d1.length!=0) // subsequent location updates
		{
			for(int i=0; i<xMAX; i++)
			{
				for(int j=0;j<yMAX;j++)				
					square[i][j] = ( Math.sqrt((d1[i][j] - r1)*(d1[i][j]-r1)) + Math.sqrt((d2[i][j] - r2)*(d2[i][j]-r2)) + Math.sqrt((d3[i][j]-r3)*(d3[i][j]-r3)) + Math.sqrt((d4[i][j]-r4)*(d4[i][j]-r4)));
			}
			double minValue = square[0][0];
			double maxValue = square[0][0];
			// Find the minimum.
			for(int i=0; i<xMAX; i++)
			{
				for(int j=0; j< yMAX; j++)
				{
					maxValue = Math.max(maxValue, square[i][j]);
					if(square[i][j] < minValue)
					{
						minValue = square[i][j];
						coord[0] = i;
						coord[1] = j;
					}
				}
			}

			double halfmax = (maxValue + minValue)/2;
			for(int i=0; i<xMAX; i++)
			{
				for(int j=0; j<yMAX; j++)
				{
					int blue = (int) Math.max(0, 255*(1 - square[i][j]/halfmax));
					int red = (int) Math.max(0, 255*(square[i][j]/halfmax - 1));				
					int green = 255 - blue - red;
					colors[i][j] = Color.argb(255, red, green, blue);
				}
			}
		}

		List<Integer> list = new ArrayList<Integer>();
		 for (int i = 0; i < colors.length; i++) {
			 // tiny change 1: proper dimensions
			 for (int j = 0; j < colors[i].length; j++) { 
				 // tiny change 2: actually store the values
				 list.add(colors[i][j]); 
			 }
		 }

		 // now you need to find a mode in the list.
		 
		 // tiny change 3, if you definitely need an array
		 int[] vector = new int[list.size()];
		 for (int i = 0; i < vector.length; i++) {
			 vector[i] = list.get(i);
		 }
		    
		 al.add(coord);
		 al.add(vector);
		 return al;
	}
	
	public static int[] speedCheck(int[] newCoord, int[] oldCoord)
	{
		int adjusted[] = new int[2];
		double difference = Math.sqrt((newCoord[0] - oldCoord[0])*(newCoord[0] - oldCoord[0]) + (newCoord[1] - oldCoord[1])*(newCoord[1] - oldCoord[1]));
		if(difference<=thre)
			adjusted  = newCoord;
		else
		{
			double ratio = difference/thre;
			adjusted[0] = (int) (newCoord[0]/ratio);
			adjusted[1] = (int) (newCoord[1]/ratio);
		}
		return adjusted;
	}
	
	public static double[] getCoordinate(int x1,int y1,int x2,int y2,int x3,int y3,double r1,double r2,double r3){

    	double coord[] = new double[2];
    	int d12 = (int) Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    	int d13 = (int) Math.sqrt((x1-x3)*(x1-x3) + (y1-y3)*(y1-y3));
    	int d23 = (int) Math.sqrt((x2-x3)*(x2-x3) + (y2-y3)*(y2-y3));
    	
    	// CASE 1, THREE CIRCLE INTERSECT EACH OTHER BUT NO MID OVERLAPPING AREA. THIS ONE IS RIGHT.
    	if(d12 <= r1+r2 && d13 <= r1+r3 && d23 <= r2+r3 )  coord = microLocation(x1, x2, x3, y1, y2, y3, r1, r2, r3);
	
    	//CASE 2: ONE CIRCLE INTERSECTS WITH TWO CIRCLES, BUT THE OTHER TWO DON'T
    	if( (d12 <= r1+r2 && d13 <= r1+r3 && d23 > r2+r3) || (d12 <= r1+r2 && d23 <= r2+r3 && d13 > r1+r3) || (d13 <= r1+r3 && d23 <= r2+r3 && d12 > r1+r2) )
    	{
    		if(d12 <= r1+r2 && d13 <= r1+r3 && d23 > r2+r3) // This is totally right.
    		{
    			double point[] = new double[2];
    			double point2[] = new double[2];
    			point[0] = - (r1*r1 - r2*r2 - x1*x1 + x2*x2 - y1*y1 + y2*y2)/(2*(x1 - x2)) - ((y1 - y2)*(x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2)))- x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1 + x1*x1*y2 + x2*x2*y1+ x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2))/(4*(x1 - x2)*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)) - ((y1 - y2)*(x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1+ 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2)))- r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1 + x1*x1*y2 + x2*x2*y1 + x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2))/(4*(x1 - x2)*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2));
        		point[1] = (x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2+ y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1 + x1*x1*y2 + x2*x2*y1 + x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2)/(4*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)) + (x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2- y2*y2))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1+ x1*x1*y2 + x2*x2*y1 + x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2)/(4*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2));       		
        		point2[0] =  - (r1*r1 - r3*r3 - x1*x1 + x3*x3 - y1*y1 + y3*y3)/(2*(x1 - x3)) - ((y1 - y3)*(x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3)))- x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1+ x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3))/(4*(x1 - x3)*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)) - ((y1 - y3)*(x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1+ 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3)))- r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3))/(4*(x1 - x3)*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3));		
        		point2[1] = (x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3)/(4*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)) + (x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3)/(4*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3));        		
        		coord[0] = (point[0] + point2[0])/2;
        		coord[1] = (point[1] + point2[1])/2;
        		double length = Math.sqrt((point[0] - x1)*(point[0] - x1) + (point[1] - y1)*(point[1] - y1)) + Math.sqrt((point2[0] - x1)*(point2[0] - x1) + (point2[1] - y1)*(point2[1] - y1));
        		length = length/2;
        		double lengthK = Math.sqrt((coord[0] - x1)*(coord[0] - x1) + (coord[1] - y1)*(coord[1] - y1));
        		double ratio = length/lengthK;
        		coord[0] = (coord[0] - x1)*ratio + x1;
        		coord[1] = (coord[1] - y1)*ratio + y1;
        		
//         		coord[0] = 9;
//        		coord[1] = 9;        		
        	}
    		if(d12 <= r1+r2 && d23 <= r2+r3 && d13 > r1+r3) // THIS ONE IS TOTALLY RIGHT!!!!
    		{
    			double point[] = new double[2];
    			double point2[] = new double[2];
    			point[0] = - (r1*r1 - r2*r2 - x1*x1 + x2*x2 - y1*y1 + y2*y2)/(2*(x1 - x2)) - ((y1 - y2)*(x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) 
        				- x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1 + x1*x1*y2 + x2*x2*y1
        				+ x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2))/(4*(x1 - x2)*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)) - ((y1 - y2)*(x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1
                				+ 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2)))
        				- r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1 + x1*x1*y2 + x2*x2*y1 + x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2))/(4*(x1 - x2)*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2));
        		
        		point[1] = (x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2
        				+ y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1 + x1*x1*y2 + x2*x2*y1 + x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 
        				- 2*x1*x2*y1 - 2*x1*x2*y2)/(4*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)) + (x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2
        				- y2*y2))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1
        				+ x1*x1*y2 + x2*x2*y1 + x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2)/(4*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2));

        		point2[0] = - (r2*r2 - r3*r3 - x2*x2 + x3*x3 - y2*y2 + y3*y3)/(2*(x2 - x3)) - ((y2 - y3)*(x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3))/(4*(x2 - x3)*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)) - ((y2 - y3)*(x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3))/(4*(x2 - x3)*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3));
        		point2[1] = (x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3)/(4*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)) + (x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3)/(4*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3));
        	
        		coord[0] = (point[0] + point2[0])/2;
        		coord[1] = (point[1] + point2[1])/2;
        		double length = Math.sqrt((point[0] - x2)*(point[0] - x2) + (point[1] - y2)*(point[1] - y2)) + Math.sqrt((point2[0] - x2)*(point2[0] - x2) + (point2[1] - y2)*(point2[1] - y2));
        		length = length/2;
        		double lengthK = Math.sqrt((coord[0] - x2)*(coord[0] - x2) + (coord[1] - y2)*(coord[1] - y2));
        		double ratio = length/lengthK;
        		coord[0] = (coord[0] - x2)*ratio + x2;
        		coord[1] = (coord[1] - y2)*ratio + y2;
//           		coord[0] = 10;
//        		coord[1] = 10;
    		}
    		if(d13 <=r1+r3 && d23 <= r2+r3 && d12 > r1+r2) // THIS ONE IS TOTALLY RIGHT!!
    		{

    			double point[] = new double[2];
    			double point2[] = new double[2];
           		point[0] = - (r1*r1 - r3*r3 - x1*x1 + x3*x3 - y1*y1 + y3*y3)/(2*(x1 - x3)) - ((y1 - y3)*(x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3))/(4*(x1 - x3)*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)) - ((y1 - y3)*(x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3))/(4*(x1 - x3)*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3));
        		point[1] = (x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1  + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + Math.pow(y3, 3) - 2*x1*x3*y1 - 2*x1*x3*y3)/(4*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)) + (x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3)/(4*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3));

        		point2[0] = - (r2*r2 - r3*r3 - x2*x2 + x3*x3 - y2*y2 + y3*y3)/(2*(x2 - x3)) - ((y2 - y3)*(x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3))/(4*(x2 - x3)*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)) - ((y2 - y3)*(x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3))/(4*(x2 - x3)*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3));
        		point2[1] = (x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3)/(4*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)) + (x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3)/(4*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3));
        	
        		coord[0] = (point[0] + point2[0])/2;
        		coord[1] = (point[1] + point2[1])/2;
        		
        		double length = Math.sqrt((point[0] - x3)*(point[0] - x3) + (point[1] - y3)*(point[1] - y3)) + Math.sqrt((point2[0] - x3)*(point2[0] - x3) + (point2[1] - y3)*(point2[1] - y3));
        		length = length/2;
        		double lengthK = Math.sqrt((coord[0] - x3)*(coord[0] - x3) + (coord[1] - y3)*(coord[1] - y3));
        		double ratio = length/lengthK;
        		coord[0] = (coord[0] - x3)*ratio + x3;
        		coord[1] = (coord[1] - y3)*ratio + y3;
        		
//           		coord[0] = 11;
//        		coord[1] = 11;
    		
    		}
    	}
    	
    	// CASE 3: ONE CIRCLE INTERSECTS WITH ONE CIRCLE, THE THIRD ONE STAYS INDEPENDENT.  *Totally right****
    	if(d12 <= r1+r2 && d13 > r1+r3 && d23 > r2+r3){ // THIS ONE IS TOTALLY RIGHT.
    		coord[0] = - (r1*r1 - r2*r2 - x1*x1 + x2*x2 - y1*y1 + y2*y2)/(2*(x1 - x2)) - ((y1 - y2)*(x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) 
    				- x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1 + x1*x1*y2 + x2*x2*y1
    				+ x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2))/(4*(x1 - x2)*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)) - ((y1 - y2)*(x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1
    				+ 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2)))
    				- r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1 + x1*x1*y2 + x2*x2*y1 + x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2))/(4*(x1 - x2)*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2));
    		
    		coord[1] = (x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2
    				+ y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1 + x1*x1*y2 + x2*x2*y1 + x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 
    				- 2*x1*x2*y1 - 2*x1*x2*y2)/(4*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)) + (x2*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2
            				- y2*y2))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r2 - r2*r2 + x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2)*(r1*r1 + 2*r1*r2 + r2*r2 - x1*x1 + 2*x1*x2 - x2*x2 - y1*y1 + 2*y1*y2 - y2*y2))) - r1*r1*y1 + r1*r1*y2 + r2*r2*y1 - r2*r2*y2 + x1*x1*y1
    				+ x1*x1*y2 + x2*x2*y1 + x2*x2*y2 - y1*y2*y2 - y1*y1*y2 + y1*y1*y1 + y2*y2*y2 - 2*x1*x2*y1 - 2*x1*x2*y2)/(4*(x1*x1 - 2*x1*x2 + x2*x2 + y1*y1 - 2*y1*y2 + y2*y2));
    		double length = Math.sqrt((coord[0] - x3)*(coord[0] - x3) + (coord[1] - y3)*(coord[1] - y3));
    		double ratio = 0.5*(r3+length)/length;
    		coord[0] = (coord[0] - x3)*ratio + x3;
    		coord[1] = (coord[1] - y3)*ratio + y3;
    		
    //		coord[0] = 12;
    //		coord[1] = 12;
    	}
    	if(d12 > r1+r2 && d13 <= r1+r3 && d23 > r2+r3){ // This one is totally right!
    		coord[0] = - (r1*r1 - r3*r3 - x1*x1 + x3*x3 - y1*y1 + y3*y3)/(2*(x1 - x3)) - ((y1 - y3)*(x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3))/(4*(x1 - x3)*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)) - ((y1 - y3)*(x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3))/(4*(x1 - x3)*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3));
    		coord[1] = (x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3)/(4*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)) + (x3*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - x1*(Math.sqrt((- r1*r1 + 2*r1*r3 - r3*r3 + x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3)*(r1*r1 + 2*r1*r3 + r3*r3 - x1*x1 + 2*x1*x3 - x3*x3 - y1*y1 + 2*y1*y3 - y3*y3))) - r1*r1*y1 + r1*r1*y3 + r3*r3*y1 - r3*r3*y3 + x1*x1*y1 + x1*x1*y3 + x3*x3*y1 + x3*x3*y3 - y1*y3*y3 - y1*y1*y3 + y1*y1*y1 + y3*y3*y3 - 2*x1*x3*y1 - 2*x1*x3*y3)/(4*(x1*x1 - 2*x1*x3 + x3*x3 + y1*y1 - 2*y1*y3 + y3*y3));
    		double length = Math.sqrt((coord[0] - x2)*(coord[0] - x2) + (coord[1] - y2)*(coord[1] - y2));
    		double ratio = 0.5*(r2+length)/length;
    		coord[0] = (coord[0] - x2)*ratio + x2;
    		coord[1] = (coord[1] - y2)*ratio + y2;
    //		coord[0] = 13;
    //		coord[1] = 13;
    	}
    	if(d12 > r1+r2 && d13 > r1+r3 && d23 <= r2+r3){ // This one is totally right!
    		coord[0] = - (r2*r2 - r3*r3 - x2*x2 + x3*x3 - y2*y2 + y3*y3)/(2*(x2 - x3)) - ((y2 - y3)*(x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3))/(4*(x2 - x3)*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)) - ((y2 - y3)*(x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3))/(4*(x2 - x3)*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3));
    		coord[1] = (x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3)/(4*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)) + (x3*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - x2*(Math.sqrt((- r2*r2 + 2*r2*r3 - r3*r3 + x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3)*(r2*r2 + 2*r2*r3 + r3*r3 - x2*x2 + 2*x2*x3 - x3*x3 - y2*y2 + 2*y2*y3 - y3*y3))) - r2*r2*y2 + r2*r2*y3 + r3*r3*y2 - r3*r3*y3 + x2*x2*y2 + x2*x2*y3 + x3*x3*y2 + x3*x3*y3 - y2*y3*y3 - y2*y2*y3 + y2*y2*y2 + y3*y3*y3 - 2*x2*x3*y2 - 2*x2*x3*y3)/(4*(x2*x2 - 2*x2*x3 + x3*x3 + y2*y2 - 2*y2*y3 + y3*y3));
    		
    		double length = Math.sqrt((coord[0] - x1)*(coord[0] - x1) + (coord[1] - y1)*(coord[1] - y1));
    		double ratio = 0.5*(r1+length)/length;
    		coord[0] = (coord[0] - x1)*ratio + x1;
    		coord[1] = (coord[1] - y1)*ratio + y1;
    		// 		coord[0] = 14;
    //		coord[1] = 14;
    	}
    	
    	// CASE 4: NO INTERSECTIONS, RETURN NULL
    	if(d12 > r1+r2 && d13 > r1+r3 && d23 > r2+r3){
    		r1=r1*1.15;
    		r2=r2*1.15;
    		r3=r3*1.15;
    		coord = getCoordinate(x1,y1,x2,y2,x3,y3,r1,r2,r3);  // recursion
    	}
    	return coord;
    
	}

}
