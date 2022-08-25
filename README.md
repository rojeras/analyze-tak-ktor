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

# Övrigt
* Länk till SKLTP källkod: https://github.com/skltp 