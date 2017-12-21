package in.etaminepgg.sfa.InputModel_For_Network;

/**
 * Created by Jaya on 21-12-2017.
 */

public class InputMode_IsValidAuthKey
{

    private  String authToken;

    public InputMode_IsValidAuthKey(String authToken)
    {
        this.authToken = authToken;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }
}
