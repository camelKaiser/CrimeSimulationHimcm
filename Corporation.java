import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Corporation 
{
	private boolean willAffect = false;
	private int affected = 0;
	private int size = 0;
	private  ArrayList list = new ArrayList();
	
	public Corporation(int member)
	{
		list.add(member);
		size++;
	}
	public int size()
	{
		return size;
	}
	public ArrayList list()
	{
		//System.out.println("CORPORATION DMG");
		return list;
	}
	public void add(int member)
	{
		list.add(member);
		size++;
	}
	public boolean willAffect()
	{
		return willAffect;
	}
	
	public void affect()
	{
		affected++;
		if((double)affected/(double)size >= 0.99)
		{
			willAffect = false;
		}
	}
	public void print()
	{
		for(int i = 0; i < list.size(); i++)
		{
			System.out.println("element is "+(int)list.get(i));
		}
	}
	
}
