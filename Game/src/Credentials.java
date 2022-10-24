public class Credentials {
    private final String email;
    private final String password;

    public Credentials(String email, String password){
        this.email = email;
        this.password = password;
    }

    String getEmail(){
        return email;
    }

    String getPassword(){
        return password;
    }
}
