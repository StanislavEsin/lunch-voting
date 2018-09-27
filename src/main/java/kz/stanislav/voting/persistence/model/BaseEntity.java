package kz.stanislav.voting.persistence.model;

import kz.stanislav.voting.mark.HasId;
import java.util.Objects;
import javax.persistence.MappedSuperclass;
import javax.persistence.Access;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.AccessType;
import javax.persistence.GenerationType;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class BaseEntity implements HasId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    protected BaseEntity() {
    }

    protected BaseEntity(final Integer id) {
        this.id = id;
    }

    @Override
    public void setId(final Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Entity %s (%s)", getClass().getName(), id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseEntity)) {
            return false;
        }
        final BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}