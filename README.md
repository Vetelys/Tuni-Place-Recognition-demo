# Visual localization demo application for Android

  Android app to take a picture, send it to a server and display an image or a text (debug) response from the server.
  Implemented as a part of my Bachelor's Thesis for Tampere University Signal Processing Research Laboratory.
  https://trepo.tuni.fi/handle/10024/137309
  
## Visual localization using query image:
  ![teaser](https://user-images.githubusercontent.com/48647132/152004525-7bdb08f1-6cf9-4234-af6e-0ce816eab3d8.png)
  
## Settings for server connection:
  ![settings](https://user-images.githubusercontent.com/48647132/152004651-1c9b70a0-670a-444d-a69d-2702b095559f.png)

## Prerequisites
  Features require an Android device with a camera and an Internet connection.
  Tested to be working on Android version 6.0+
  
## Build
  Open project on Android Studio.
  Create a virtual device or connect physical device through usb debugging.
  Select virtual or physical device from the drop-down menu.
  Click run.
  
## Server
  Server file provided in this repository is just a dummy version that does not do any visual localization,
  but is able to respond to the requests from the Android software. Feel free to modify it to fit for your
  localization purposes, for example using the toolbox found at https://github.com/cvg/Hierarchical-Localization.
