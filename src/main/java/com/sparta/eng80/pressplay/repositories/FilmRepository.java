package com.sparta.eng80.pressplay.repositories;

import com.sparta.eng80.pressplay.entities.FilmEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface    FilmRepository extends CrudRepository<FilmEntity, Integer> {

    @Query(value = "select * from film where title = ?", nativeQuery = true)
    Iterable<FilmEntity> findByTitle(String title);

    @Query(value = "select * from film f" +
            "inner join film_actor a on a.film_id = f.film_id where actor_id = ?", nativeQuery = true)
    Optional<FilmEntity> findByActor(int actorID);

    @Query(nativeQuery = true, value = "select * from film f" +
            "inner join film_actor fa on fa.film_id = f.film_id " +
            "inner join actor a on fa.actor_id = a.actor_id" +
            " where a.first_name like ?1")
    Iterable<FilmEntity> findByName(String name);

    @Query(nativeQuery = true, value = "select * from film f" +
            " inner join film_actor fa on fa.film_id = f.film_id" +
            " inner join actor a on fa.actor_id = a.actor_id" +
            " where a.first_name like ?1 and a.last_name like ?2")
    Iterable<FilmEntity> findByName(String firstName, String lastName);

    @Query(nativeQuery = true, value = "select * from film f " +
            "inner join language l on l.language_id = f.language_id " +
            "where l.name = ?")
    Iterable<FilmEntity> findByLanguage(String language);

    @Query(value = "SELECT f.* FROM film f JOIN (SELECT i.film_id FROM inventory i JOIN (SELECT r.inventory_id, COUNT(*) as rentalCount FROM rental r GROUP BY r.inventory_id) AS rentals ON i.inventory_id = rentals.inventory_id GROUP BY i.film_id ORDER BY SUM(rentals.rentalCount) DESC LIMIT 3) as rentals2 ON f.film_id = rentals2.film_id", nativeQuery = true)
    Iterable<FilmEntity> findTopNMostRentedFilms(int n);
}
