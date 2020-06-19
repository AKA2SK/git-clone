package com.lhl.gitlab;


public class InstructionBook {
    String serviceName;
    String serviceUrl;

    public InstructionBook(String serviceName,String serviceUrl) {
        this.serviceName=serviceName;
        this.serviceUrl=serviceUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
