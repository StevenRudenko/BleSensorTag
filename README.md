BLE SensorTag
==============

**This is not an official application for TI SensorTag.** It is a sample project for Bluetooth Low Energy (BLE) usage on Android. There was [TI SensorTag][1] module used for demonstration.

There were SensorTag UUIDs linked in application to get information from BLE device sensors.
<table>
<tr>
  <td>
    <span style="float: right"><img src="http://www.ti.com/ww/en/wireless_connectivity/sensortag/images/xSensorTag.jpg.pagespeed.ic.s7Gd2yUw9a.jpg" /></span>
  </td>
  <td>
    <b>Sensors list:</b><br/>
    <ul>
    <li>IR temperature Sensor</li>
    <li>Accelerometer</li>
    <li>Gyroscope</li>
    <li>Magnetometer</li>
    <li>Humidity Sensor (doesn't work for SensorTag with FW v1.01)</li>
    <li>Pressure Sensor</li>
    <ul/>
  </td>
</tr>
</table>

Please read official [SensorTag User Guide][2] for more information about SensorTag development.

## Troubleshooting
If you have problems with TI Sensor Tag services detection or any sensor doesn't work you should try to update [firmware of SensorTag](http://processors.wiki.ti.com/index.php/SensorTag_Firmware)

Also you can take a look on [TI open source project for SensorTag](http://git.ti.com/sensortag-android).

-------------------------------------------------------------------------------

Developed By
============

* Steven Rudenko - <steven.rudenko@gmail.com>

License
=======
```
The MIT License (MIT)

Copyright (c) 2013 Steven Rudenko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

[1]: http://www.ti.com/sensortag
[2]: http://processors.wiki.ti.com/index.php/SensorTag_User_Guide
