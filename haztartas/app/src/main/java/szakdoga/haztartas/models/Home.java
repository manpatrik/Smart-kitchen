package szakdoga.haztartas.models;

import java.util.List;

public class Home {
    private String homeId;
    private String name;
    private String ownerEmail;
    private String owner;
    private List<String> guestIds;
    private List<String> guestEmails;

    public Home() {
    }

    public Home(String homeId, String name, String ownerEmail, String owner, List<String> guestsId, List<String> guestEmails) {
        this.homeId = homeId;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.owner = owner;
        this.guestIds = guestsId;
        this.guestEmails = guestEmails;
    }


    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getGuestIds() {
        return guestIds;
    }

    public void setGuestIds(List<String> guestIds) {
        this.guestIds = guestIds;
    }

    public List<String> getGuestEmails() {
        return guestEmails;
    }

    public void setGuestEmails(List<String> guestEmails) {
        this.guestEmails = guestEmails;
    }

    public void addGuest(String userId, String email){
        this.guestIds.add(userId);
        this.guestEmails.add(email);
    }

    public void deleteGuest(String userId){
        for( int i=0; i<this.guestIds.size(); i++ ){
            if(guestIds.get(i).equals(userId)){
                guestIds.remove(i);
                guestEmails.remove(i);
                return;
            }
        }
    }
}
