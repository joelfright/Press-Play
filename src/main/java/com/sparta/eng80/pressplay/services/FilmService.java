package com.sparta.eng80.pressplay.services;

import com.sparta.eng80.pressplay.entities.CategoryEntity;
import com.sparta.eng80.pressplay.entities.FilmEntity;
import com.sparta.eng80.pressplay.entities.LanguageEntity;
import com.sparta.eng80.pressplay.repositories.CategoryRepository;
import com.sparta.eng80.pressplay.repositories.FilmRepository;
import com.sparta.eng80.pressplay.repositories.LanguageRepository;
import com.sparta.eng80.pressplay.services.interfaces.FilmInterface;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.ArrayList;

@Service
public class FilmService implements FilmInterface {

    private final FilmRepository filmRepository;
    private final CategoryRepository categoryRepository;
    private final LanguageRepository languageRepository;

    public FilmService(FilmRepository filmRepository, CategoryRepository categoryRepository, LanguageRepository languageRepository) {
        this.filmRepository = filmRepository;
        this.categoryRepository = categoryRepository;
        this.languageRepository = languageRepository;
    }

    public Iterable<FilmEntity> findByCategory(int categoryID) {
        if (categoryID < 0) {
            return new ArrayList<>();
        }
        Iterable<CategoryEntity> AllCategories = categoryRepository.findAll();
        boolean found = false;
        for (CategoryEntity categoryEntity : AllCategories) {
            if (categoryEntity.getCategoryId() == categoryID) {
                categoryID = categoryEntity.getCategoryId();
                found = true;
                break;
            }
        }
        if (!found) {
            // genre doesn't exist, return empty list
            return new ArrayList<>();
        }
        Iterable<CategoryEntity> AllFilmCategorys = categoryRepository.findAll();

        ArrayList<FilmEntity> allFilmIdsThatBelongToGenre = new ArrayList<>();
        for (CategoryEntity filmCategoryEntity : AllFilmCategorys) {
            if (filmCategoryEntity.getCategoryId() == categoryID) {
                allFilmIdsThatBelongToGenre.addAll(filmCategoryEntity.getFilms());
            }
        }
        return allFilmIdsThatBelongToGenre;
    }

    public Iterable<FilmEntity> findByCategory(String genre) {
        if (genre == null || genre.equals("")) {
            // return empty list
            return new ArrayList<>();
        }
        Iterable<CategoryEntity> AllCategories = categoryRepository.findAll();
        for (CategoryEntity categoryEntity : AllCategories) {
            if (categoryEntity.getName().equals(genre)) {
                return findByCategory(categoryEntity.getCategoryId());
            }
        }
        return new ArrayList<>();
    }

    public Iterable<CategoryEntity> findAllGenres() {
        return categoryRepository.findAll();
    }

    @Override
    public Iterable<FilmEntity> findByTitle(String title) {
        return filmRepository.findByTitle(title);
    }

    @Override
    public Optional<FilmEntity> findActorById(int actorID) {
        if(actorID < 0){
            return null;
        }
        return filmRepository.findByActor(actorID);
    }

    @Override
    public Iterable<FilmEntity> findActorByName(String actor) {
        if(actor.split(" ").length == 2){
            String[] actorName = actor.split(" ");
            actorName[0] = "%" + actorName[0] + "%";
            actorName[1] = "%" + actorName[1] + "%";
            return filmRepository.findByName(actorName[0], actorName[1]);
        }
        actor = "%" + actor + "%";
        return filmRepository.findByName(actor);
    }


    @Override
    public Iterable<FilmEntity> findByLanguage(String language) {
        return filmRepository.findByLanguage(language);
    }

    @Override
    public Iterable<FilmEntity> findTopNMostRentedFilms(int n) {
        return filmRepository.findTopNMostRentedFilms(n);
    }

    @Override
    public Optional<FilmEntity> findById(Integer id) {
        return filmRepository.findById(id);
    }

    @Override
    public Iterable<FilmEntity> findAll() {
        return filmRepository.findAll();
    }

    public Iterable<LanguageEntity> findAllLanguages(){
        return languageRepository.findAll();
    }


    @Override
    public int save(FilmEntity filmEntity) {
        boolean found = false;
        Iterable<FilmEntity> AllFilms = findAll();
        for (FilmEntity existingFilms : AllFilms) {
            if (existingFilms.equals(filmEntity)) {
                found = true;
                break;
            }
        }
        if (!found) {
            return filmRepository.save(filmEntity).getFilmId();
        }
        return -1;
    }


}
