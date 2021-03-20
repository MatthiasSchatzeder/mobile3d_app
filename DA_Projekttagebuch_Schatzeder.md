# Projekttagebuch Schatzeder



## 15.07.2019

| Stunden | Protokoll                       |
| :------ | ------------------------------- |
| 3       | Einlesen in Kotlin (Grundlagen) |

https://kotlinlang.org/docs/reference/basic-syntax.html

## 16.07.2019

| Stunden | Protokoll                   |
| ------- | --------------------------- |
| 1       | Einlesen in Kotlin          |
| 3       | Einlesen in Material design |

Wahl der Programmiersprache fällt auf Kotlin.

## 17.07.2019

| Stunden | Protokoll                   |
| ------- | --------------------------- |
| 5       | Einlesen in Material design |

## 18.07.2019

| Stunden | Protokoll                                       |
| ------- | ----------------------------------------------- |
| 2       | Einlesen in Material design(Android Components) |

https://material.io/develop/android/

## 19.07.2019

| Stunden | Protokoll                |
| ------- | ------------------------ |
| 4       | UI design / UI erstellen |

Erste Version des UI zum ansteuern der Achsen erstellt

Schwierigkeiten mit Material design (Gradle build error)

- Einbinden der Icons
- Einbinden von Material Components (in das Constraint Layout)



![](images\/control_axes_ui_v1.PNG?raw=true)222



## 22.07.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 1       | UI erstellen |
| 1       | Besprechung  |

Button design anpassen, Button - Icons zentrieren.

## 23.07.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 2       | UI erstellen |

Tests mit dem Tab Layout.



## 24.07.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 6       | UI erstellen |

Tests mit dem Tab Layout in Kombination mit dem ViewPager. Da ViewPager etwas veraltet ist kam es zu Kompatibilitätsproblemen (Gradle build error).

Weitere Tests mit dem ViewPager2 und dem Tab Layout.



## 29.07.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 4       | UI erstellen |

