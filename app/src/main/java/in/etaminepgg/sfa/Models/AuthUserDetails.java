package in.etaminepgg.sfa.Models;


@SuppressWarnings("all")
public class AuthUserDetails
{
    private final int apiStatus;

    private final String authToken;

    private  String userId;

    private final Data data;

    public AuthUserDetails(int apiStatus, String authToken, Data data)
    {
        this.apiStatus = apiStatus;
        this.authToken = authToken;
        this.data = data;
    }

    public int getApiStatus()
    {
        return apiStatus;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public Data getData()
    {
        return data;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public static class Data
    {
        private final String username;

        private final String mobile;

        private final String email;

        private final String role;

        private final String location_id;

        private final String company_id;

        private final String no_mandatory_visit;

        public Data(String username, String mobile, String email, String role, String locationId,
                    String companyId,String no_mandatory_visit)
        {
            this.username = username;
            this.mobile = mobile;
            this.email = email;
            this.role = role;
            this.location_id = locationId;
            this.company_id = companyId;
            this.no_mandatory_visit = no_mandatory_visit;
        }

        public String getUsername()
        {
            return username;
        }

        public String getMobile()
        {
            return mobile;
        }

        public String getEmail()
        {
            return email;
        }

        public String getRole()
        {
            return role;
        }

        public String getLocationId()
        {
            return location_id;
        }

        public String getCompanyId()
        {
            return company_id;
        }

        public String getNo_mandatory_visit() {
            return no_mandatory_visit;
        }
    }
}
