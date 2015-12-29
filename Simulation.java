

import java.lang.*;
import java.io.*;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;




public class Simulation 
{
	public static void main(String[] arg)
	{

        // The name of the file to write to
        String fileName = "normal_normal_plus50.txt";

        try {
            // Assume default encoding.
            FileWriter fileWriter = new FileWriter(fileName);
            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

    		for(int i = 0; i < 450; i++) //call simulate multiple times
    		{
    			System.out.println(i + 1); //print count every iteration
    			
    			double simulation = simulate(); //simulate 80 day period
    			
    			bufferedWriter.write(Double.toString(simulation)); //write to file
    			bufferedWriter.newLine();
    			
    		}
            //close file
            bufferedWriter.close();
        }
        catch(IOException ex) {//exception
            System.out.println(
                "Error writing to file '"
                + fileName + "'");
        }
		
		
	}
	public static int t = 0; //global variable for the hour
	public static double simulate()
	{
		Family[][] families = new Family[250][280]; //2D array for map of families
		
		Corporation[] corps = new Corporation[88]; //initialize corporation, resident, and commuter arrays
		Node[] nodes = new Node[280000];
		Node[] commuters = new Node[600000];
		
		for(int j = 0; j <commuters.length; j++) //generate commuters
		{
			int corpNumber = randInt(1,88); //random peer group
			commuters[j] = new Node(corpNumber-1); //create commuter node
			
			if(corps[corpNumber-1] == null) //create new corporation if required
			{
				corps[corpNumber-1] = new Corporation(j);
			}
			else //add to corporation
			{
				corps[corpNumber-1].add(j);
			}
			
		}
		System.out.println("generated commuters");
		
		for(int i = 0; i < 280000; i=i+4) //generate residential nodes
		{
			int a = 0; 
			int b = 0; 
			int c = 0; 
			int d = 0;
			
			a = randInt(1, 88); //generate peer groups and check for uniqueness
			b = randInt(1, 88);
			while(a == b)
			{
				b = randInt(1, 88);
			}
			c = randInt(1, 88);
			while(a == c || c == b)
			{
				c = randInt(1, 88);
			}
			d = randInt(1, 88);	
			while(a == d || b == d || c == d)
			{
				d = randInt(1, 88);
			}
			
			//generate 4 nodes (1 family)
			nodes[i] = new Node(i+1,i+2,i+3, a-1,(i/4)%280, (i/4)%250); //create node
			if(corps[a-1] == null) //assign corporation
			{
				corps[a-1] = new Corporation(i);
			}
			else
			{
				corps[a-1].add(i);
			}
			//Repeat 3 more times
			nodes[i+1] = new Node(i,i+2,i+3, b-1,(i/4)%280, (i/4)%250);
			if(corps[b-1] == null)
			{
				corps[b-1] = new Corporation(i+1);
			}
			else
			{
				corps[b-1].add(i+1);
			}
			
			nodes[i+2] = new Node(i,i+1,i+3, c-1,(i/4)%280, (i/4)%250);
			if(corps[c-1] == null)
			{
				corps[c-1] = new Corporation(i+2);
			}
			else
			{
				corps[c-1].add(i+2);
			}
			
			nodes[i+3] = new Node(i,i+1,i+2, d-1,(i/4)%280, (i/4)%250);
			if(corps[d-1] == null)
			{
				corps[d-1] = new Corporation(i+3);
			}
			else
			{
				corps[d-1].add(i+3);
			}
			
		}
		System.out.println("generated residents");
		
		
		int j = 0;
		for(int row = 0; row < 250; row++) //generate 2D array for families
		{
			for(int col = 0; col < 280; col++)
			{
				families[row][col] = new Family(j, j+1, j+2, j+3); //add 4 nodes to famiy
				j=j+4;
			}
		}
		
		Day1(nodes, commuters, corps,families); //simulate day one
		for(int f = 0; f < 89; f++) //simulate next 89 days
		{
			Day(nodes, commuters, corps,families);
		}
		
		int resident = 0; //resident index
		
		double safety = 0.0; //variables for residential and commuter safety
		double comSafety =0.0;
		
		while (resident<280000) //loop though residents
		{
			safety += nodes[resident].safety(); //find total safety
			resident++;
		}
		safety = safety/280000; //average
		int o = 0; //commuter index
		
		while(o < commuters.length)
		{
			
			comSafety += commuters[o].safety(); //find total safety
			o++;
		}
		comSafety = comSafety/600000; //average
		return (double)((safety+comSafety)/2); //return average of commuter and residential safety
		
	}
	
