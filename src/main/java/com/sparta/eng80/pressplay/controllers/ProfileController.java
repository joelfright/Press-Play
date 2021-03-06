package com.sparta.eng80.pressplay.controllers;

import com.sparta.eng80.pressplay.entities.*;
import com.sparta.eng80.pressplay.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class ProfileController {

    private final CustomerService customerService;
    private final AddressService addressService;
    private final StaffService staffService;
    private final RentalService rentalService;
    private final SecurityService securityService;
    private final ActorService actorService;
    private final FilmService filmService;

    @Autowired
    public ProfileController(CustomerService customerService, AddressService addressService, RentalService rentalService, StaffService staffService, ActorService actorService, FilmService filmService, SecurityService securityService) {
        this.customerService = customerService;
        this.addressService = addressService;
        this.rentalService = rentalService;
        this.staffService = staffService;
        this.actorService = actorService;
        this.filmService = filmService;
        this.securityService = securityService;
    }

    @GetMapping("/profile")
    public String mainProfile(ModelMap model) {
        model = Filters.getFilters(model, filmService, actorService);

        UserEntity user = securityService.getCurrentUser();
        model.addAttribute("user", user);

        if (user instanceof CustomerEntity) {
            CustomerEntity customer = (CustomerEntity) user;
            Iterable<RentalEntity> rentalHistory = rentalService.findByCustomerId(customer.getCustomerId());
            Iterable<RentalEntity> overdueRentals = rentalService.findOverdueRentalsByCustomerId(customer.getCustomerId());
            Iterable<RentalEntity> currentRentals = rentalService.getCurrentRentals(customer.getCustomerId());

            rentalHistory.forEach(rental -> {
                overdueRentals.forEach(overdue -> {
                    if (rental.equals(overdue)) {
                        rental.setStatus(RentalEntity.OVERDUE);
                    }
                });

                if (rental.getStatus() != RentalEntity.OVERDUE) {
                    currentRentals.forEach(current -> {
                        if (rental.equals(current)) {
                            rental.setStatus(RentalEntity.CURRENT);
                        }
                    });
                }
            });
            model.addAttribute("rentalHistory", rentalHistory);
        }
        return "fragments/profile";
    }

     @PostMapping("/profile")
     public String edit(@ModelAttribute("user") UserEntity user) {
        String currentEmail = securityService.getCurrentUser().getEmail();
        if (securityService.authToken(currentEmail, user.getPassword()).isAuthenticated())  {
            Optional<CustomerEntity> currentDetailsOptional = customerService.findByEmail(currentEmail);
            if (currentDetailsOptional.isPresent()) {
                CustomerEntity currentDetails = currentDetailsOptional.get();

                String inputFirstName = user.getFirstName();
                if (inputFirstName != null) {
                    currentDetails.setFirstName(inputFirstName);
                }
                String inputLastName = user.getLastName();
                if (inputLastName != null) {
                    currentDetails.setLastName(inputLastName);
                }

                String inputEmail = user.getEmail();
                if (inputEmail != null) {
                    currentDetails.setEmail(inputEmail);
                }

                AddressEntity inputAddress = user.getAddress();
                if (inputAddress != null) {
                    Optional<AddressEntity> currentAddressOptional = addressService.findFullAddressByCustomerId(currentDetails.getCustomerId());
                    AddressEntity currentAddress;
                    if (currentAddressOptional.isPresent()) {
                        currentAddress = currentAddressOptional.get();

                        CityEntity inputCity = inputAddress.getCity();
                        CountryEntity inputCountry = inputCity.getCountry();

                        Optional<CityEntity> cityOptional = addressService.findCityByCityAndCountry(inputCity.getCity(), inputCountry.getCountry());
                        CityEntity city;
                        if (cityOptional.isPresent()) {
                            city = cityOptional.get();
                        } else {
                            city = new CityEntity();
                            city.setCity(inputCity.getCity());
                            Optional<CountryEntity> countryOptional = addressService.findCountryByName(inputCountry.getCountry());
                            CountryEntity country;
                            if (countryOptional.isPresent()) {
                                country = countryOptional.get();
                            } else {
                                country = new CountryEntity();
                                country.setCountry(inputCountry.getCountry());
                                addressService.saveCountry(country);
                            }
                            city.setCountry(country);
                            addressService.saveCity(city);
                        }

                        currentAddress.setAddress(inputAddress.getAddress());
                        currentAddress.setDistrict(inputAddress.getDistrict());
                        currentAddress.setPostalCode(inputAddress.getPostalCode());
                        currentAddress.setPhone(inputAddress.getPhone());
                        currentAddress.setCity(city);
                        addressService.saveAddress(currentAddress);
                        currentDetails.setAddress(currentAddress);
                    }
                }
                String inputPassword = user.getPassword();
                currentDetails.setPassword(inputPassword);
                customerService.save(currentDetails);
                SecurityContextHolder.getContext().setAuthentication(securityService.authToken(inputEmail, inputPassword));
            }
        }
        return "redirect:/profile";
     }
}
