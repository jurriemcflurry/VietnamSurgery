package toning.juriaan.vietnamsurgery;

public class RegisterObject {

    public String registerUsername;
    public String registerPassword;
    public String confirmpassword;
    public String userrole;
    public String email;

    public RegisterObject(String registerUsername, String registerPassword, String confirmpassword, String userrole, String email){
        this.registerUsername = registerUsername;
        this.registerPassword = registerPassword;
        this.confirmpassword = confirmpassword;
        this.userrole = userrole;
        this.email = email;
    }

}
