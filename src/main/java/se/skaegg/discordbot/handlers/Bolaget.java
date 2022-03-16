package se.skaegg.discordbot.handlers;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import reactor.core.publisher.Mono;

public class Bolaget implements EventHandler{

    @Override
    public Mono<Void> process(Message eventMessage) {

        String queryParam = eventMessage.getContent().replaceAll("(?i)!bolaget ", "");

        String image;
        String productDescription;
        String productHeader;

        //Searchpage
        //JBrowser loads the page and runs all scripts on the page, which jsoup cant handle
        JBrowserDriver driver = new JBrowserDriver(Settings.builder().headless(true).build());
        driver.get("https://www.systembolaget.se/sok/?textQuery=" + queryParam);
        String searchPage = driver.getPageSource();

        //Jsoup parses the loaded page
        Document searchDoc = Jsoup.parse(searchPage);
        Elements linksInMainSearch = searchDoc.select("main").select("a[href]");
        String firstLinkInSearch = "https://www.systembolaget.se" + linksInMainSearch.first().attr("href");

        //Check if there was any search result
        if (!searchDoc.select("main").select("div#noSearchResult").hasText()) {

            //Product page
            driver.get(firstLinkInSearch);
            String productPage = driver.getPageSource();
            Document productDoc = Jsoup.parse(productPage);
            driver.quit();
            Element main = productDoc.select("main").first();

            Elements productImages = main.select("img[src]");
            image = productImages.first().attr("src");

            StringBuilder sb = new StringBuilder();
            Element h1 = main.select("h1").first();
            for (Element e : h1.select("span")) {
                sb.append(e.text());
                sb.append("\n");
            }

            productHeader = sb.toString();


            productDescription = "";
            Elements h1Divs = h1.parent().select("div.css-1tqlcdc");
            if (!h1Divs.isEmpty()) {
                Element descriptionDiv = h1.parent().select("div.css-1tqlcdc").first();
                productDescription = descriptionDiv.text();
            }
        }

        else {
            driver.quit();
            image = "";
            productHeader = "Inga träffar jao";
            productDescription = "Buuu!";
        }

        String finalProductDescription = productDescription;


        return Mono.just(eventMessage)
            .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
            .flatMap(message -> message.getChannel().flatMap(channel ->
                    channel.createEmbed(spec ->
                            spec.setColor(Color.CYAN)
                                    .setImage(image)
                                    .setUrl(firstLinkInSearch)
                                    .setDescription(finalProductDescription)
                                    .setTitle(productHeader)))
            )
            .then();
    }
}
