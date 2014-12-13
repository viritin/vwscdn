package org.vaadin.vwscdn.client;

public class WidgetSetResponse {

    private PublishStatus widgetSetStatus = PublishStatus.UNKNOWN;

    private String widgetSetName;
    private String widgetSetUrl;

    public String getWidgetSetName() {
        return widgetSetName;
    }

    public void setWidgetSetName(String widgetSetName) {
        this.widgetSetName = widgetSetName;
    }

    public String getWidgetSetUrl() {
        return widgetSetUrl;
    }

    public void setWidgetSetUrl(String widgetSetUrl) {
        this.widgetSetUrl = widgetSetUrl;
    }

    public PublishStatus getStatus() {
        return widgetSetStatus;
    }

    public void setStatus(PublishStatus status) {
        this.widgetSetStatus = status;
    }
}
