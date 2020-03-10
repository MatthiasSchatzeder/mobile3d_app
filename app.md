# Nymea Network Manager

## Raspi

list all connections of the network manager:

`nmcli connection`



delete connection with name:

`nmcli connection delete <connection name>`



start debug log on raspi

`nymea-networkmanager --debug`



nymea-networkmanager config file

```conf
[General]
Mode=always
Timeout=60
AdvertiseName=BT WLAN setup
PlatformName=nymea-box
```



## App

transparent color: #80000000



### ToDo

- automatically connect to the raspberry's wifi







- physical feedback on button click need api 26
  - possible solution -> min api to 26 
  - 26 is the oldest **supported** version