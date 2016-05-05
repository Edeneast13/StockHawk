# Udacity P3: StockHawk

StockHawk is an app created by Udacity for the Android Developer Nanodegree. It displays a list of stocks to the user and
users can add or remove stocks as desired. This project required for features to be added, and current features to be improved
upon.

##Installation
Clone repository and open in Android Studio

Run project

##Added Features
Features added to complete this project were a widget that displays the stock information on the users home screen, 
and a detail activity that shows how the stock changes over time. This data is displayed on a line graph using
https://github.com/PhilJay/MPAndroidChart charting library. 

##Adjustments 
Other project requirements were to prepare the app to be translated into foreign languages, improve acceccebility options
with the screen reader, and fix a bug causing the app to crash when the user searched for a stock that did not exist.

Translation Preperation - adjusted margins and padding to allow for text to properlly format in all languages. 
Screen Reader Accessibility - added content description attribute to all interactive UI elements.
Stock Search Bug - implemented a test to see if stock exisited and caught unhandled exceptions. 
