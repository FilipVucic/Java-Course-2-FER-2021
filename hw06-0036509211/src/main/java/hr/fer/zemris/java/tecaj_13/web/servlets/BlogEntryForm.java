package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.model.BlogEntry;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Filip Vucic
 */
public class BlogEntryForm {

    /**
     * Mapa s pogreškama. Očekuje se da su ključevi nazivi svojstava a vrijednosti
     * tekstovi pogrešaka.
     */
    Map<String, String> greske = new HashMap<>();
    private String id;
    private String title;
    private String text;

    /**
     * Konstruktor.
     */
    public BlogEntryForm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        this.id = pripremi(req.getParameter("id"));
        this.title = pripremi(req.getParameter("title"));
        this.text = pripremi(req.getParameter("text"));
    }


    /**
     * Na temelju predanog {@link BlogEntry}-a popunjava ovaj formular.
     *
     * @param blogEntry objekt koji čuva originalne podatke
     */
    public void popuniIzBlogEntrya(BlogEntry blogEntry) {
        if (blogEntry.getId() == null) {
            this.id = "";
        } else {
            this.id = blogEntry.getId().toString();
        }
        this.title = pripremi(blogEntry.getTitle());
        this.text = pripremi(blogEntry.getText());
    }

    /**
     * Temeljem sadržaja ovog formulara puni svojstva predanog domenskog
     * objekta. Metodu ne bi trebalo pozivati ako formular prethodno nije
     * validiran i ako nije utvrđeno da nema pogrešaka.
     *
     * @param blogEntry domenski objekt koji treba napuniti
     */
    public void popuniUBlogEntry(BlogEntry blogEntry) {
        blogEntry.setTitle(this.title);
        blogEntry.setText(this.text);
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

        if (this.title.isEmpty()) {
            greske.put("title", "Title is mandatory!");
        }

        if (this.text.isEmpty()) {
            greske.put("text", "Text is mandatory!");
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


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
