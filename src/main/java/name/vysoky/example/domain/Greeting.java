package name.vysoky.example.domain;

import name.vysoky.example.util.Parser;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Just some testing greetings.
 *
 * @author Jiri Vysoky
 */
@Entity
public class Greeting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIME)
    private Date dateCreated;
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return Parser.formatShortDate(dateCreated) + ": \"" + message + "\"";
    }
}
