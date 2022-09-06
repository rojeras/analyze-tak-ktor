# Teknologier att kolla

* MVC (redux-liknande modell)
* Freemarker
* htmx
* Pinegrowx

# Todo

* Låt tak/X göra redirect till tak/X/Summary
* Lägg in exception handling
* Red ut cooroutines och varför *plattforms* ibland inte laddas i tid.
* Addera loggning.
* Formatera alla sidor snyggare
* Se till att hela tiden lägga på testfall!
* Tag bort alla referenser till "article" - både routes och mallar
* Ändra group/domän till "se.skoview"

# Done

* Bygg vidare och hantera allt data från TAK-api
* Bygg ut summary.ftl till att inkludera all grundinformation
*

# Tänkt Funktion

* Startsidan
    * Innehåller enbart en dropdown där man väljer TP. Denna dropdown följer med på alla visade sidor.
    * Ev finns en Hem-knapp vid droppdownen som också följer med.
* Summary
    * Efter val av en TP så visas en sammanfattningssida för aktuell TP.
    * Det är i princip detsamma som summary i analyze-tak.py
        * Antal objekt
        * Antal felaktigheter
* Detaljsidor
    * Vid val av en rad i summary-sidan så visas en detaljsida över aktuell information. Varierar lite beroende på vad
      man tittar på.
* Felsidor
    * Motsvarar de felkontroller som pythonversionen skapar CSV för
    * Som användare ska man kunna markera fel som skall åtgärdas
    * En knapp möjliggör generering av JSON för markerade fel
* Länkar
    * Ev lägger vi på länkar från vissa objekt till hippo och/eller TkView

# Designfunderingar

## Källa och datalager

En utmaning i att implementera visualisering och städning är vilken källa för TAK-info som skall användas.
Python-prototypen accessade en TAK-db direkt.
KTOR-prototypen försökte istället gå visa TAK-api. Det har en del stora fördelar, men det har visat sig att det saknas
information vilket gör att det inte går att implementera alla kontroller.
Man skulle också kunna tänka sig TPDB. Men, det är begränsat till TAK-api och inte heller komplett.
Ytterligare en källa som föreslagits är export-filerna.
Ev får vi ge analyze-applikationen ett eget datalager som kan populeras på olika sätt.

# Övrigt

* Länk till SKLTP källkod: https://github.com/skltp 