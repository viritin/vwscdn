package in.virit.vwscdn.client;

import java.util.Date;

public class WidgetSetStatus {

    private PublishState publishState = PublishState.NOT_FOUND;
    private String id;
    private String info;
    private String compileHost;
    private Date compileStartTime;
    private Date compileEndTime;
    private long compileTime;
    private WidgetSetRequest content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WidgetSetRequest getContent() {
        return content;
    }

    public void setContent(WidgetSetRequest content) {
        this.content = content;
    }

    public Date getCompileStartTime() {
        return compileStartTime;
    }

    public void setCompileStartTime(Date compileStartTime) {
        this.compileStartTime = compileStartTime;
    }

    public Date getCompileEndTime() {
        return compileEndTime;
    }

    public void setCompileEndTime(Date compileEndTimeMs) {
        this.compileEndTime = compileEndTimeMs;
    }

    public long getCompileTime() {
        return compileTime;
    }

    public void setCompileTime(long compileTimeMs) {
        this.compileTime = compileTimeMs;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCompileHost() {
        return compileHost;
    }

    public void setCompileHost(String compileHost) {
        this.compileHost = compileHost;
    }

    public PublishState getPublishState() {
        return publishState;
    }

    public void setPublishState(PublishState state) {
        this.publishState = state;
    }

}
