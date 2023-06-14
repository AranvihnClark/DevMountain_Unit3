package com.javaunit3.springmvc;

import com.javaunit3.springmvc.model.MovieEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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
        model.addAttribute("bestMovie", bestMovieService.getBestMovie().getTitle());
        return "bestMovie";
    }

    @RequestMapping("/voteForBestMovie")
    public String voteForBestMovie(HttpServletRequest request, Model model) {
        String movieTitle = request.getParameter("movieTitle");
        model.addAttribute("BestMovieVote", movieTitle);
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
