package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

public class Help implements EventHandler {
    @Override
    public Mono<Void> process(Message eventMessage) {

        String helpText = "" +
                "`!ping` - Kolla om jag lever\n" +
                "`!bolagetöppet` - Öppettider för Bolaget i Norrtälje\n" +
                "`!codenames` - Skapar en länk för Codenames på horsepaste.com\n" +
                "`!lag [Namn kommaseparerat]` - Slumpar fram 2 lag utifrån namnen som angivits\n" +
                "`!lagvoice` - Tar alla namn som är i någon voicekanal och slumpar 2 lag\n" +
                "`!fredag` - Är det fredag?\n" +
                "`!film [Film- eller seriennamn på orginalspråk]` - Visa information om film/serie från OMDB api\n" +
                "`!nedräkning [Namn på nedräkning]` - Visar hur lång tid det är kvar till datumet på nedräkningen\n" +
                "`!nynedräkning [Namn på nedräkning, datum i format yyyy-MM-dd HH:mm]` - Lägger till nedräkning\n" +
                "`!nedräkningar` - Listar nedräkningar med namn och ID\n" +
                "`!tabortnedräkning [ID]` - Tar bort nedräkning\n";

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel ->
                        channel.createEmbed(spec ->
                                spec.setColor(Color.of(90, 130, 180))
                                        .setTitle("Kommandorörelser")
                                        .setDescription(helpText))
                                .then());
    }
}
