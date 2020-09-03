package pkg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class Movie
{

	public Double movie_id;
	public String title;
	public ArrayList<Double> genre_ids; 
	public ArrayList<String> genres;
	public String release_date;
	public String posterpath;
	public String overview;
	
	Movie()
	{
		this.title=new String();
		this.genre_ids = new ArrayList<>();
		this.genres = new ArrayList<>();
		this.release_date = new String();
		this.posterpath = new String();
		this.overview = new String();
	}
	
	public String toString()
	{
		return "Title: " + this.title + " Release Date: " + this.release_date + " Genres: " + this.genres.toString();
	}
}
//sort movies by title 
class Sortbytitle implements Comparator<Movie> 
{ 
    public int compare(Movie a, Movie b) 
    { 
    	return a.title.compareTo(b.title);
    } 
} 
//sort movies by release date 
class Sortbyreleasedate implements Comparator<Movie> 
{ 
    public int compare(Movie a, Movie b) 
    { 
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	try 
    	{
			java.util.Date date1  = (java.util.Date)formatter.parse(a.release_date);
			java.util.Date date2  = (java.util.Date)formatter.parse(b.release_date);
			return date1.compareTo(date2);
		} catch (ParseException e) {}
    	return -1;
    } 
} 