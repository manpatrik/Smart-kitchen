package szakdoga.haztartas.models;

public class Home {
    private String homeId;
    private String homeName;

    public Home(String homeId, String homeName) {
        this.homeId = homeId;
        this.homeName = homeName;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }
}
