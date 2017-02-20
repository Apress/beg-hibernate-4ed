package chapter09.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Software extends Product implements Serializable {
    @Column
    @NotNull
    String version;

    public Software() {
    }

    public Software(Supplier supplier, String name, String description, Double price, String version) {
        super(supplier, name, description, price);
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Software)) return false;
        if (!super.equals(o)) return false;

        Software software = (Software) o;

        return version.equals(software.version);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append(":Software{");
        sb.append('}');
        return sb.toString();
    }
}
