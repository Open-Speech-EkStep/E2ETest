
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import Constants.Constants;
import restCommunication.RestClient;
import restCommunication.RestResponse;


public class TriggerDag implements Constants {

    static TimeConversion timeconversion = new TimeConversion();
    static String formatted_date;

    public RestResponse triggerDag(String api, String dag_id,String exec_date) {

        try {
            RestClient restClient = new RestClient();
            Map<String, String> paramMap = getParams_trigger(api, dag_id, exec_date);
            Map<String, String> headerMap = getHeaders();

            RestResponse restResponse = restClient.post(COMPOSER_ENDPOINT, paramMap, headerMap,null);
            return restResponse;

        } catch (Exception e) {
            System.out.println("http connection exception");
            return null;
        }

    }

    public RestResponse setAirflowVariable(String api,String command,String key,String value) throws IOException, URISyntaxException {
        RestClient restClient = new RestClient();
        Map<String, String> paramMap = setAirflowparams(api,command, key, value);
        Map<String, String> headerMap = getHeaders();
        RestResponse restResponse = restClient.post(COMPOSER_ENDPOINT, paramMap, headerMap,null);
        return restResponse;

    }


    public String setformatteddate()
    {
        this.formatted_date=timeconversion.scheduletime();
        return formatted_date;
    }


    public String dagStatus(String api, String dag_id,String time)
    {
        try {
            RestClient restClient = new RestClient();
            Map<String, String> paramMap = getParams(api, dag_id,time);
            Map<String, String> headerMap = getHeaders();
            RestResponse restResponse = restClient.post(COMPOSER_ENDPOINT, paramMap, headerMap, null);

            String dagstatus = restClient.getJSONvaluesfromResponse(restResponse,"output");
            dagstatus=dagstatus.replace("\"","");
            dagstatus=dagstatus.replace("\\n","@");

            String dagstatusarray[] = dagstatus.split("@");

            String newdagstatus=dagstatusarray[dagstatusarray.length-1];
            System.out.println(newdagstatus);
            return newdagstatus;

        } catch (Exception e) {
            System.out.println("http connection exception");
            System.out.println(e);
            return "unable to get the dag status";
        }

    }


    public String triggerAndWait(String dag_id,String api, Integer timeoutInMin,Integer sleeptime) throws InterruptedException {
        int numberOfTries = timeoutInMin * 2;
        String pollStatus;
        do {
            Thread.sleep(sleeptime);
            numberOfTries--;
            System.out.println("this formatdate"+this.formatted_date);
            pollStatus = dagStatus(api,dag_id,this.formatted_date);

            System.out.println("Dag current status:" + pollStatus);
        } while (!(pollStatus.equals(JOB_FAILED_STATUS) || pollStatus.equals(JOB_SUCCESS_STATUS)) && numberOfTries > 0);

        return pollStatus;
    }




    private static Map<String, String> getHeaders() throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", TokenGenerator.getToken());
        return headerMap;
    }

    private static Map<String, String> getParams_trigger(String api, String dag_id,String exec_date) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("api", api);
        paramMap.put("dag_id", dag_id);
        paramMap.put("exec_date", exec_date);
        return paramMap;
    }


    private static Map<String, String> getParams(String api, String dag_id,String execution_date) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("api", api);
        paramMap.put("dag_id", dag_id);
        paramMap.put("execution_date", execution_date);
        return paramMap;
    }

    private static Map<String, String> setAirflowparams(String api,String command, String key,String value) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("api", api);
        paramMap.put("cmd", command);
        paramMap.put("key", key);
        paramMap.put("value", value);
        return paramMap;
    }


}