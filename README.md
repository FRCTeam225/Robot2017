# FireStorm

Software for TechFire's 2017 Robot FireStorm


**Notable software features**

* Android ADB interface for phone vision - in addition to the Java bridge, our `adb` binary is patched to listen on 0.0.0.0 for all port forwards. This allows us to view the HTTP MjpegServer running on the phone from the driver station
* Embedded Jetty HTTP Server inspired by 254's 2015 code for debugging & tuning. We use this interface to set autonomous modes pre-match, tune constants without deploying code, and graph PID loops in real-time through websocket
* Fully automated shooting - Robot looks up shooter RPM from vision distance through a table
* Vision latency compensation
* Trapezoidal motion profiles for autonomous driving

