package se.skaegg.discordbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OmdbTitleSearchResultDTO {

    String title;
    String year;
    String rated;
    String released;
    String runtime;
    String genre;
    String director;
    String writer;
    String actors;
    String plot;
    String language;
    String country;
    String awards;
    String poster;
    List<OmdbRating> ratings;
    String metascore;
    String imdbRating;
    String imdbVotes;
    String imdbID;
    String type;
    String dvd;
    String boxOffice;
    String production;
    String website;
    String response;
    String error;
    String totalSeasons;

    public String getError() {
        return error;
    }

    @JsonProperty("Error")
    public void setError(String error) {
        this.error = error;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty("Title")
    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    @JsonProperty("Year")
    public void setYear(String year) {
        this.year = year;
    }

    public String getRated() {
        return rated;
    }

    @JsonProperty("Rated")
    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getReleased() {
        return released;
    }

    @JsonProperty("Released")
    public void setReleased(String released) {
        this.released = released;
    }

    public String getRuntime() {
        return runtime;
    }

    @JsonProperty("Runtime")
    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    @JsonProperty("Genre")
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    @JsonProperty("Director")
    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    @JsonProperty("Writer")
    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return actors;
    }

    @JsonProperty("Actors")
    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    @JsonProperty("Plot")
    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguage() {
        return language;
    }

    @JsonProperty("Language")
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    @JsonProperty("Country")
    public void setCountry(String country) {
        this.country = country;
    }

    public String getAwards() {
        return awards;
    }

    @JsonProperty("Awards")
    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPoster() {
        return poster;
    }

    @JsonProperty("Poster")
    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<OmdbRating> getRatings() {
        return ratings;
    }

    @JsonProperty("Ratings")
    public void setRatings(List<OmdbRating> ratings) {
        this.ratings = ratings;
    }

    public String getMetascore() {
        return metascore;
    }

    @JsonProperty("Metascore")
    public void setMetascore(String metascore) {
        this.metascore = metascore;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    @JsonProperty("imdbRating")
    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    @JsonProperty("imdbVotes")
    public void setImdbVotes(String imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public String getImdbID() {
        return imdbID;
    }

    @JsonProperty("imdbID")
    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getType() {
        return type;
    }
    @JsonProperty("Type")
    public void setType(String type) {
        this.type = type;
    }

    public String getDvd() {
        return dvd;
    }

    @JsonProperty("DVD")
    public void setDvd(String dvd) {
        this.dvd = dvd;
    }

    public String getBoxOffice() {
        return boxOffice;
    }

    @JsonProperty("BoxOffice")
    public void setBoxOffice(String boxOffice) {
        this.boxOffice = boxOffice;
    }

    public String getProduction() {
        return production;
    }

    @JsonProperty("Production")
    public void setProduction(String production) {
        this.production = production;
    }

    public String getWebsite() {
        return website;
    }

    @JsonProperty("Website")
    public void setWebsite(String website) {
        this.website = website;
    }

    public String getResponse() {
        return response;
    }

    @JsonProperty("Response")
    public void setResponse(String response) {
        this.response = response;
    }

    public String getTotalSeasons() {
        return totalSeasons;
    }

    @JsonProperty("totalSeasons")
    public void setTotalSeasons(String totalSeasons) {
        this.totalSeasons = totalSeasons;
    }
}
