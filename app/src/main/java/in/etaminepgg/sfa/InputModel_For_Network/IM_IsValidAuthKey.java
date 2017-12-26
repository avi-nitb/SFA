package in.etaminepgg.sfa.InputModel_For_Network;

/**
 * Created by Jaya on 21-12-2017.
 */

public class IM_IsValidAuthKey
{

    private  String authToken;

    public IM_IsValidAuthKey(String authToken)
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
