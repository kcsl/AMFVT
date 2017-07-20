---
layout: default
---

## Overview
The Android Malicious Flow Visualization Toolbox empowers a human analyst to detect sophisticated zero-day malware. The toolbox incorporates visualization capabilities that help understand and analyze complex Android semantics used by an app. 

## Visualization Capabilities
The Android Malicious Flow Visualization Toolbox (AMFVT) project supports the following interactive visualization capabilities.

1. Android Subsystem Interaction Smart View: Visualize interactions of the app with various sub-systems of Android via data and control flow
2. Exceptional Flow Smart View: Visualize how exceptions thrown and caught by the app may amount to malicious behavior by the agency of Java’s exception flow semantics
3. Integrity Checker Smart View: Visualize how sensitive data may be modified by the app leading toan integrity breach

## Features
Interactive features supported by the visualization capabilities include:

1. Source correspondence: an analyst can click on a graph element from a smart view result to go to the corresponding source code location, and vice versa
2. Automatic recomputation on change of selection: an analyst can click to select a specific code artifact, and the smart view is automatically recomputed and displayed corresponding to the new selection
3. Smart views are composable: selecting a code artifact from the output of one smart view can be used as input to another smart view

<em>More visualization capabilities can be easily added to the toolbox. We are currently working to integrate an Intent Resolution Smart View to visualize flows between apps via Intents.</em>

## Getting Started
Ready to get started?

1. First [install](/AMFVT/install) the AMFVT plugin
2. Then check out the provided [tutorials](/AMFVT/tutorials) to jump start your analysis

## Source Code
Need additional resources? Grab a copy of the [source](https://github.com/kcsl/AMFVT).
