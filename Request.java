
public class Request {

	private String protocolVersion;
	private String action;
	private Params params;

	public Request(String protocolVersion, String action, Params params) {
		super();
		this.protocolVersion = protocolVersion;
		this.action = action;
		this.params = params;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String data) {
		this.action = data;
	}

	public Params getParams() {
		return params;
	}

	public void setParams(Params params) {
		this.params = params;
	}

	public String toRawString() {
		String paramsString = "";
		if (params != null) {
			paramsString = params.toString();
		}
		return protocolVersion + "##" + action + "##" + paramsString;
	}

	public static Request fromRawString(String rawString) {
		String[] parts = rawString.split("##");
		String protocolVersion = parts[0];
		String action = parts[1];

		// request has params ? check
		String paramsString = "";
		if (parts.length > 2)
			paramsString = parts[2];

		// params has only file name which is for view file
		Params params = new Params(paramsString, null);

		// handle if params has file name and content
		if (paramsString.contains("#")) {
			String[] paramsParts = paramsString.split("#");
			params = new Params(paramsParts[0], paramsParts[1]);
		}

		return new Request(protocolVersion, action, params);
	}

}
