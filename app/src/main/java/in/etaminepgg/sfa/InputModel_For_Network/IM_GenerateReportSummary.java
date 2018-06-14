package in.etaminepgg.sfa.InputModel_For_Network;

/**
 * Created by etamine on 4/6/18.
 */

public class IM_GenerateReportSummary
{

    private String authToken;
    private String created_by;

    public IM_GenerateReportSummary(String authToken, String created_by)
    {
        this.authToken = authToken;
        this.created_by = created_by;
    }


    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public void setCreated_by(String created_by)
    {
        this.created_by = created_by;
    }
}
