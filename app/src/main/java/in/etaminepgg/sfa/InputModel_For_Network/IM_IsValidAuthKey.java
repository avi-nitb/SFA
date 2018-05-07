package in.etaminepgg.sfa.InputModel_For_Network;

/**
 * Created by Jaya on 21-12-2017.
 */

public class IM_IsValidAuthKey
{

    private  String authToken;

    private  String company_id;

    public IM_IsValidAuthKey(String authToken,String company_id)
    {
        this.authToken = authToken;
        this.company_id = company_id;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public String getCompany_id()
    {
        return company_id;
    }

    public void setCompany_id(String company_id)
    {
        this.company_id = company_id;
    }
}