	//called each day
	public static void Day(Node[] nodes, Node[] commuters, Corporation[] corps, Family[][] families) //daily day
	{
		int severity;
		int morning  = 35;//crimes for each hour during time period
		int afternoon = 92;
		int evening =47; 
		while(t<=8)//when hour is 8 AM or before
		{
			for(int i = 0; i < morning; i++) //each crime
			{
				int a = randInt(0, 280000 -1); //random victim and criminal
				int b = randInt(0, 280000 - 1);
				while(a == b)
				{
					b = randInt(0, 280000 -1);
				}
				severity = Severity(); 
				nodes[a].setVictim(severity);
				Neighbor(severity, nodes, a, families); //damage neighbors of victim
				nodes[b].incrementVictim(); 
				
				int[] family = nodes[a].family(); //damage family
				for(int k = 0; k<family.length; k++)
				{
					nodes[family[k]].familyVictim(Severity());
				}
				int test = nodes[a].corp(); //find corporation
			
				corps[test].affect();
				if(corps[nodes[a].corp()].willAffect()) //if all in group are affected 
				{
					ArrayList list = corps[nodes[a].corp()].list(); //damage group
					for(int p = 0; p < corps[nodes[a].corp()].size();p++)
					{
						int affected = (int) list.get(p);
						if(affected < 280000)
						{
							nodes[affected].corporationVictim(Severity(),(double)list.size());
						}
						else
						{
							commuters[affected-280000].corporationVictim(Severity(),(double)list.size());
						}
					}
				}		
			}
			t = (t+1)%24; //increase hour count
		}
		while(t > 8 && t <= 20) //commuters are included
		{
			for(int i = 0; i < afternoon; i++) //each crime
			{
				int a = randInt(0, 880000 -1); //2 distinct nodes
				int b = randInt(0, 880000 - 1);
				while(a == b)
				{
					b = randInt(0, 880000 -1);
				}
				if(a < 280000) //if victim is a resident
				{
					severity = Severity();
					nodes[a].setVictim(severity);
					Neighbor(severity, nodes, a, families); //damage neighbors
					
					int[] family = nodes[a].family(); //damage families
					for(int k = 0; k<family.length; k++)
					{
						nodes[family[k]].familyVictim(Severity());
					}
					corps[nodes[a].corp()].affect(); //peer group
				}
				else //not a resident
				{
					commuters[a - 280000].setVictim(Severity());
					corps[commuters[a-280000].corp()].affect();
				}
				if(b < 280000) //aggressor is a resident
				{
					nodes[b].incrementVictim();	
				}
				else //not a resident
				{
					commuters[b - 280000].incrementVictim();
				}	
				
				if(a < 280000) //affected person was resident
				{
					if(corps[nodes[a].corp()].willAffect()) //if peer group is hurt
					{
						ArrayList list = corps[nodes[a].corp()].list();
						for(int p = 0; p < corps[nodes[a].corp()].size();p++)
						{
							int affected = (int) list.get(p);
							if(affected < 280000)
							{
								nodes[affected].corporationVictim(Severity(),(double)list.size());
							}
							else
							{
								commuters[affected-280000].corporationVictim(Severity(),(double)list.size());
							}
						}
					}
				}
				else //affected was commuter
				{
					if(corps[commuters[a-280000].corp()].willAffect()) //if all in group are affected 
					{
						ArrayList list = corps[commuters[a-280000].corp()].list();
						for(int p = 0; p < corps[commuters[a-280000].corp()].size();p++)
						{
							int affected = (int) list.get(p); //determine if the peer group victim is a commuter
							if(affected < 280000)
							{
								nodes[affected].corporationVictim(Severity(),(double)list.size());
							}
							else
							{
								commuters[affected-280000].corporationVictim(Severity(),(double)list.size());
							}
						}
					}
				}
			}
			t = (t+1)%24;//increment t
		}
		while(t>20) //evening, only residents again
		{
			for(int i = 0; i < evening; i++) //each crime
			{
				int a = randInt(0, 280000 -1); //2 distinct nodes
				int b = randInt(0, 280000 - 1);
				while(a == b)
				{
					b = randInt(0, 280000 -1);
				}
				severity = Severity();
				nodes[a].setVictim(severity);
				Neighbor(severity, nodes, a, families); //set the victim
				nodes[b].incrementVictim();
				
				int[] family = nodes[a].family(); //decrement family
				for(int k = 0; k<family.length; k++)
				{
					nodes[family[k]].familyVictim(Severity());
				}
				
				corps[nodes[a].corp()].affect(); //peer group
				if(corps[nodes[a].corp()].willAffect()) //if all in group are affected 
				{
					ArrayList list = corps[nodes[a].corp()].list();
					for(int p = 0; p < corps[nodes[a].corp()].size();p++)
					{
						int affected = (int) list.get(p);
						if(affected < 280000)
						{
							nodes[affected].corporationVictim(Severity(),(double)list.size());
						}
						else
						{
							commuters[affected-280000].corporationVictim(Severity(),(double)list.size());
						}
					}
				}		
			}
			t = (t+1)%24;
		}
	}
	
