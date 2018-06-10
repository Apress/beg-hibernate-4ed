package chapter10.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(name = "product.searchByPhrase",
                query = "from Product p "
                        + "where p.name like :text or p.description like :text"),
        @NamedQuery(name = "product.findProductAndSupplier",
                query = "from Product p inner join p.supplier as s"),
})
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    Supplier supplier;
    @Column
    @NotNull
    String name;
    @Column
    @NotNull
    String description;
    @Column
    @NotNull
    Double price;

    Integer vseion;

    public Product() {
    }

    public Product(Supplier supplier, String name, String description, Double price) {
        this.supplier = supplier;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (!description.equals(product.description)) return false;
        if (id != null ? !id.equals(product.id) : product.id != null) return false;
        if (!name.equals(product.name)) return false;
        if (!price.equals(product.price)) return false;
        return supplier.equals(product.supplier);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + supplier.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + price.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Product{");
        sb.append("id=").append(id);
        sb.append(", supplier='").append(supplier.getName());
        sb.append("', name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", price=").append(price);
        sb.append('}');
        return sb.toString();
    }
}
