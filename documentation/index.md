# Titanium Android Notifications module

## Description

Adds proper support for Google Cloud Messaging (GCM v2) as well as local/scheduled notifications on Android.

## Accessing the TiAndroidNotifications Module

To access this module from JavaScript, you would do the following:

    var tigcm = require("se.hyperlab.tigcm");

The tigcm variable is a reference to the Module object.

## Usage

### tigcm.registerForPushNotifications (options)

Should be called when launching your application. Provide an object containing three callback functions to handle push notifications:

#### onRegister
Called when we have successfully received a device token. Provides an object where token is stored as `token`

#### onMessage
Called when we receive a push notification (if app is in foreground), or when we click a notification that opens the app. Provides an object containing:

* `data` - The data sent with the push notification.
* `appInForeground` - True if the app is in foreground when we receive it.

#### onError
Called when we fail to receive a device token. Provides an object with the error message as `error`

## Example

```js
var gcm = require('se.hyperlab.tigcm');

function onRegister(e) {
  Ti.API.info('Got device token: ' + e.token);
  // some code that stores the token on the server side
}

function onMessage(e) {
  Ti.API.info('Received push notification with data: ' + JSON.stringify(e.data));
}

function onError(e) {
  Ti.API.error(e.error);
}

gcm.registerForPushNotifications({
  onRegister: onRegister,
  onMessage: onMessage,
  onError: onError,
});

```

## Notification properties

You can customize your notification using the following properties:

| Property | Description |
| -------- | ----------- |
| title* | The notification title, large font size |
| message* | Notification text, medium font size |
| data | Additional data, could be anything (that's serializable) |
| color | Color background of the icon, as hex |
| number | A number shown by the notification icon |

*You need to provide either title or message for the notification to be created.

## Resources

To style the notification you can also use the following resources:

### Notification icon image

To set the notification icon, create a drawable called `ic_stat_notification` and place in the `platform/android/res/drawable`-folders.

### Notification icon background color

To set the default color of the notification icon background in your app, create a color resource with the name `notification_icon_background`.

## Contributors

**Alfred Eriksson**
Web: http://hyperlab.se

**Jonatan Lundin**
Web: http://hyperlab.se
Twitter: @mr_lundis

## License

The MIT License (MIT)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
