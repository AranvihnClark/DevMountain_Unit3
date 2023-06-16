package com.javaunit3.springmvc;

import com.javaunit3.springmvc.model.MovieEntity;
import com.javaunit3.springmvc.model.VoteEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class MovieController {

    @Autowired
    BestMovieService bestMovieService;

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @RequestMapping("/bestMovie")
    public String getBestMoviePage(Model model) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        // Creates a variable that holds all our movies from the database.
        List<MovieEntity> movieEntityList = session.createQuery("from MovieEntity").list();

        // We then sort the movies based on the number of votes each movie has.
        movieEntityList.sort(Comparator.comparing(movieEntity -> movieEntity.getVotes().size()));

        // We grab the movie with the most votes.
        MovieEntity mostVotes = movieEntityList.get(movieEntityList.size() - 1);

        // Since they wanted us grab all attributes with voters as well... We will display all voters as well.
        List<String> voters = new ArrayList<>();

        // We then add the vote names and add it to our string list above.
        for (VoteEntity vote : mostVotes.getVotes()) {
            voters.add(vote.getVoterName());
        }

        // I wasn't going to print out the names as below but it was how the example was described, so I figured I'd just keep it as such.
        String voterList = String.join(",", voters);

        // THis prints out the attributes for us on the html side.
        model.addAttribute("bestMovie", mostVotes.getTitle());
        model.addAttribute("bestMovieVoters", voterList);

        //model.addAttribute("bestMovie", bestMovieService.getBestMovie().getTitle());
        return "bestMovie";
    }

    // Was going to have the mapping be "/voteForBestMovieFormPage" but the example listed it without the 'page' so I changed it.

    @RequestMapping("/voteForBestMovieForm")
    public String voteForBestMovieFormPage(Model model)
    {
        // Creates a 'session' to store our movie to a table.
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        // We create a variable to store the all the movies in our database.
        List<MovieEntity> movieEntityList = session.createQuery("from MovieEntity").list();

        // Then we add the attribute "movies" of our database of movies.
        session.getTransaction().commit();
        model.addAttribute("movies", movieEntityList);

        return "voteForBestMovie";
    }

    @RequestMapping("/voteForBestMovie")
    public String voteForBestMovie(HttpServletRequest request, Model model) {
        // Grabs the user input, thought the parameter hasn't been made yet, but I will add it before the end of this project.
        String movieId = request.getParameter("movieId");
//        String movieId = "1";
        String name = request.getParameter("voterName");
//        System.out.println(movieId);

        // New session to grab the current factory session.
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        // We create a movie entity variable to grab the movie id.
        MovieEntity movieEntity = (MovieEntity) session.get(MovieEntity.class, Integer.parseInt(movieId));

        // We create a new variable to hold the vote info.
        VoteEntity vote = new VoteEntity();

        // We set the vote up to be the inputted name
        vote.setVoterName(name);

        // Then we add the voter to the movie id.
        movieEntity.addVote(vote);

        // We update the database with our updated voter.
        session.update(movieEntity);

        // We confirm the changes.
        session.getTransaction().commit();

//        String movieTitle = request.getParameter("movieTitle");
//        model.addAttribute("BestMovieVote", movieTitle);
        return "voteForBestMovie";
    }

    @RequestMapping("/addMovieForm")
    public String addMovieForm() {
        return "addMovie";
    }

    @RequestMapping("/addMovie")
    public String addMovie(HttpServletRequest request)
    {
        // Assigns local variables with strings we inputted from the html.
        // Though I feel it's not really necessary because we can just push this straight into our movie entity below and save on memory,
        // But I guess it wouldn't look as nice...
        String movieTitle = request.getParameter("movieTitle");
        String maturityRating = request.getParameter("maturityRating");
        String genre = request.getParameter("genre");

        // A new MovieEntity is created (which just means a new entry in our table is created).
        MovieEntity movieEntity = new MovieEntity();

        // I wonder why we do it like below to set the column info in our table and why we don't just create a constructor.
        // But, thinking now, I guess if it gets bigger it would be an ugly constructor call.
        movieEntity.setTitle(movieTitle);
        movieEntity.setMaturityRating(maturityRating);
        movieEntity.setGenre(genre);

        // Creates a new session factory to 'Update' our persistent table...
        // Because sessionFactory was autowired, I believe it will automatically grab the 'movies' table -- if I'm understanding this right.
        // Since our only SessionFactory 'bean' was for MovieEntity, I think I understood this correctly.
        // The '.getCurrentSession' will automatically decide what table we need based on the 'current' session or location we are at in the html.
        Session session = sessionFactory.getCurrentSession();

        // Starts the updating process (though I guess it would be more accurate to say this starts the transfer process)
        session.beginTransaction();

        // We save our new row to the table.
        session.save(movieEntity);
        session.getTransaction().commit();

        return "addMovie";
    }
}
