
/**
 * 
 * Response example: PROTOCOL_VERSION#STATUS#DATA
 *
 */

public class Response {

    private String protocolVersion;
    private String status;
    private String data;

    public Response(String protocolVersion, String status, String data) {
        super();
        this.protocolVersion = protocolVersion;
        this.status = status;
        this.data = data;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String toRawResponse() {
        return protocolVersion + "##" + status + "##" + data + "\r\n";
    }

    public static Response fromRawResponse(String rawResponse) {
        String[] parts = rawResponse.split("##");
        String protocolVersion = parts[0];
        String status = parts[1];
        String data = parts[2];

        return new Response(protocolVersion, status, data);
    }

    public String getResult() {
        String rawString = "";
        if (data == null) {
            return "There is no any result.";
        } else {
            rawString += protocolVersion + "##" + status + "##" + data;
            return rawString;
        }
    }

}
