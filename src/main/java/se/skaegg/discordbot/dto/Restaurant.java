package se.skaegg.discordbot.dto;

public class Restaurant {

    String name;
    double rating;
    String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "**" + name + "**" +
                "\n" +
                address +
                "\n" +
                "Betyg: " + rating;
    }
}
