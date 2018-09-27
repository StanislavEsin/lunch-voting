package kz.stanislav.voting.web.dto;

import kz.stanislav.voting.mark.HasId;

public abstract class BaseDto implements HasId {
    protected Integer id;

    public BaseDto() {
    }

    public BaseDto(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}