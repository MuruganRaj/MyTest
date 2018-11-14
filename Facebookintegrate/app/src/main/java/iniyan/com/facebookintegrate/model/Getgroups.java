package iniyan.com.facebookintegrate.model;

public class Getgroups {
    private GetgroupsData data;
    private GetgroupsResponse[] response;

    public GetgroupsData getData() {
        return this.data;
    }

    public void setData(GetgroupsData data) {
        this.data = data;
    }

    public GetgroupsResponse[] getResponse() {
        return this.response;
    }

    public void setResponse(GetgroupsResponse[] response) {
        this.response = response;
    }
}
