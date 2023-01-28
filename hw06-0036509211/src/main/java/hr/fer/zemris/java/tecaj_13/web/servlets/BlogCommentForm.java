package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.model.BlogComment;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Filip Vucic
 */
public class BlogCommentForm {

    /**
     * Mapa s pogreškama. Očekuje se da su ključevi nazivi svojstava a vrijednosti
     * tekstovi pogrešaka.
     */
    Map<String, String> greske = new HashMap<>();
    private String usersEMail;
    private String message;

    /**
     * Konstruktor.
     */
    public BlogCommentForm() {
    }

    public String getUsersEMail() {
        return usersEMail;
    }

    public void setUsersEMail(String usersEMail) {
        this.usersEMail = usersEMail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        this.usersEMail = pripremi(req.getParameter("usersEMail"));
        this.message = pripremi(req.getParameter("message"));
    }


    /**
     * Na temelju predanog {@link BlogComment}-a popunjava ovaj formular.
     *
     * @param blogComment objekt koji čuva originalne podatke
     */
    public void popuniIzBlogCommenta(BlogComment blogComment) {
        this.usersEMail = pripremi(blogComment.getUsersEMail());
        this.message = pripremi(blogComment.getMessage());
    }

    /**
     * Temeljem sadržaja ovog formulara puni svojstva predanog domenskog
     * objekta. Metodu ne bi trebalo pozivati ako formular prethodno nije
     * validiran i ako nije utvrđeno da nema pogrešaka.
     *
     * @param blogComment domenski objekt koji treba napuniti
     */
    public void popuniUBlogComment(BlogComment blogComment) {
        blogComment.setUsersEMail(this.usersEMail);
        blogComment.setMessage(this.message);
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

        if (this.usersEMail.isEmpty()) {
            greske.put("email", "Email is mandatory!");
        } else {
            int l = usersEMail.length();
            int p = usersEMail.indexOf('@');
            if (l < 3 || p == -1 || p == 0 || p == l - 1) {
                greske.put("email", "EMail format not valid.");
            }
        }

        if (this.message.isEmpty()) {
            greske.put("message", "Message is mandatory!");
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

}
