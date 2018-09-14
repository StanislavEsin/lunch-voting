package kz.stanislav.voting.persistence.model;

import javax.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.NotNull;

/**
 * Dish.
 *
 * @author Stanislav (376825@gmail.com)
 * @since 13.08.2018
 */
@Entity
@Table(name = "dishes", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "price", "menu_id"}, name = "name_price_menu_idx")})
public class Dish extends NamedEntity {
    @Column(name = "price", nullable = false)
    @Range(min = 1)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private Menu menu;

    public Dish() {
    }

    public Dish(String name, @Range(min = 1) Integer price, @NotNull Menu menu) {
        this(null, name, price, menu);
    }

    public Dish(Integer id, String name, @Range(min = 1) Integer price, @NotNull Menu menu) {
        super(id, name);
        this.price = price;
        this.menu = menu;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", menu=" + menu.getId() +
                '}';
    }
}