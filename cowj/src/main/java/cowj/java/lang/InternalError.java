package cowj.java.lang;

public class InternalError extends Error
{
    private static final long serialVersionUID = 6579076928447024110L;

    public InternalError()
    {
    	super();
    }
    
	public InternalError(String message)
    {
		super(message);
    }
}
