package in.etaminepgg.sfa.Models;

/**
 * Created by Jaya on 21-12-2017.
 */

public class ValidAuthModel
{

  /*   "api_status": 1,
       "message": "Authorization token is valid."*/


  private int api_status;
  private String message;

    public int getApi_status()
    {
        return api_status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setApi_status(int api_status)
    {
        this.api_status = api_status;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
