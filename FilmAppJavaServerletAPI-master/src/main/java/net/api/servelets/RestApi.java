package net.api.servelets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.api.FilmDAO.FilmDAO;
import net.api.film.Film;

@WebServlet("/films/*")
public class RestApi extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FilmDAO filmDAO;

    public RestApi() {
        this.filmDAO = new FilmDAO();
    }
    

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String title = request.getParameter("title"); // Get the title parameter

        if (title != null && !title.isEmpty()) {
            // Search film by title
            try {
                searchByTitle(request, response, title);
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Internal Server Error");
            }
        } else if (pathInfo == null || pathInfo.equals("/")) {
            // Get all films
            try {
                listAllFilms(request, response);
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Internal Server Error");
            }
        } else {
            // Get film by ID
            String[] parts = pathInfo.split("/");
            if (parts.length != 2 || !parts[1].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid Request");
                return;
            }

            int id = Integer.parseInt(parts[1]);
            Film film = filmDAO.getFilmByID(id);
            if (film == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("Film not found");
                return;
            }

            // Checking the Accept header 
            String acceptHeader = request.getHeader("Accept");
            if (acceptHeader != null) {
                if (acceptHeader.contains("application/xml")) {
                    // Return XML response
                    String xmlResponse = generateXmlResponse(film);
                    response.setContentType("application/xml");
                    response.getWriter().println(xmlResponse);
                } else if (acceptHeader.contains("application/json")) {
                    // Return JSON response
                    Gson gson = new Gson();
                    String jsonFilm = gson.toJson(film);
                    response.setContentType("application/json");
                    response.getWriter().println(jsonFilm);
                } else if (acceptHeader.contains("text/plain")) {
                    // Return plain text response
                    String plainTextResponse = generatePlainTextResponse(film);
                    response.setContentType("text/plain");
                    response.getWriter().println(plainTextResponse);
                } else {
                    // Unsupported format
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    response.getWriter().println("Unsupported response format");
                }
            } else {
                // Returns JSON By default
                Gson gson = new Gson();
                String jsonFilm = gson.toJson(film);
                response.setContentType("application/json");
                response.getWriter().println(jsonFilm);
            }
        }
    }

    
    private String generateXmlResponse(Film films2) {
        ArrayList<Film> films = filmDAO.getAllFilms();
        // build XML response
        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlResponse.append("<films>\n");
        for (Film film1 : films) {
            xmlResponse.append("  <film>\n");
            xmlResponse.append("    <id>").append(film1.getId()).append("</id>\n");
            xmlResponse.append("    <title>").append(film1.getTitle()).append("</title>\n");
            xmlResponse.append("    <year>").append(film1.getYear()).append("</year>\n");
            xmlResponse.append("    <director>").append(film1.getDirector()).append("</director>\n");
            xmlResponse.append("    <stars>").append(film1.getStars()).append("</stars>\n");
            xmlResponse.append("    <review>").append(film1.getReview()).append("</review>\n");
            xmlResponse.append("  </film>\n");
        }
        xmlResponse.append("</films>");

        return xmlResponse.toString();
    }
    
    private String generateXmlResponseAllFilm(ArrayList<Film> films2) {
        // Retrieve films from DAO
        ArrayList<Film> films = filmDAO.getAllFilms();
        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlResponse.append("<films>\n");
        for (Film film1 : films) {
            xmlResponse.append("  <film>\n");
            xmlResponse.append("    <id>").append(film1.getId()).append("</id>\n");
            xmlResponse.append("    <title>").append(film1.getTitle()).append("</title>\n");
            xmlResponse.append("    <year>").append(film1.getYear()).append("</year>\n");
            xmlResponse.append("    <director>").append(film1.getDirector()).append("</director>\n");
            xmlResponse.append("    <stars>").append(film1.getStars()).append("</stars>\n");
            xmlResponse.append("    <review>").append(film1.getReview()).append("</review>\n");
            xmlResponse.append("  </film>\n");
        }
        xmlResponse.append("</films>");
        return xmlResponse.toString();
    }
    
    private String generatePlainTextResponse(Film film) {
        // create plain text response
        StringBuilder plainTextResponse = new StringBuilder();
        plainTextResponse.append("Film ID: ").append(film.getId()).append("\n");
        plainTextResponse.append("Title: ").append(film.getTitle()).append("\n");
        plainTextResponse.append("Year: ").append(film.getYear()).append("\n");
        plainTextResponse.append("Director: ").append(film.getDirector()).append("\n");
        plainTextResponse.append("Stars: ").append(film.getStars()).append("\n");
        plainTextResponse.append("Review: ").append(film.getReview()).append("\n");
        return plainTextResponse.toString();
    }
    
    private String generatePlainTextResponseAllFilm(ArrayList<Film> films) {
        //create plain text response
        StringBuilder plainTextResponse = new StringBuilder();
        
        for (Film film : films) {
            plainTextResponse.append("Film ID: ").append(film.getId()).append("\n");
            plainTextResponse.append("Title: ").append(film.getTitle()).append("\n");
            plainTextResponse.append("Year: ").append(film.getYear()).append("\n");
            plainTextResponse.append("Director: ").append(film.getDirector()).append("\n");
            plainTextResponse.append("Stars: ").append(film.getStars()).append("\n");
            plainTextResponse.append("Review: ").append(film.getReview()).append("\n");
            plainTextResponse.append("\n");
        }
        
        return plainTextResponse.toString();
    }
    


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Add a new film
            try {
                addFilm(request, response);
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Internal Server Error");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid Request");
        }
    }
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    String pathInfo = request.getPathInfo();

    if (pathInfo != null && !pathInfo.equals("/")) {
        // Update a film by ID
        String[] parts = pathInfo.split("/");
        if (parts.length != 2 || !parts[1].matches("\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid Request");
            return;
        }

        int id = Integer.parseInt(parts[1]);

        // Read JSON data from request body
        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        String jsonData = jsonBuilder.toString();

        // Parse JSON data into Film object
        Gson gson = new Gson();
        Film updatedFilm = gson.fromJson(jsonData, Film.class);

        // Update film using the parsed Film object
        try {
            updateFilm(request, response, id, updatedFilm);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal Server Error");
        }
    } else {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().println("Invalid Request");
    }
}



    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            // Delete a film by ID
            String[] parts = pathInfo.split("/");
            if (parts.length != 2 || !parts[1].matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Invalid Request");
                return;
            }

            int id = Integer.parseInt(parts[1]);
            try {
                deleteFilm(request, response, id);
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Internal Server Error");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid Request");
        }
    }

    private void listAllFilms(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        ArrayList<Film> films = filmDAO.getAllFilms();
        if (films.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("No films found");
        } else {
            // Check the Accept header for content negotiation
            String acceptHeader = request.getHeader("Accept");
            if (acceptHeader != null) {
                if (acceptHeader.contains("application/xml")) {
                    // Generate XML response
                    String xmlResponse =  generateXmlResponseAllFilm(films);
                    response.setContentType("application/xml");
                    response.getWriter().println(xmlResponse);
                } else if (acceptHeader.contains("application/json")) {
                    // Generate JSON response
                    Gson gson = new Gson();
                    String jsonFilms = gson.toJson(films);
                    response.setContentType("application/json");
                    response.getWriter().println(jsonFilms);
                } else if (acceptHeader.contains("text/plain")) {
                    // Generate plain text response
                    String plainTextResponse = generatePlainTextResponseAllFilm(films);
                    response.setContentType("text/plain");
                    response.getWriter().println(plainTextResponse);
                } else {
                    // Unsupported format
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    response.getWriter().println("Unsupported response format");
                }
            } else {
                // No Accept header provided, return JSON by default
                Gson gson = new Gson();
                String jsonFilms = gson.toJson(films);
                response.setContentType("application/json");
                response.getWriter().println(jsonFilms);
            }
        }
    }


    private void addFilm(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();
        Film newFilm = gson.fromJson(reader, Film.class);

        filmDAO.addFilm(newFilm);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().println("Film added successfully");
    }


  
    private void updateFilm(HttpServletRequest request, HttpServletResponse response, int id, Film updatedFilm) throws SQLException, IOException {
        if (updatedFilm == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid film data");
            return;
        }

        Film existingFilm = filmDAO.getFilmByID(id);
        if (existingFilm == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("Film not found");
        } else {
           
            String title = updatedFilm.getTitle();
            int year = updatedFilm.getYear();
            String director = updatedFilm.getDirector();
            String stars = updatedFilm.getStars();
            String review = updatedFilm.getReview();

            // Update the existing film 
            existingFilm.setTitle(title);
            existingFilm.setYear(year);
            existingFilm.setDirector(director);
            existingFilm.setStars(stars);
            existingFilm.setReview(review);

            // Update the film in the database
            filmDAO.updateFilmById(id, title, year, director, review);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Film updated successfully");
        }
    }


    private void deleteFilm(HttpServletRequest request, HttpServletResponse response, int id) throws SQLException, IOException {
        Film existingFilm = filmDAO.getFilmByID(id);
        if (existingFilm == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("Film not found");
        } else {
            filmDAO.deleteFilmById(id);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Film deleted successfully");
        }
    }
    
    private void searchByTitle(HttpServletRequest request, HttpServletResponse response, String title) throws SQLException, IOException {
        Film film = filmDAO.getFilmByTitle(title);
        if (film == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("Film not found");
            return;
        }

        // Check the Accept header for content negotiation
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null) {
            if (acceptHeader.contains("application/xml")) {
                // Generate XML response
                String xmlResponse = generateXmlResponse(film);
                response.setContentType("application/xml");
                response.getWriter().println(xmlResponse);
            } else if (acceptHeader.contains("application/json")) {
                // Generate JSON response
                Gson gson = new Gson();
                String jsonFilm = gson.toJson(film);
                response.setContentType("application/json");
                response.getWriter().println(jsonFilm);
            } else if (acceptHeader.contains("text/plain")) {
                // Generatetext response
                String plainTextResponse = generatePlainTextResponse(film);
                response.setContentType("text/plain");
                response.getWriter().println(plainTextResponse);
            } else {
                // Unsupported format
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                response.getWriter().println("Unsupported response format");
            }
        } else {
            // No Accept header provided, return JSON by default
            Gson gson = new Gson();
            String jsonFilm = gson.toJson(film);
            response.setContentType("application/json");
            response.getWriter().println(jsonFilm);
        }
    }

}
