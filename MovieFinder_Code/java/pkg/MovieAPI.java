package pkg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MovieAPI {
	String API_KEY = "06535c6c8c81552b1719cfb52a7b381c";
	String urlMovieData = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + API_KEY + "&language=en-US"
			+ "&region=US";
	String urlGenreData = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + API_KEY + "&language=en-US";
	String bucketName = "program5moviefinder";
	AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

	public static Map<String, Object> jsonToMap(String str) {
		Map<String, Object> map = new Gson().fromJson(str, new TypeToken<HashMap<String, Object>>() {
		}.getType());
		return map;
	}
	/*
	 * Create a bucket and blobs to hold movie information gathered from themoviedb api. The function checks if the s3 blob has 
	 * been more than one day since the blob was modified. If it has been, the information from the api is gathered again and 
	 * stored in the s3 blob and returns the array of new movies gathered. 
	 * */
	@SuppressWarnings("unchecked")
	public ArrayList<Movie> fetchMovieData() 
	{
		StringBuilder movieDataString = new StringBuilder();
		ArrayList<Movie> upcomingMovieList = new ArrayList<>();
		String key = "movie";
		try 
		{
			boolean exists = doesBucketExist();

			if (exists) 
			{
				if (doesKeyExist(key)) 
				{

					S3Object data = s3.getObject(bucketName, key);
					Date lastModified = data.getObjectMetadata().getLastModified();
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
					Date todayDate = new Date();
					//positive if first greater, else negative 
					int compareTo = sdf.format(lastModified).compareTo(sdf.format(todayDate));
					
					if (compareTo >= 0) //no day passed 
					{
						return null;
					}
				}
			}
			else //bucket not exist
			{
				createBucket();
			}

			movieDataString = fetchData(urlMovieData);
			if (movieDataString.toString() == null)
			{
				S3Object data = s3.getObject(bucketName, key);
				InputStream movieData = data.getObjectContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(movieData));
				String s;
				while ((s = reader.readLine()) != null) {
						movieDataString.append(s);
				}
			}
			storeInS3(movieDataString.toString(), key);
			String pageToQuery = "&page=";
			Map<String, Object> respMap = jsonToMap(movieDataString.toString());
			Double pages = (Double) (respMap.get("total_pages"));
			Integer pageCount = 1;
			ArrayList<Map<String, Object>> movieDetails = new ArrayList<Map<String, Object>>();
			while (pageCount <= pages) {
				String url = urlMovieData.concat(pageToQuery).concat(pageCount.toString());
				Map<String, Object> moviesMap = jsonToMap(fetchData(url).toString());
				ArrayList<Map<String, Object>> pageWiseMovies = (ArrayList<Map<String, Object>>) moviesMap
						.get("results");
				movieDetails.addAll(pageWiseMovies);
				pageCount++;
			}

			Map<Object, String> genreMap = getGenres();
			String poster_url = "https://image.tmdb.org/t/p/original";
			for (Map<String, Object> movie : movieDetails) {
				Movie upcomingMovie = new Movie();
				upcomingMovie.movie_id = (Double) movie.get("id");
				upcomingMovie.title = (String) movie.get("title");

				System.out.println(upcomingMovie.title);
				upcomingMovie.release_date = (String) movie.get("release_date");
				upcomingMovie.overview = (String)movie.get("overview");
				if (upcomingMovie.overview == null)
					upcomingMovie.overview = "N/A";
				ArrayList<Double> genre_ids = (ArrayList<Double>) movie.get("genre_ids");
				if (!genre_ids.isEmpty()) {
					for (Double id : genre_ids) {
						upcomingMovie.genres.add(genreMap.get(id));
					}
				}
				if (movie.get("poster_path") != null)
					upcomingMovie.posterpath = poster_url + (String) movie.get("poster_path");
				else
					upcomingMovie.posterpath = "https://images.atomtickets.com/image/upload/w_520,h_780,q_auto/ingestion-images-archive-prod/archive/coming_soon_poster.jpg";
				upcomingMovieList.add(upcomingMovie);
			}
			return upcomingMovieList;

		} catch (IOException e) {

			return upcomingMovieList;
		}
	}
	/*
	 * Create a bucket and blobs to hold genre information gathered from themoviedb api. The function checks if the s3 blob has 
	 * been more than one day since the blob was modified. If it has been, the information from the api is gathered again and 
	 * stored in the s3 blob and returns the array of genres gathered. 
	 * */
	@SuppressWarnings("unchecked")
	private Map<Object, String> getGenres() {
		StringBuilder genreData = new StringBuilder();
		String key = "genre";
		try {
			boolean exists = doesBucketExist();
			if (exists) {
				if (doesKeyExist(key)) {
					S3Object data = s3.getObject(bucketName, key);
					Date lastModified = data.getObjectMetadata().getLastModified();
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
					Date todayDate = new Date();
					int compareTo = sdf.format(lastModified).compareTo(sdf.format(todayDate));
					if (compareTo <= 0) 
					{ //no day pass
						return null;
					}
				} else {
					genreData = fetchData(urlGenreData);
					storeInS3(genreData.toString(), key);
				}
			} else {
				createBucket();
				genreData = fetchData(urlGenreData);
				storeInS3(genreData.toString(), key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> respMap = jsonToMap(genreData.toString());
		Map<Object, String> genreMap = new HashMap<Object, String>();
		ArrayList<Map<String, String>> genres = new ArrayList<>();
		genres = (ArrayList<Map<String, String>>) respMap.get("genres");
		for (Map<String, String> genre : genres) {
			Object id = genre.get("id");
			String genreName = genre.get("name");
			genreMap.put(id, genreName);
		}
		return genreMap;
	}
	//Checks if key exists in bucket
	private boolean doesKeyExist(String key) {
		try
		{
			return s3.doesObjectExist(bucketName, key);
		}
		catch (AmazonS3Exception e) {}
		return false;
	}
	//Creates bucket 
	private void createBucket()
	{
		Bucket a = null;
		try
		{
			CreateBucketRequest request = new CreateBucketRequest(bucketName);

            Bucket bucket = s3.createBucket(request);
		}
		catch(AmazonS3Exception e) {}
	}
	//Stores string in s3 blob 
	private void storeInS3(String result, String stringObjKeyName) {
		try 
		{
			s3.putObject(bucketName, stringObjKeyName, result.toString());
		
		}catch(AmazonS3Exception e) {}
	}
	//Checks if specified bucket exists
	private boolean doesBucketExist() {
		boolean existing = false;
		for (Bucket bucket : s3.listBuckets()) {
			if (bucket.getName().equals(bucketName))
				existing = true;
		}
		return existing;
	}

	//reads data from url provided
	private StringBuilder fetchData(String urlString) throws MalformedURLException, IOException {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			
			result.append(line);
		}
		br.close();
		return result;
	}

}
