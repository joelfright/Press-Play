package com.sparta.eng80.pressplay.services.interfaces;

import java.util.Optional;

public interface ServiceInterface<T> {

    Optional<T> findById(Integer id);
    Iterable<T> findAll();
    int save(T t);

}
