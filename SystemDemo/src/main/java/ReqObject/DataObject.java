package ReqObject;

import com.alibaba.fastjson.JSONObject;

public class DataObject {
    private String s;
    private JSONObject json;
    public void setJson(JSONObject json) {
        this.json = json;
    }
    public void setString(String s) {
        this.s = s;
    }
    public String getString() {
        return s;
    }
    public JSONObject getJson() {
        return json;
    }
}
