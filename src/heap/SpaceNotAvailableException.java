package heap;

import chainexception.ChainException;

@SuppressWarnings("serial")
public class SpaceNotAvailableException extends ChainException {
	public SpaceNotAvailableException(Exception e, String name)

	{
		super(e, name);
	}

}
