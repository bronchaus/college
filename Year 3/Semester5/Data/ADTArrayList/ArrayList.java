
public class ArrayList implements ListInterface
{
	private static final int MAX_LIST = 50;
	private Object items[]; // list variables
	private int numItems; //current ammount of items in the list
	
	public ArrayList()
	{
		items = new Object[MAX_LIST]; // allocating memory
		numItems = 0;
	}

	public boolean isEmpty() 
	{
		return (numItems==0);
	}

	public int size() 
	{
		return numItems;
	}

	public void add(int index, Object item) throws ListIndexOutOfBoundsException, ListException 
	{
		if(numItems==50)
		{
			throw new ListIndexOutOfBoundsException("List is full!");
		}
		else
		{

		}
	}

	public int translate(int position)
	{
		return position-1;
	}
	
	public Object get(int index) throws ListIndexOutOfBoundsException 
	{
		if(index>=1 && index<=numItems)
		{
			return items[translate(index)];
		}
		else
		{
			throw new ListIndexOutOfBoundsException("Invalid index on get");
		}
	}

	public void remove(int index) throws ListIndexOutOfBoundsException 
	{
		
	}

	public void removeAll() 
	{
		items = new Object[MAX_LIST];
		numItems=0;
	}

}
