# A simple Swing Scala image gallery
## Loading images from Flickr
Note: This app was created in 2017 when I was a beginner to Scala. 
So don't expect a perfect code please. In 2021 I updated the project to build with IntelliJ IDEA 2021.2.2, updated the Scala version and all dependencies.

The app shows 10 recently uploaded images or 10 images fitting the search term. 
There is no pagination implemented so you're really limited to 10 images in total.
A click on an image opens the image URL in a browser.

## How to get started
1. [Flickr API key](https://www.flickr.com/services/api/misc.api_keys.html) has to be set as environment variable:
`FLICKR_API_KEY`
2. Launch app through IntelliJ IDEA