Erstellen des Tab Layout mit dem ViewPager2 (um das Tab Layout mit dem ViewPager2 zu verwenden muss ein Adapter verwendet werden [Quelle](https://stackoverflow.com/questions/55372259/how-to-use-tablayout-with-viewpager2-in-android) , Verwendung des bereits bestehenden TabLayoutMediator [GitHub Link](https://github.com/material-components/material-components-android/blob/master/lib/java/com/google/android/material/tabs/TabLayoutMediator.java)). Verwendung von Fragments anstatt Activities für die  einzelnen  Seiten.

![](images/control_panel_tab_view.PNG?raw=true)



## 30.07.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 1       | UI erstellen |

UI an verschiedene Display Auflösungen anpassen.



## 31.07.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 4       | UI erstellen |

UI zur Steuerung fertiggestellt. Testzugriffe auf UI Elemente erstellt.

![](images/control_panel_axis_v2.PNG?raw=true)

![](images/control_panel_tool_v1.PNG?raw=true)

![](images/control_panel_general_v1.PNG?raw=true)

## 06.08.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 1       | UI erstellen |

Anpassungen der Variablen / Ressource Id's an Namenskonventionen.



## 13.08.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 6       | UI erstellen |

Erstellen der seitlichen Navigation View.

Einbinden der Toolbar statt der Actionbar



## 14.08.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 2       | UI erstellen |

Einbinden des Action Buttons in der Toolbar



## 15.08.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 2       | UI erstellen |

Action Button Funktion und Navigationslogik der Navigation View implementieren

## 17.08.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 4       | UI erstellen |

Toolbar Navigation Button eingefügt und mit dem Navigation View verknüpft (Navigation Sidemap eingefügt)

![](images/navigation_menu_v1.PNG?raw=true)



## 30.08.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 5       | UI erstellen |

Zu Settings Page informieren.

(Erster versuch mit PreferenceScreen -> deprecated)

erstellen der Settings Activity und einer SwitchCompat 

![](images/settings_page_v1.PNG?raw=true)



## 13.09.2019

| Stunden | Protokoll   |
| ------- | ----------- |
| 2       | Besprechung |

## **21.09.2019

| Stunden | Protokoll               |
| ------- | ----------------------- |
| 3       | Pflichtenheft erstellen |

## 23.09.2019

| Stunden | Protokoll    |
| ------- | ------------ |
| 2       | UI erstellen |

kleine Anpassungen und Erweiterungen in den Control Tabs

## 27.09.2019

| Stunden | Protokoll   |
| ------- | ----------- |
| 2       | Informieren |

Einlesen in die Bluetooth LE Verbindung und das GATT Protokoll (für den nymea-networkmanager)

https://github.com/nymea/nymea-networkmanager



## 3.10.2019

| Stunden | Protokoll   |
| ------- | ----------- |
| 1       | Informieren |

Tests mit dem nymea-networkmanager



| Stunden | Protokoll    |
| ------- | ------------ |
| 1       | UI erstellen |

kleine UI Anpassungen und Code formatieren



## 16.10.2019

| Stunden | Protokoll          |
| ------- | ------------------ |
| 2       | BLE implementieren |

Bluetooth Adapter implementiert

Bluetooth enable Abfrage eingefügt



## 15.11.2019

| Stunden | Protokoll |
| ------- | --------- |
| 2       | BLE       |

Bluetooth Adapter ist deprecated -> einlesen in BluetoothLeScanner und ersetzen



## 18.11.2019

| Stunden | Protokoll |
| ------- | --------- |
| 1       | BLE       |

Scan Methode implementieren 

Probleme mit ScanCallback

## 19.11.2019

| Stunden | Protokoll |
| ------- | --------- |
| 2       | BLE       |

ScanCallback implementieren



## 20.11.2019

| Stunden | Protkoll |
| ------- | -------- |
| 1       | BLE      |

ListView und ListView-item layout erstellt. 

ListView Adapter implementiert -> mit ListView verbinden



## 21.11.2019

| Stunden | Protokoll |
| ------- | --------- |
| 2       | BLE       |

Tests mit BLE Scanner 

--> Probleme mit App crashes und Scan Methode funktioniert nicht

## 22.11.2019

| Stunden | Protokoll |
| ------- | --------- |
| 2       | BLE       |

Testen 

--> Probleme mit location permission



## 6.12.2019

| Stunden | Protokoll |
| ------- | --------- |
| 4       | BLE       |

Behebung von App crash Problemen bei Null Pointer Exception (verursacht durch BLE Scanner).

weitere Tests und Problembehandlungen mit der Scan Methode (Fehler: location permission nicht gegeben)



## 7.12.2019

| Stunden | Protokoll |
| ------- | --------- |
| 5       | BLE       |

Testen + Debugging, permission Abfragen ([Quelle](https://medium.com/google-developer-experts/exploring-android-q-location-permissions-64d312b0e2e1)) und GPS enable request implementiert. 

--> BLE Scanner funktioniert und gefundene Geräte werden aufgelistet



## 8.12.2019

| Stunden | Protokoll |
| ------- | --------- |
| 1       | BLE       |

Code Verbesserungen, Darstellung der gefundenen Geräte überarbeiten



## 9.12.2019

| Stunden | Protokoll  |
| ------- | ---------- |
| 2       | BLE - GATT |

informieren über die GATT Server Verbindung 



## 11.12.2019

| Stunden | Protokoll  |
| ------- | ---------- |
| 2       | BLE - GATT |

GATT services und characteristics erstellen -> UUID wurde nicht gefunden



## 14.12.2019

| Stunden | Protokoll  |
| ------- | ---------- |
| 2       | BLE - GATT |

Testen und Debuggen:

- der BLE Server(Raspberry) sendet keine Callbacks / Callbacks werden nicht empfangen



## 20.12.2019

| Stunden | Protokoll        |
| ------- | ---------------- |
| 1       | Plakat erstellen |
| 2       | BLE - GATT       |

Bluetooth Gatt callback Probleme.

- device.connect Aufruf ändern (mit TRANSPORT_LE Parameter)



## 28.12.2019

| Stunden | Protokoll  |
| ------- | ---------- |
| 1       | BLE - GATT |

Testen

- App erhält noch immer keine callbacks

## 30.12.2019

| Stunden | Protokoll  |
| ------- | ---------- |
| 5       | BLE - GATT |

- Gatt connection --> App kann sich mit dem BLE Server verbinden (Problem: autoconnect war true)

- Callbacks bei Verbindung werden erhalten



- Error handling bei Verbindungsabbruch implementiert 

## 31.12.2019

| Stunden | Protokoll  |
| ------- | ---------- |
| 1       | BLE - GATT |

Testen der Verbindung

- Verbindung zu Services und Characteristics implementiert 



## 2.1.2020

| Stunden | Protokoll  |
| ------- | ---------- |
| 4       | BLE - GATT |

- Error handling bei Benutzereingaben und noch nicht Vollkommen hergestellter Verbindung
- loading-Screen während des Verbindungsaufbaus eingefügt



## 4.1.2020

| Stunden | Protokoll  |
| ------- | ---------- |
| 2       | BLE - GATT |

- Datenaustausch Testen 
- -> erhalten von Statusmeldungen 



## 5.1.2020

| Stunden | Protokoll  |
| ------- | ---------- |
| 5       | BLE - GATT |

- notify flag für characteristics setzen und 0x0100 an den Deskriptor schreiben um notifiactions von den characteristics zu erhalten zu erhalten
- einlesen der Daten und zusammenfügen der 20 Byte Pakete -> JSON parsen
  - Verfügbaren Netzwerke werden empfangen



## 6.1.2020

| Stunden | Protokoll  |
| ------- | ---------- |
| 5       | BLE - GATT |

- methode zum schreiben der Daten an den Gatt Server
  - Strings in 20 byte Pakete splitten
- 2 text input Felder für SSID und Passwort eingefügt (Vorrübergehende Lösung) -> Verbindung zu beliebigen Netzwerk herstellen funktioniert
- Probleme beim Erhalten der IP Adresse des Raspberry 
- Raspberry Pi neu aufsetzen -> networkmanager neu installieren



## 7.1.2020

| Stunden | Protokoll  |
| ------- | ---------- |
| 3       | BLE - GATT |

- Funktion zum erhalten der aktuellen Verbindung
- Funktion zum erhalten der Netzwerke (von Raspi sichtbar)
  - Netzwerke in listview darstellen



- Probleme mit aufeinanderfolgenden wirtecharacteristic Operationen
  - führt zu ungültigen respong Nachrichten



## 9.1.2020

| Stunden | Protokoll  |
| ------- | ---------- |
| 3       | BLE - GATT |

- Versuche den daemon und den nymea-networkmanager auf raspbian buster aufzusetzen 
  - -> kein erfolg



- loading animation fix in der App
  - bei drücken des connect Buttons dauerte es ein paar Sekunden bis der die loading animation startet



## 10.1.2020

| Stunden | Protokoll |
| ------- | --------- |
| 1       | UI        |
| 1       | Socket IO |

- UI: Darstellung der Netzwerke anpassen
- UI fixes
- einlesen in Socket IO





## 11.1.2020

| Stunden | Protokoll                    |
| ------- | ---------------------------- |
| 4       | Socket IO, Backend Anbindung |

- HTTP Request an das Backend um den authentification-token zu erhalten.
- Socket IO Verbindung aufbauen / implementieren.



## 26.1.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 1       | Socket IO, UI |

- bei Start wird geprüft, ob das Backend erreichbar ist 
- Main Activity zeigt Verbindungsstatus an



## 28.1.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 2       | Socket IO, UI |

- Button onClickListener für alle Steuerknöpfe implementiert
- haptic-engine feedback implementiert



## 31.1.2020

| Stunden | Protokoll                   |
| ------- | --------------------------- |
| 2       | Dokumentation, code-cleanup |

- Code Dokumentation und Verbesserungen der BLE und GATT activity 



## 5.2.2020

| Stunden | Protokoll                   |
| ------- | --------------------------- |
| 1       | Dokumentation, code-cleanup |

- Code Dokumentation und Verbesserungen



## 7.2.2020

| Stunden | Protokoll |
| ------- | --------- |
| 1       | Socket IO |

- Setup Socket activity - connection Message (v1) implementiert 



## 11.2.2020

| Stunden | Protokoll         |
| ------- | ----------------- |
| 2       | Shared Preference |

- Shared Preference hinzugefügt um die eingegebene IP Adresse in einer lokalen Datei zu speichern 

  -> beim starten der App wird versucht die SocketIO Verbindung zu der zuletzt eingegebenen IP aufzubauen



## 14.2.2020

| Stunden | Protokoll |
| ------- | --------- |
| 4       | GATT, UI  |

- Coroutine in der GattActivity implementiert um UI-freezes zu verhindern
  - -> lade-Animation wird normal angezeigt 
- nachdem der connect Button gedrückt wurde wird die aktuelle Verbindung in einem alert dialog angezeigt



## 15.2.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 1       | Dokumentation |

- Theoretische Grundlagen zu Kotlin in der Dokumentation eingefügt



## 18.2.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 2       | Dokumentation |

- Theoretische Grundlagen zu Android und Gradle eingefügt



## 20.2.2020

| Stunden | Protokoll |
| ------- | --------- |
| 7       | Socket IO |

- Socket IO Verbindung aufbauen -> Verbesserungen mit User Interaktionen
- Nach Übermittlung einer Nachricht konnte kein ACK in der "emit" Funktion empfangen werden. -> Es muss auf das Callback Event gewartet werden (Verwendung der "on" Funktion).

- Probleme beim Anzeigen des aktuellen Verbindungsstatus, da bei einem Verbindungsverlust zum Backend ein disconnect Event empfangen wird und dann sollte die Statusanzeige 'nicht verbunden' anzeigen. Dies wurde allerdings auf einem auf einem background Thread ausgeführt und dieser Thread hat keinen Zugriff auf das UI. 

  -> mit einem Refresh Button wird die Verbindung überprüft und die UI Statusanzeige wird aktualisiert.



## 21.2.2020

| Stunden | Protokoll      |
| ------- | -------------- |
| 1       | Socket IO / UI |

- Anzeige (Snackbar) bei Verbindungsverlust

## 22.2.2020

| Stunden | Protokoll |
| ------- | --------- |
| 3       | Socket IO |

- Überarbeitung  der Verbindung -> kein Refresh Button wird mehr benötigt

  Lösung: die Funktion, die die Statusanzeige setzt muss in RunOnUIThread{} sein.

- Beispielaufruf eines Event (bewegen der Achsen) funktioniert



## 27.2.2020

| Stunden | Protokoll |
| ------- | --------- |
| 1       | Socket IO |

- Socket IO Funktionalität (event emits) für alle Control Buttons implementiert



## 28.2.2020

| Stunden | Protokoll |
| ------- | --------- |
| 2       | Socket IO |

- Verbesserung bei der Verbindung 
  - Auth Token wird nun auch gespeichert und immer wenn Verbindung zum Backend besteht ein neuer angefordert
  - -> somit kann wenn man die App startet und noch keine Verbindung zum Backend hat zuerst einen gespeicherten Auth Token verwenden um die Socket IO Verbindung aufzubauen und somit wird die Socket IO Verbindung automatisch bei Verfügbarkeit hergestellt



## 1.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 1       | Socket IO     |
| 2       | Dokumentation |

- Bugfixes beim Control Socket Verbindungsabbruch 
- Alert Dialog wird erst in der Main Activity angezeigt -> verhindert App crash



- Präsentation erstellt

## 2.3.2020

| Stunden | Protokoll |
| ------- | --------- |
| 2       | Socket IO |

- Druckerinfo wird in der App angezeigt und alle 5 Sekunden neu angefordert



## 7.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 4       | Dokumentation |



## 8.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 7       | Dokumentation |



## 10.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 2       | Dokumentation |



## 12.3.2020

| Stunden | Protokoll          |
| ------- | ------------------ |
| 3       | Dokumentation      |
| 2       | Bluetooth LE, GATT |

- Änderung des Wifi Setup UI
- eine eigene Activity zum eingeben des Wifi Passworts wurde implementiert



## 13.3.2020

| Stunden | Protokoll          |
| ------- | ------------------ |
| 3       | Push Notifications |

- FCM (Firebase Cloud Messaging) im Projekt implementiert
- Versuche mit eigenen Push Notifikationen



## 14.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 3       | Dokumentation |



## 19.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 3       | Dokumentation |



## 20.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 1       | Dokumentation |

- release auf Github hinzugefügt und in der Dokumentation eingebaut



## 22.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 4       | Dokumentation |

Korrekturlesen

## 23.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 2       | Dokumentation |

Verbessern von Fehlern



## 26.3.2020

| Stunden | Protokoll     |
| ------- | ------------- |
| 3       | Dokumentation |

Besprechung (auch mit Betreuungslehrern)

Formatierung an manchen Stellen überarbeitet