public class Params {
    public String filename;
    public String content;

    public Params(String filename, String content) {
        this.filename = filename;
        this.content = content;
    }

    public String toString() {
        String paramsString = "";

        if (this.filename != null)
            paramsString += this.filename;
        if (this.filename != null && this.content != null)
            paramsString += "#";
        if (this.content != null)
            paramsString += this.content;

        return paramsString;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getContent() {
        return this.content;
    }
}
