package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.model.BlogUser;
import hr.fer.zemris.java.tecaj_13.model.Util;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * <ol>
 * <li>Punjenje iz trenutnog zahtjeva metodom {@link #popuniIzHttpRequesta(HttpServletRequest)}. Čita parametre
 *     i upisuje odgovarajuća svojstva u formular.</li>
 * <li>Punjenje iz domenskog objekta metodom {@link #popuniIzBlogUsera(BlogUser)} (BlogUser)}. Prima {@link BlogUser} kao argument
 *     i temeljem toga što je u njemu upisano popunjava ovaj formular.</li>
 * <li>Punjenje domenskog objekta temeljem upisanog sadržaja u formularu metodom {@link #popuniUBlogUsera(BlogUser)} (Record)}.
 *     Ideja je da se ovo radi tek ako su podatci u formularu prošli validaciju. Pogledajte pojedine servlete koji
 *     su pripremljeni uz ovaj primjer za demonstraciju kako se to radi.</li>
 * </ol>
 *
 * @author Filip Vucic
 */
public class RegisterForm {

    /**
     * Mapa s pogreškama. Očekuje se da su ključevi nazivi svojstava a vrijednosti
     * tekstovi pogrešaka.
     */
    Map<String, String> greske = new HashMap<>();
    /**
     * Password.
     */
    private String passwordHash;
    /**
     * Prezime osobe.
     */
    private String lastName;
    /**
     * Ime osobe.
     */
    private String firstName;
    /**
     * Nick osobe.
     */
    private String nick;
    /**
     * E-mail osobe.
     */
    private String email;

    /**
     * Konstruktor.
     */
    public RegisterForm() {
    }

    /**
     * Dohvaća poruku pogreške za traženo svojstvo.
     *
     * @param ime naziv svojstva za koje se traži poruka pogreške
     * @return poruku pogreške ili <code>null</code> ako svojstvo nema pridruženu pogrešku
     */
    public String dohvatiPogresku(String ime) {
        return greske.get(ime);
    }

    /**
     * Provjera ima li barem jedno od svojstava pridruženu pogrešku.
     *
     * @return <code>true</code> ako ima, <code>false</code> inače.
     */
    public boolean imaPogresaka() {
        return !greske.isEmpty();
    }

    /**
     * Provjerava ima li traženo svojstvo pridruženu pogrešku.
     *
     * @param ime naziv svojstva za koje se ispituje postojanje pogreške
     * @return <code>true</code> ako ima, <code>false</code> inače.
     */
    public boolean imaPogresku(String ime) {
        return greske.containsKey(ime);
    }

    /**
     * Na temelju parametara primljenih kroz {@link HttpServletRequest} popunjava
     * svojstva ovog formulara.
     *
     * @param req objekt s parametrima
     */
    public void popuniIzHttpRequesta(HttpServletRequest req) {
        this.firstName = pripremi(req.getParameter("firstName"));
        this.lastName = pripremi(req.getParameter("lastName"));
        this.nick = pripremi(req.getParameter("nick"));
        this.email = pripremi(req.getParameter("email"));
        try {
            if (!pripremi(req.getParameter("password")).isEmpty()) {
                this.passwordHash = Util.bytetohex(Util.calcHash(pripremi(req.getParameter("password"))));
            } else {
                this.passwordHash = "";
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Na temelju predanog {@link BlogUser}-a popunjava ovaj formular.
     *
     * @param blogUser objekt koji čuva originalne podatke
     */
    public void popuniIzBlogUsera(BlogUser blogUser) {
        this.firstName = pripremi(blogUser.getFirstName());
        this.lastName = pripremi(blogUser.getLastName());
        this.email = pripremi(blogUser.getEmail());
        this.nick = pripremi(blogUser.getNick());
        this.passwordHash = pripremi(blogUser.getPasswordHash());
    }

    /**
     * Temeljem sadržaja ovog formulara puni svojstva predanog domenskog
     * objekta. Metodu ne bi trebalo pozivati ako formular prethodno nije
     * validiran i ako nije utvrđeno da nema pogrešaka.
     *
     * @param blogUser domenski objekt koji treba napuniti
     */
    public void popuniUBlogUsera(BlogUser blogUser) {
        blogUser.setFirstName(this.firstName);
        blogUser.setLastName(this.lastName);
        blogUser.setNick(this.nick);
        blogUser.setEmail(this.email);
        blogUser.setPasswordHash(this.passwordHash);
    }

    public void dodajGresku(String key, String value) {
        greske.put(key, value);
    }

    /**
     * Metoda obavlja validaciju formulara. Formular je prethodno na neki način potrebno
     * napuniti. Metoda provjerava semantičku korektnost svih podataka te po potrebi
     * registrira pogreške u mapu pogrešaka.
     */
    public void validiraj() {
        greske.clear();

        if (this.firstName.isEmpty()) {
            greske.put("firstName", "First name is mandatory!");
        }

        if (this.lastName.isEmpty()) {
            greske.put("lastName", "Last name is mandatory!");
        }

        if (this.email.isEmpty()) {
            greske.put("email", "EMail is mandatory!");
        } else {
            int l = email.length();
            int p = email.indexOf('@');
            if (l < 3 || p == -1 || p == 0 || p == l - 1) {
                greske.put("email", "EMail format not valid.");
            }
        }

        if (this.nick.isEmpty()) {
            greske.put("nick", "Nick is mandatory!");
        }

        if (this.passwordHash.isEmpty()) {
            greske.put("password", "Password is mandatory!");
        }
    }

    /**
     * Pomoćna metoda koja <code>null</code> stringove konvertira u prazne stringove, što je
     * puno pogodnije za uporabu na webu.
     *
     * @param s string
     * @return primljeni string ako je različit od <code>null</code>, prazan string inače.
     */
    private String pripremi(String s) {
        if (s == null) return "";
        return s.trim();
    }

    /**
     * Dohvat password hash-a.
     *
     * @return id
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Setter za password hash od passworda.
     *
     * @param password Password
     */
    public void setPasswordHash(String password) {
        if (password.isEmpty()) {
            this.passwordHash = "";
        } else {
            try {
                this.passwordHash = Util.bytetohex(Util.calcHash(password));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Dohvat prezimena.
     *
     * @return prezime
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter za prezime.
     *
     * @param lastName vrijednost na koju ga treba postaviti.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Dohvat imena.
     *
     * @return ime
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter za ime.
     *
     * @param firstName vrijednost na koju ga treba postaviti.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Dohvat emaila.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter za email.
     *
     * @param email vrijednost na koju ga treba postaviti.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Dohvat nicka.
     *
     * @return nick
     */
    public String getNick() {
        return nick;
    }

    /**
     * Setter za nick.
     *
     * @param nick vrijednost na koju ga treba postaviti.
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

}
