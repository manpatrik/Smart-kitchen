package szakdoga.haztartas.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String email;
    private List<String> tokens;
    private List<String> tokenNames;
    private List<String> blockTokens;

    public User() {
    }

    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
        tokens = new ArrayList<>();
    }

    public User(String userId, String email, List<String> tokens, List<String> tokenNames, List<String> blockTokens) {
        this.userId = userId;
        this.email = email;
        this.tokens = tokens;
        this.tokenNames = tokenNames;
        this.blockTokens = blockTokens;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getTokenNames() {
        return tokenNames;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public List<String> getBlockTokens() {
        return blockTokens;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public void setBlockTokens(List<String> blockTokens) {
        this.blockTokens = blockTokens;
    }

    public void setTokenNames(List<String> tokenNames) {
        this.tokenNames = tokenNames;
    }

    public void addToken(String token, String tokenName){
        this.tokens.add(token);
        this.tokenNames.add(tokenName);
    }

    public void addBlockToken(String blockToken){
        this.blockTokens.add(blockToken);
    }

    public void removeToken(String token){
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equals(token)){
                tokenNames.remove(i);
                tokens.remove(i);
                return;
            }
        }
    }

    public void removeBlockToken(String blockToken){
        this.blockTokens.remove(blockToken);
    }

    public boolean isHaveToken(String token){
        for (int i = 0; i < tokens.size(); i++) {
            if( tokens.get(i).equals(token)){
                return true;
            }
        }
        return false;
    }
}
