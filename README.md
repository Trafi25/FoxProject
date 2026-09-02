# YellowFox Fahrtanalyse

Android-App zur Analyse der von YellowFox bereitgestellten Ortungsdaten.

Die App zeigt Startzeit, Endzeit, Fahrzeit und die mit der
Haversine-Formel berechnete Distanz an.

## Ausführen

1. Projekt mit Android Studio öffnen.
2. Gradle-Synchronisierung abwarten.
3. App auf einem Gerät oder Emulator ab API 24 starten.

## Gewählter Schwerpunkt: Fehlertoleranz und Unit-Tests

Einzelne ungültige API-Einträge werden übersprungen. Nach einem
erfolgreichen Abruf wird die JSON-Antwort atomar im internen
App-Speicher gespeichert. Schlägt ein späterer Abruf fehl, zeigt die
App automatisch die zuletzt gültigen Offline-Daten an. Ladezustand,
Fehlerzustand, Datenquelle und erneutes Laden werden in der UI
dargestellt. Unit-Tests prüfen Fahrzeit, Distanzsumme und Rundung.

## Annahmen

1. `time` ist UTC im Format `yyyy-MM-dd HH:mm:ss`.
2. Ortungspunkte können ungeordnet eintreffen.
3. Die Punkte werden vor der Berechnung chronologisch sortiert.
4. Einzelne ungültige Punkte werden ignoriert.
5. Mindestens ein gültiger Punkt ist erforderlich.
6. Die Erde wird für die Haversine-Berechnung als Kugel betrachtet.
7. Der verwendete mittlere Erdradius beträgt 6371,0088 km.
8. Die Distanz wird kaufmännisch auf zwei Nachkommastellen gerundet.