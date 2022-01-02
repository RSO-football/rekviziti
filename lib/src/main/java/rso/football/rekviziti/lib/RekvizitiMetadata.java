package rso.football.rekviziti.lib;

public class RekvizitiMetadata {

    private Integer rekvizitId;
    private String type;
    private Integer cost;
    private Integer trenerId;

    public Integer getRekvizitId() {
        return rekvizitId;
    }

    public void setRekvizitId(Integer rekvizitId) {
        this.rekvizitId = rekvizitId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCost() {
        return cost;
    }

    public Integer getTrenerId() {
        return trenerId;
    }

    public void setTrenerId(Integer trenerId) {
        this.trenerId = trenerId;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }
}
