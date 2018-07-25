package kz.stanislav.voting.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@MappedSuperclass
public abstract class NamedEntity extends BaseEntity {
    @NotBlank
    @Size(min = 1, max = 250)
    @Column(name = "name", nullable = false)
    protected String name;

    protected NamedEntity() {
    }

    protected NamedEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("Entity %s (%s, '%s')", getClass().getName(), id, name);
    }
}