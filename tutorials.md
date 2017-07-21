---
layout: page
title: Tutorials
permalink: /tutorials/
---

If you haven't already, [install](/AMFVT/install) the AMFVT plugin into Eclipse.

## Install dependencies

For analyzing an Android source application, you will need the ability to compile an Android application in the Eclipse workspace. To do so you will need to install the Android ADT plugin. Google has discontinued support of Android development for Eclipse, but the eclipse update site is still active: `https://dl-ssl.google.com/android/eclipse/`. Once installed you should use the SDK Manager to download the appropriate Android API versions for your analysis task. If you plan to audit an Android APK (compiled binary) then you do not need this dependency.


## Using the toolbox

1. Import the Android app's source code (if available) or APK into the eclipse workspace.
2. Click `Atlas` &rarr; `Atas Smart View` and select the required smart view from the dropdown displayed at the bottom of the smart view panel. Multiple smart views can be opened at the same time by repeating this step. 
3. Select an input for the smart view by clicking on a source code artifact in the app or by selecting a graph element from the result of another smart view. 
4. The smart view instantly updates in response to the new selection (if the selection is applicable to the smart view, e.g., the integrity checker smart view responds only when the new selection is a data flow artifact such as variable, parameter, etc.; otherwise it remains unchanged).
5. The Android Subsystem Interaction smart view is by default configured to show interactions with respect to all subsystems in the Android. To restrict the smart view to show interactions only with respect to one of the 19 subsystems (see Table 1 in the paper for more details), click `Altas` &rarr; `Atlas Shell` to open the query interface. Then run the following query to change the subsystem configuration to IO Subsystem, for example. 

``com.kcsl.amfvt.ui.smart.SubsystemSmartView.SUBSYSTEM = "IO_SUBSYSTEM"``

After this, the smart view will show interactions of a selected artifact only with the IO Subsystem.
