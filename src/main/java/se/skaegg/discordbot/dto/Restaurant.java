package se.skaegg.discordbot.dto;

public class Restaurant {

    String name;
    String url;
    String openingHours;
    String address;
    String phone;
    String pricing;
    String website;
    String photo;
    String rating;
    String reviewText;
    String reviewByline;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating != null ? ":star: " + rating : "";
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address != null ? ":homes: " + address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url != null ? url : "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhoto() {
        return photo != null ? photo : "";
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOpeningHours() {
        return openingHours != null ? ":clock1: " + openingHours : "";
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getPhone() {
        return phone != null ? ":telephone: " + phone : "";
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPricing() {
        return pricing != null ? ":dollar: " + pricing : "";
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public String getWebsite() {
        return website != null ? ":globe_with_meridians: " + website : "";
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getReviewText() {
        return reviewText != null ? reviewText : "";
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getReviewByline() {
        return reviewByline != null ? reviewByline : "";
    }

    public void setReviewByline(String reviewByline) {
        this.reviewByline = reviewByline;
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
