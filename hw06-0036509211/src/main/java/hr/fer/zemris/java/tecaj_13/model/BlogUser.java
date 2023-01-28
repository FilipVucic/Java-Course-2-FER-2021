package hr.fer.zemris.java.tecaj_13.model;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "BlogUser.upit1", query = "select b from BlogUser as b where b.nick=:nick"),
        @NamedQuery(name = "BlogUser.upit2", query = "select b from BlogUser as b")
})
@Entity
@Table(name = "blog_users")
@Cacheable(true)
public class BlogUser {
    private Long id;
    private String firstName;
    private String lastName;
    private String nick;
    private String email;
    private String passwordHash;
    private List<BlogEntry> blogEntries;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(length = 30, nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(length = 30, nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(length = 20, nullable = false, unique = true)
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Column(length = 100, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(length = 50, nullable = false)
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    public List<BlogEntry> getBlogEntries() {
        return blogEntries;
    }

    public void setBlogEntries(List<BlogEntry> blogEntries) {
        this.blogEntries = blogEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogUser blogUser = (BlogUser) o;
        return Objects.equals(id, blogUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
