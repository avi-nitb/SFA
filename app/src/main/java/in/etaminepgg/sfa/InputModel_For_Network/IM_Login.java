package in.etaminepgg.sfa.InputModel_For_Network;

/**
 * Created by Jaya on 20-12-2017.
 */

public class LoginInput_Model
{

    private String IMEI;
    private String username;
    private String password;

    public LoginInput_Model(String IMEI, String username, String password)
    {
        this.IMEI = IMEI;
        this.username = username;
        this.password = password;
    }

    public String getIMEI()
    {
        return IMEI;
    }

    public void setIMEI(String IMEI)
    {
        this.IMEI = IMEI;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
