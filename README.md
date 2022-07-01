# Teknologier att kolla
* MVC (redux-liknande modell)
* Freemarker
* htmx
* Pinegrow

# Todo
* Bygg vidare och hantera allt data från TAK-api
* Bygg ut summary.ftl till att inkludera all grundinformation
* Se till att hela tiden lägga på testfall!
* Tag bort alla referenser till "article" - både routes och mallar
* Ändra group/domän till "se.skoview"

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
  * Vid val av en rad i summary-sidan så visas en detaljsida över aktuell information. Varierar lite beroende på vad man tittar på. 
* Länkar
  * Ev lägger vi på länkar från vissa objekt till hippo och/eller TkView