package net.api.FilmDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import net.api.film.Film;


public class FilmDAO {
	Film film = new Film();
	Film oneFilm = null;
	Connection conn = null;
    Statement stmt = null;
    String user = "odugbesa";
    String password = "diStrelg9";
    String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/"+user;
    /*
     * Using cloud based mysql for testing 
     * */
    /*String user = "adeyemoymmu";
    String password = "yetundemmu";
    String url ="jdbc:mysql://awseb-e-usup6ktgea-stack-awsebrdsdatabase-kudt7rpnjpwr.c5qeeemoijfd.eu-north-1.rds.amazonaws.com:3306/ebdb";*/
 
	private void openConnection(){
		// loading jdbc driver for mysql
		try{
		    Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch(Exception e) { System.out.println(e); }

		// connecting to database
		try{
			// connection string for demos database, username demos, password demos
 			conn = DriverManager.getConnection(url, user, password);
		    stmt = conn.createStatement();
		} catch(SQLException se) { System.out.println(se); }	   
    }
	private void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Film getNextFilm(ResultSet rs){
    	Film thisFilm=null;
		try {
			thisFilm = new Film(
					rs.getInt("id"),
					rs.getString("title"),
					rs.getInt("year"),
					rs.getString("director"),
					rs.getString("stars"),
					rs.getString("review"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return thisFilm;		
	}
	
	
	
   public ArrayList<Film> getAllFilms(){
	   
		ArrayList<Film> allFilms = new ArrayList<Film>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from films";
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneFilm = getNextFilm(rs1);
		    	allFilms.add(oneFilm);
		   }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allFilms;
   }

   public Film getFilmByID(int id){
	   
		openConnection();
		oneFilm=null;
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from films where id="+id;
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneFilm = getNextFilm(rs1);
		    }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return oneFilm;
   }

   public Film addFilm( Film film){
		openConnection();
		try{
			String insertSQL= "INSERT INTO films (id,title, year, director, stars, review) VALUES (?, ?, ?, ?, ?, ?)";
			//ResultSet rs1 = stmt.executeQuery(inserySQL);
			long currentTimeMillis = System.currentTimeMillis();
			int lastFiveDigits = (int) (currentTimeMillis % 100000);
			int id = Integer.parseInt(String.format("%05d", lastFiveDigits));
			film.setId(id);
			PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);
			preparedStatement.setInt(1, film.getId());
			preparedStatement.setString(2,film.getTitle());
			preparedStatement.setInt(3,film.getYear());
			preparedStatement.setString(4,film.getDirector());
			preparedStatement.setString(5,film.getStars());
			preparedStatement.setString(6,film.getReview());
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0){
				System.out.print("no rows affected");
			} else {
				System.out.print("failed to add new film");
			}
			return null;
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		} finally {
			closeConnection();
		}
   }

   public void updateFilmById(int id, String title, int year, String director, String review) {
	    openConnection();
	    try {
	        String checkExistsSQL = "SELECT COUNT(*) FROM films WHERE id=?";
	        PreparedStatement checkIfFilmExists = conn.prepareStatement(checkExistsSQL);
	        checkIfFilmExists.setInt(1, id);
	        ResultSet resultSet = checkIfFilmExists.executeQuery();
	        resultSet.next();
	        int count = resultSet.getInt(1);
	        if (count == 0) {
	            System.out.println("Film with ID: " + id + " does not exist.");
	            return;
	        }
	        String updateSQL = "UPDATE films SET title=?, year=?, director=?, review=? WHERE id=?";
	        PreparedStatement preparedStatement = conn.prepareStatement(updateSQL);
	        preparedStatement.setString(1, title);
	        preparedStatement.setInt(2, year);
	        preparedStatement.setString(3, director);
	        preparedStatement.setString(4, review);
	        preparedStatement.setInt(5, id);

	        int rowsAffected = preparedStatement.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Film updated successfully");
	        } else {
	            System.out.println("Failed to update film");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        closeConnection();
	    }
	}


   public void deleteFilmById(int id) {
	    openConnection();
	    try {
	        String deleteFilmSQL = "DELETE FROM films WHERE id=?";
	        PreparedStatement preparedStatement = conn.prepareStatement(deleteFilmSQL);
	        preparedStatement.setInt(1, id);
	        int rowsDeleted = preparedStatement.executeUpdate();
	        if (rowsDeleted > 0) {
	            System.out.print("Deleted Successfully");
	        } else {
	            System.out.print("Failed to delete");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        closeConnection();
	    }
	}
   
   public Film getFilmByTitle(String title) {
	    openConnection();
	    Film film = null;
	    try {
	        String selectSQL = "SELECT * FROM films WHERE title=?";
	        PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
	        preparedStatement.setString(1, title);
	        ResultSet rs = preparedStatement.executeQuery();
	        
	        if (rs.next()) {
	            film = new Film(
	                    rs.getInt("id"),
	                    rs.getString("title"),
	                    rs.getInt("year"),
	                    rs.getString("director"),
	                    rs.getString("stars"),
	                    rs.getString("review"));
	        }
	        
	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        closeConnection();
	    }
	    return film;
	}

}


