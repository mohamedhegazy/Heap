package heap;

import chainexception.ChainException;

@SuppressWarnings("serial")
public class InvalidUpdateException  extends ChainException{
	public InvalidUpdateException(Exception e, String name)

	{
		super(e, name);
	}

}
