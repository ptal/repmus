package kernel.tools;

import java.util.ArrayList;
import java.util.List;


import javafx.geometry.Point2D;

public class ST {
	
/////////////////////////////////////////////
	
	public static long lcm (long a,long b) {
		for(long n=1;;n++) {
			if(n%a == 0 && n%b == 0) return n;
		}
	}
	
/*	public static long pgcd(long a,long b) {
		    long r = a;
		    while (r!=0)  {r = a%b; a=b; b=r; }
		    return(Math.abs(a));
		    }*/
	
	public static long pgcd(long a, long b)
	{
	    while (b > 0)
	    {
	        long temp = b;
	        b = a % b; // % is remainder
	        a = temp;
	    }
	    return a;
	}

	
/////////////////////////////////////////////
    
    public static int getIndexOf( int toSearch, int[] tab ){
        int i = 0;
        while(!(tab[i] == toSearch) )
        {  i++; }
          return i; // or return tab[i];
      }
    
    public static int mod ( int num, int mod ){
        int rep = num % mod;
        if (rep < 0)
        	rep = rep +=mod;
          return rep;
     }
    
	public static int mod_plus ( int num1,int num2, int mod ){
	       return mod (num1+num2, mod);
	      }

		public static int mod_rest ( int num1,int num2, int mod ){
		       return mod (num1-num2, mod);
		      }

		public static int mod_times ( int num1,int num2, int mod ){
		       return mod (num1*num2, mod);
		      }
    
    public static List<Integer> a2l (int [] array){
    	List<Integer> rep = new ArrayList<Integer>();
        for (int index = 0; index < array.length; index++)
        {
        	rep.add(array[index]);
        }
    	return rep;
    }
//////////////////////////////////////////////
    public static List<Double> dxTox (double start, List<Double> deltas) {
    	List<Double> rep = new ArrayList<Double>();
    	double sum = start;
    	rep.add(start);
        for (Double item : deltas)
        {
        	sum = item + sum;
        	rep.add(sum);
        }
        return rep;
    }
    
    public static List<Double> xTodx (List<Double> list) {
    	List<Double> rep = new ArrayList<Double>();
    	for (int i = 0; i < list.size()-1; i++)
    		rep.add(list.get(i+1) - list.get(i));
    	return rep;
    }
    
    public static double summarize (List<Double> list) {
    	double rep = 0;
    	for (int i = 0; i < list.size(); i++)
    		rep = rep + list.get(i);
    	return rep;
    }
    
    //////////////////////////////////////////////
    public static <T> T lastElement (List<T> thelist){
    	if (thelist.size() == 0)
    		return null;
    	else
    		return thelist.get(thelist.size() - 1);
    }
    
    public static <T> boolean emptyList (List<T> thelist){
    	if (thelist == null)
    		return true;
    	return thelist.size() == 0;
    }
    
    public static <T> List<T> createList (int num, T initial){
    	List<T> rep = new ArrayList<T>();
        for (int index = 0; index < num ; index++)
        	rep.add(initial);
        return rep;
    }
    
    public static List<List<Integer>> a2l (int [][] array){
    	List<List<Integer>> rep = new ArrayList<List<Integer>>();
        for (int index = 0; index < array.length; index++)
        {
        	rep.add(a2l(array[index]));
        }
    	return rep;
    }
    
    public static List<Long> a2long (long [] array){
    	List<Long> rep = new ArrayList<Long>();
        for (int index = 0; index < array.length; index++)
        {
        	rep.add(array[index]);
        }
    	return rep;
    }
    
    public static List<List<Long>> a2long (long [][] array){
    	List<List<Long>> rep = new ArrayList<List<Long>>();
        for (int index = 0; index < array.length; index++)
        {
        	rep.add(a2long(array[index]));
        }
    	return rep;
    }
    
    
    
    public static <T> List<T> atol (T [] array){
    	List<T> rep = new ArrayList<T>();
        for (int index = 0; index < array.length; index++)
        	rep.add(array[index]);
       return rep;
    }
    
    public static <T> List<T> removeDuplicates (List<T> thelist){
    	List<T> rep = new ArrayList<T>();
        for (T item : thelist) {
        	if (! rep.contains(item))
        		rep.add(item);
        }
       return rep;
    }
    
  
}
