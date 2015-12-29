
public class Node 
{
	private int familySize = 3; //size of the family
	private int x;   //coordinate of family in 2D array
	private int y;
	private boolean victim = false; //flag will be raised if Node suffers a crime
	public static double max = 43; //maximum crime severity
	
	private int[] family = new int[familySize]; //initialize empty array to hold family
	
	private int victims = 0; //will count victims if Node is a criminal
	private double safety = 1; //initial safety
	private int corporation; //corporation
	
	public Node(int corp) //constructor for commuter
	{
		corporation = corp;
		for(int i = 0; i < familySize; i++) //set all family members to -1
		{
			family[i] = -1;
		}
	}
	
	//constructor takes 3 family members, corporation, and family coordinates
	public Node(int a,int b, int c,int corp, int x, int y)
	{
		family[0] = a;
		family[1] = b;
		family[2] = c;
		
		
		corporation = corp;
		this.x = x;
		this.y = y;
		
	}
	
	public int[] family() //access the family members
	{
		return family;
	}
	
	public int victims() //access victim count
	{
		return victims;
	}
	public  void setVictim(double severity) //Node suffers a crime of severity of parameter
	{
		victim = true; //set flag to true
		if(safety>0) //lower safety by an amount related to the crime severity
		{
			safety = safety - ((double)severity/(double)max) ;
		}
	}
	public void familyVictim(int severity) //if a family member suffers a crime
	{
		if(safety>0)
		{
			safety = safety - (((double)severity/(double)max)*0.25) ;
			//lower safety by family member's suffered amount divided by number of members in family
		}
	}
	public void corporationVictim(int severity, double corpSize) //if peer group member suffers
	{
		if(safety>0)
		{
			safety = safety - (((double)severity/(double)max)*(1.0/corpSize)) ;
		}
		//lower safety by victim's amount divided by total peer group size
	}
	
	public void incrementVictim()//increase victim count
	{
		victims++;
	}
	public boolean getVictim() //returns if Node is a victim
	{
		return victim;
	}
	
	public int corp() //return corporation
	{
		return this.corporation;
	}
	public double safety()//return safety
	{
		return safety;
	}
	public int x() //returns x and y family coordinates
	{
		return x;
	}
	public int y()
	{
		return y;
	}
	
}
