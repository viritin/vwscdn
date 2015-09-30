package com.vaadin.wscdn.client;

public class WidgetSetResponse {

    private PublishState widgetSetStatus = PublishState.UNKNOWN;

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

    public PublishState getStatus() {
        return widgetSetStatus;
    }

    public void setStatus(PublishState status) {
        this.widgetSetStatus = status;
    }
}