	//Day1() is the same as Day() with a crime count equivalent to 3 months
	public static void Day1(Node[] nodes, Node[] commuters, Corporation[] corps, Family[][] families)
	{
		int severity;
		int morning  = 3196; //crimes/hr
		int afternoon = 8302; 
		int evening =4184; 
		while(t<=8)
		{
			for(int i = 0; i < morning ; i++) //each crime
			{
				int a = randInt(0, 280000 -1); //2 distinct nodes
				int b = randInt(0, 280000 - 1);
				while(a == b)
				{
					b = randInt(0, 280000 -1);
				}
				severity = Severity();
				nodes[a].setVictim(severity); //neighbor
				Neighbor(severity, nodes, a, families); //family damage
				nodes[b].incrementVictim();
				
				int[] family = nodes[a].family();
				for(int k = 0; k<family.length; k++)
				{
					nodes[family[k]].familyVictim(Severity());
				}
				
				//corporation
				int test = nodes[a].corp();
				corps[test].affect();
				if(corps[nodes[a].corp()].willAffect()) //if all in peer group are affected 
				{
					ArrayList list = corps[nodes[a].corp()].list();
					for(int p = 0; p < corps[nodes[a].corp()].size();p++)
					{
						int affected = (int) list.get(p);
						if(affected < 280000) //damaged peer group member is resident or not
						{
							nodes[affected].corporationVictim(Severity(),(double)list.size());
						}
						else
						{
							commuters[affected-280000].corporationVictim(Severity(),(double)list.size());
						}
					}
				}		
			}
			t = (t+1)%24;
		}
		while(t > 8 && t <= 20) //commuters are in
		{
			for(int i = 0; i < afternoon; i++) //each crime
			{
				int a = randInt(0, 880000 -1); //2 distinct nodes
				int b = randInt(0, 880000 - 1);
				while(a == b)
				{
					b = randInt(0, 880000 -1);
				}
				if(a < 280000) //set victim
				{
					severity = Severity();
					nodes[a].setVictim(severity);
					Neighbor(severity, nodes, a, families); //neighbor and family
					int[] family = nodes[a].family();
					for(int k = 0; k<family.length; k++)
					{
						nodes[family[k]].familyVictim(Severity());
					}
					corps[nodes[a].corp()].affect();
				}
				else
				{
					commuters[a - 280000].setVictim(Severity());
					corps[commuters[a-280000].corp()].affect();
				}
				if(b < 280000) //set aggressor
				{
					nodes[b].incrementVictim();	
				}
				else
				{
					commuters[b - 280000].incrementVictim();
				}	
				
				if(a < 280000) //affected person was resident
				{
					if(corps[nodes[a].corp()].willAffect()) //if all in peer group are affected 
					{
						ArrayList list = corps[nodes[a].corp()].list();
						for(int p = 0; p < corps[nodes[a].corp()].size();p++)
						{
							int affected = (int) list.get(p);
							if(affected < 280000)
							{
								nodes[affected].corporationVictim(Severity(),(double)list.size());
							}
							else
							{
								commuters[affected-280000].corporationVictim(Severity(),(double)list.size());
							}
						}
					}
				}
				else //affected was commuter
				{
					if(corps[commuters[a-280000].corp()].willAffect()) //if all in group are affected 
					{
						ArrayList list = corps[commuters[a-280000].corp()].list();
						for(int p = 0; p < corps[commuters[a-280000].corp()].size();p++)
						{
							int affected = (int) list.get(p); //determine if the group victim is a commuter
							if(affected < 280000)
							{
								nodes[affected].corporationVictim(Severity(),(double)list.size());
							}
							else
							{
								commuters[affected-280000].corporationVictim(Severity(),(double)list.size());
							}
						}
					}
				}
			}
			t = (t+1)%24;
		}
		while(t>20)
		{
			for(int i = 0; i < evening; i++) //each crime
			{
				int a = randInt(0, 280000 -1); //2 distinct nodes
				int b = randInt(0, 280000 - 1);
				while(a == b)
				{
					b = randInt(0, 280000 -1);
				}
				severity = Severity();
				nodes[a].setVictim(severity);
				Neighbor(severity, nodes, a, families); //neighbor
				nodes[b].incrementVictim();
				
				int[] family = nodes[a].family(); //decrement family
				for(int k = 0; k<family.length; k++)
				{
					nodes[family[k]].familyVictim(Severity());
				}
				corps[nodes[a].corp()].affect();
				if(corps[nodes[a].corp()].willAffect()) //if all in peer group are affected 
				{
					ArrayList list = corps[nodes[a].corp()].list();
					for(int p = 0; p < corps[nodes[a].corp()].size();p++)
					{
						int affected = (int) list.get(p);
						if(affected < 280000)
						{
							nodes[affected].corporationVictim(Severity(),(double)list.size());
						}
						else
						{
							commuters[affected-280000].corporationVictim(Severity(),(double)list.size());
						}
					}
				}		
			}
			t = (t+1)%24;
		}
	}
	public static int Severity() //random Severity generator using frequency
	{
		int severity = randInt(0, 1000);
		if(severity < 11)
			return 4;
		else if(severity < 286)
			return 6;
		else if(severity < 367)
			return 7;
		else if(severity < 459)
			return 8;
		else if(severity < 475)
			return 9;
		else if(severity < 610)
			return 10;
		else if(severity < 710)
			return 11;
		else if(severity < 780)
			return 12;
		else if(severity < 810)
			return 14;
		else if(severity < 811)
			return 16;
		else if(severity < 817)
			return 17;
		else if(severity < 882)
			return 18;
		else if(severity < 909)
			return 19;
		else if(severity < 919)
			return 20;
		else if(severity < 923)
			return 21;
		else if(severity < 941)
			return 22;
		else if(severity < 953)
			return 23;
		else if(severity < 967)
			return 24;
		else if(severity < 980)
			return 25;
		else if(severity < 982)
			return 26;
		else if(severity < 985)
			return 27;
		else if(severity < 989)
			return 30;
		else if(severity < 991)
			return 32;
		else if(severity < 993)
			return 34;
		else if(severity < 995)
			return 35;
		else if(severity < 999)
			return 38;
		else
			return 43;

	}
	public static void Neighbor(int severity, Node[] nodes, int node, Family[][] families)//damage neighbor
	{
		Family temp = new Family();
		int ax, ay, bx, by, cx, cy, dx, dy;
		
		int row = nodes[node].y();
		int col = nodes[node].x();
		//Tier 1
	
		double class1 = (double)severity/2;
		double class2 = (double)severity/4;
		double class3 = (double)severity/8;
		
		//Class 3
		ax = col+1;
		bx = col+2;
		cx = col-1; 
		dx = col-2;
		ay = row+1;
		by = row + 2;
		cy=row-1;
		dy=row-2;

		if(ay < 250)
		{
			temp = families[ay][col];
			if (temp == null)
			{
				System.out.println("fuc");
				System.out.println("ay "+ay+" col "+col);
			}
			nodes[temp.a()].setVictim(class1);
			nodes[temp.b()].setVictim(class1);
			nodes[temp.c()].setVictim(class1);
			nodes[temp.d()].setVictim(class1);
		}
		if(by < 250)
		{
			temp = families[by][col];
			nodes[temp.a()].setVictim(class3);
			nodes[temp.b()].setVictim(class3);
			nodes[temp.c()].setVictim(class3);
			nodes[temp.d()].setVictim(class3);
		}
		if(cy > -1)
		{
			temp = families[cy][col];
			nodes[temp.a()].setVictim(class1);
			nodes[temp.b()].setVictim(class1);
			nodes[temp.c()].setVictim(class1);
			nodes[temp.d()].setVictim(class1);
		}
		if(dy>-1)
		{
			temp = families[dy][col];
			nodes[temp.a()].setVictim(class3);
			nodes[temp.b()].setVictim(class3);
			nodes[temp.c()].setVictim(class3);
			nodes[temp.d()].setVictim(class3);
		}

		if(ax < 280)
		{
			temp = families[row][ax];
			nodes[temp.a()].setVictim(class1);
			nodes[temp.b()].setVictim(class1);
			nodes[temp.c()].setVictim(class1);
			nodes[temp.d()].setVictim(class1);
		}
		if(bx < 280)
		{
			temp = families[row][bx];
			nodes[temp.a()].setVictim(class3);
			nodes[temp.b()].setVictim(class3);
			nodes[temp.c()].setVictim(class3);
			nodes[temp.d()].setVictim(class3);
		}
		if(cx > -1)
		{
			temp = families[row][cx];
			nodes[temp.a()].setVictim(class1);
			nodes[temp.b()].setVictim(class1);
			nodes[temp.c()].setVictim(class1);
			nodes[temp.d()].setVictim(class1);
		}
		if(dx>-1)
		{
			temp = families[row][dx];
			nodes[temp.a()].setVictim(class3);
			nodes[temp.b()].setVictim(class3);
			nodes[temp.c()].setVictim(class3);
			nodes[temp.d()].setVictim(class3);
		}
		//Case 2
		if(ax < 280 && ay < 250)
		{
			temp = families[ay][ax];
			nodes[temp.a()].setVictim(class2);
			nodes[temp.b()].setVictim(class2);
			nodes[temp.c()].setVictim(class2);
			nodes[temp.d()].setVictim(class2);
		}
		if(cx > -1 && cy > -1)
		{
			temp = families[cy][cx];
			nodes[temp.a()].setVictim(class2);
			nodes[temp.b()].setVictim(class2);
			nodes[temp.c()].setVictim(class2);
			nodes[temp.d()].setVictim(class2);
		}
		if(ax < 280 && cy>-1)
		{
			temp = families[cy][ax];
			nodes[temp.a()].setVictim(class2);
			nodes[temp.b()].setVictim(class2);
			nodes[temp.c()].setVictim(class2);
			nodes[temp.d()].setVictim(class2);
		}
		if(cx > -1&& ay < 250)
		{
			temp = families[ay][cx];
			nodes[temp.a()].setVictim(class2);
			nodes[temp.b()].setVictim(class2);
			nodes[temp.c()].setVictim(class2);
			nodes[temp.d()].setVictim(class2);
		}
	}
	
	public static int randInt(int min, int max) { //generate random number in range

	 
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
