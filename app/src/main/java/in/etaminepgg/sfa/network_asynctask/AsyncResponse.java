package in.etaminepgg.sfa.network_asynctask;

public interface AsyncResponse
{
    void onRefresh();

    void ReceivedResponseFromServer(String output, String REQUEST_NUMBER);

}