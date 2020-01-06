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



log:

```
 I | BluetoothServer: Got command stream "{\n    \"c\": 5\n}\n"
 I | BluetoothServer: WirelessService: Execute get current connection.
 I | BluetoothServer: WirelessService: There is currently no access active accesspoint
 I | BluetoothServer: WirelessService: Start streaming response data: 53 bytes
 I | BluetoothServer: WirelessService: Finished streaming response data
```

