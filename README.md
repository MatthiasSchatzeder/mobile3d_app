# mobile3d_app
This app was created as a part of a diploma thesis.



## Abstract

The purpose of the android app is to setup and control a mobile 3d printer (3d printer built into a suitcase).



## Functionality and used technologies

### Setup

The mobile 3d printer was created to be easily transported. Although a 3d printer normally has a fix place where it is setup once and then runs there. 

In order to make the setup as easy as possible the app has some helpful features. It uses Bluetooth and a BLE (bluetooth low energy) protocol to connect  with the [nymea networkmanager](https://github.com/nymea/nymea-networkmanager) deamon on the raspberry pi. With the BLE connection the user can setup a new wifi connection for the 3d printer and access the ip address of the printer in the network (in order to connect to the printers web interface). 



### Controls

The printer can also be controlled over the android application. Basic tasks like moving axis, heating up the nozzle and the bed are easy to accomplish within the app. [Socket IO](https://github.com/socketio/socket.io) is being used for controlling the 3d printer.

