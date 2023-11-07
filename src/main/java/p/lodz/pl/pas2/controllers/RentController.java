package p.lodz.pl.pas2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import p.lodz.pl.pas2.model.Movie;
import p.lodz.pl.pas2.model.Request.RentRequest;
import p.lodz.pl.pas2.model.Rent;
import p.lodz.pl.pas2.model.User;
import p.lodz.pl.pas2.services.MovieService;
import p.lodz.pl.pas2.services.RentService;
import p.lodz.pl.pas2.services.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/rent")
public class RentController {

    private final RentService rentService;
    private final UserService userService;
    private final MovieService movieService;

    @Autowired
    public RentController(RentService rentService, UserService userService, MovieService movieService) {
        this.rentService = rentService;
        this.userService = userService;
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<Rent> addRent(@RequestBody RentRequest rentRequest) {
        User user = userService.getUser(rentRequest.getClientID());
        Movie movie = movieService.getMovie(rentRequest.getMovieID());
        if(user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        if(movie == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        if(!user.isActive()) return ResponseEntity.status(HttpStatus.LOCKED).body(null);
        Rent rent = new Rent(user, movie, rentRequest.getStartDate(), rentRequest.getEndDate());
        Rent addedRent = rentService.addRent(rent);
        if(addedRent == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedRent);
    }

    @GetMapping("/current")
    public ResponseEntity<List<Rent>> getCurrentRents() {
        return ResponseEntity.status(HttpStatus.OK).body(rentService.getCurrentRents());
    }

    @GetMapping("/past")
    public ResponseEntity<List<Rent>> getPastRents() {
        return ResponseEntity.status(HttpStatus.OK).body(rentService.getPastRents());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteRent(@PathVariable UUID id) {
        boolean deleteStatus = rentService.deleteRent(id);
        if(!deleteStatus) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}