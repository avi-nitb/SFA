package in.etaminepgg.sfa.Models;


@SuppressWarnings("all")
public class AuthUserDetails
{
    private final int apiStatus;

    private final String authToken;

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

    public static class Data
    {
        private final String username;

        private final String mobile;

        private final String email;

        private final String role;

        private final String locationId;

        private final String companyId;

        public Data(String username, String mobile, String email, String role, String locationId,
                    String companyId)
        {
            this.username = username;
            this.mobile = mobile;
            this.email = email;
            this.role = role;
            this.locationId = locationId;
            this.companyId = companyId;
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
            return locationId;
        }

        public String getCompanyId()
        {
            return companyId;
        }
    }
}
