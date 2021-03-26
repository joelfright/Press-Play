package com.sparta.eng80.pressplay.controllers;

import com.sparta.eng80.pressplay.entities.CategoryEntity;
import com.sparta.eng80.pressplay.entities.FilmEntity;
import com.sparta.eng80.pressplay.services.CategoryService;
import com.sparta.eng80.pressplay.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    FilmService filmService;

    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public String findCategories(ModelMap modelMap){
        Iterable<CategoryEntity> categories = filmService.findAllGenres();
        List<Integer> categorySizes = new ArrayList<>();
        for (CategoryEntity category : categories) {
            categorySizes.add((int)filmService.findByCategory(category.getCategoryId()).spliterator().getExactSizeIfKnown());
        }
        modelMap.addAttribute("categories", categories);
        modelMap.addAttribute("sizes", categorySizes);
        return "fragments/categories";
    }

    @GetMapping("/category")
    public String findCategory(@RequestParam("category") String category, ModelMap modelMap){
        Iterable<FilmEntity> filmEntities = filmService.findByCategory(category);
        modelMap.addAttribute("films", filmEntities);
        return "fragments/results";
    }

    @PostMapping("/newCategory")
    public String newCategory(@RequestParam("name") String name){
        categoryService.addCategory(name);
        return "redirect:/categories";
    }
}